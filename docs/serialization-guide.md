<!--- TEST_NAME GuideTest -->

# Kotlin Serialization Guide

**Table of contents**

<!--- TOC -->

* [Introduction](#introduction)
* [Basics](#basics)
  * [JSON encoding](#json-encoding)
  * [JSON decoding](#json-decoding)
* [Serializable classes](#serializable-classes)
  * [Backing fields are serialized](#backing-fields-are-serialized)
  * [Constructor properties requirement](#constructor-properties-requirement)
  * [Data validation](#data-validation)
  * [Optional properties](#optional-properties)
  * [Defaults are encoded](#defaults-are-encoded)
  * [Referenced objects](#referenced-objects)
  * [No compression of repeated references](#no-compression-of-repeated-references)
* [Custom JSON configuration](#custom-json-configuration)
  * [Pretty printing](#pretty-printing)
  * [Encode defaults](#encode-defaults)

<!--- END -->

## Introduction

Kotlin Serialization is cross-platform and multi-format framework for data serialization &mdash;
converting trees of objects to strings, byte arrays, or other _serial_ representations and back.
 
Kotlin Serialization is not just a library. It is a compiler plugin that is bundled with the Kotlin
compiler distribution itself. Build configuration is explained in [README.md](../README.md#setup). 
Once the project is set up, we can start serializing some classes.  

## Basics

This section shows the basic use of Kotlin Serialization and explains its core concepts.

To convert an object tree to a string or to a sequence of bytes it must come
through two mutually intertwined processes. In the first step an object is _serialized_ &mdash; 
it is converted into  a serial sequence of its constituting primitive values. This process is common for all 
data formats and its result depends on the object being serialized. A _serializer_ controls this process. 
The second step is called _encoding_ &mdash; it is the conversion of the corresponding sequence of primitives into 
the output format representation. An _encoder_ controls this process. 

The reverse process starts with parsing of the input format and _decoding_ of primitive values,
followed by _deserialization_ of the resulting stream into objects. We'll see details of this process later. 

For now, we'll start with [JSON](https://json.org) encoding.

<!--- INCLUDE .*-basic-.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
-->

### JSON encoding

The whole process of converting data into a specific format is called _encoding_. For JSON we'll be 
encoding data using the [Json.encodeToString] function, while serializes the object
that is passed as its parameter under the hood and converts it to a JSON string.

Let's start with a class describing source code repository and try to get its JSON representation:

```kotlin 
class Repository(val name: String, val language: String)

fun main() {
    val data = Repository("kotlinx.serialization", "Kotlin")
    println(Json.encodeToString(data))
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-basic-01.kt).

When we run this code we get the following exception:

```text 
Exception in thread "main" kotlinx.serialization.SerializationException: Serializer for class 'Repository' is not found. Mark the class as @Serializable or provide the serializer explicitly.
```

<!--- TEST LINES_START -->

Serializable classes have to be explicitly marked. Kotlin serialization does not use reflection, 
so you cannot accidentally deserialize a class which was not supposed to be serializable. Let's fix it by
adding the `@Serializable` annotation:

```kotlin
@Serializable 
class Repository(val name: String, val language: String)

fun main() {
    val data = Repository("kotlinx.serialization", "Kotlin")
    println(Json.encodeToString(data))
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-basic-02.kt).

The `@Serializable` annotation instructs Kotlin Serialization plugin to automatically generate and hook 
up a _serializer_ for this class. Now the output of the example is the corresponding JSON:

```text
{"name":"kotlinx.serialization","language":"Kotlin"}
```
 
<!--- TEST -->                                     

### JSON decoding

The reverse process is called _decoding_. To decode a JSON string into an object we'll 
use the [Json.decodeFromString] function. To specify which type we want to get as a result, 
we provide a type parameter to this function. 

As we'll see later, serialization works with different kinds of classes. 
Here we are making our class as `data class` not because it is required, but because
we want to print its contents to verify how it decodes:

```kotlin
@Serializable 
data class Repository(val name: String, val language: String)

fun main() {
    val data = Json.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization","language":"Kotlin"}
    """)
    println(data)
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-basic-03.kt).

We get back the object:

```text
Repository(name=kotlinx.serialization, language=Kotlin)
```
 
<!--- TEST -->          

## Serializable classes

This section goes into more details on how different `@Serializable` classes are handled.                           

<!--- INCLUDE .*-classes-.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
-->

### Backing fields are serialized

Only class's properties with backing fields are serialized, so properties with getter/setter that don't
have a backing field and delegated properties are not serialized, as the following example shows:

```kotlin        
@Serializable 
class Repository(
    // name is a property with backing fields -- serialized
    var name: String
) {
    var stars: Int = 0 // property with a backing field -- serialized
 
    val path: String // getter only, no backing field -- not serialized
        get() = "kotlin/$name"                                         

    var id by ::name // delegated property -- not serialized
}

fun main() {
    val data = Repository("Kotlin").apply { stars = 9000 }
    println(Json.encodeToString(data))
}
``` 

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-01.kt).

We can clearly see that only `owner` and `stars` properties are present in the JSON output:

```text 
{"name":"Kotlin","stars":9000}
```

<!--- TEST -->

### Constructor properties requirement

If we want to define the `Repository` class so that it takes a path string and then 
deconstructs into the corresponding properties, then we might be tempted to write something like this:

```kotlin
@Serializable 
class Repository(path: String) {
    val owner: String = path.substringBefore('/')    
    val name: String = path.substringAfter('/')    
}
```

<!--- CLEAR -->

This class will not compile, because `@Serializable` annotation requires that all parameters to class primary
constructor are properties. A simple workaround is to define a private primary constructor with class's
properties and turn the constructor we wanted into the secondary one:

```kotlin
@Serializable 
class Repository private constructor(val owner: String, val name: String) {
    constructor(path: String) : this(
        owner = path.substringBefore('/'),    
        name = path.substringAfter('/')
    )                        

    val path: String
        get() = "$owner/$name"  
}
```    

Serialization works with private primaty constructor and still serializes only backing fields. This code:

```kotlin
fun main() {
    println(Json.encodeToString(Repository("kotlin/kotlinx.serialization")))
}
```                                                       

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-02.kt).

Outputs:

```text 
{"owner":"kotlin","name":"kotlinx.serialization"}
```

<!--- TEST -->

### Data validation

Another case where you might want to introduce a primary constructor parameter without a property if when you
want to validate its value before storing it to a property. Replace it with a property in a primary constructor
and move validation to the `init { ... }` block:

```kotlin
@Serializable
class Repository(val name: String) {
    init {
        require(name.isNotEmpty()) { "name cannot be empty" }
    }
}
```

A deserialization works like a regular constructor in Kotlin and calls all `init` blocks, ensuring that you 
cannot get an invalid class as a result of deserialization:

```kotlin
fun main() {
    val data = Json.decodeFromString<Repository>("""
        {"name":""}
    """)
    println(data)
}
```    

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-03.kt).

Running this code produces the exception:

```text 
Exception in thread "main" java.lang.IllegalArgumentException: name cannot be empty
```

<!--- TEST LINES_START -->

### Optional properties

An object can be deserialized only when all its properties are present in the input.
For example, the following code:

```kotlin
@Serializable 
data class Repository(val name: String, val language: String)

fun main() {
    val data = Json.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization"}
    """)
    println(data)
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-04.kt).

Produces the following exception:

```text
Exception in thread "main" kotlinx.serialization.MissingFieldException: Field 'language' is required, but it was missing
```   

<!--- TEST LINES_START -->

This problem can be fixed by adding a default value to the property, which automatically makes its optional
for serialization:

```kotlin
@Serializable 
data class Repository(val name: String, val language: String = "Kotlin")

fun main() {
    val data = Json.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization"}
    """)
    println(data)
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-05.kt).

Produces the following output:

```text
Repository(name=kotlinx.serialization, language=Kotlin)
```   

<!--- TEST -->

### Defaults are encoded

Default values are still encoded by default:

```kotlin
@Serializable 
data class Repository(val name: String, val language: String = "Kotlin")

fun main() {
    val data = Repository("kotlinx.serialization")
    println(Json.encodeToString(data))
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-06.kt).

Produces the following output which has `language` property, even though its value is equal to the default one:

```text
{"name":"kotlinx.serialization","language":"Kotlin"}
```                 

See [Encode defaults][#encode-defaults] section on how this behavior can be configured for JSON. 

<!--- TEST -->

### Referenced objects

Serializable class can reference other classes in their serializable properties, 
which must also be marked as `@Serializable`:

```kotlin
@Serializable
class Repository(val name: String, val owner: User)

@Serializable
class User(val name: String)

fun main() {
    val owner = User("kotlin")
    val data = Repository("kotlinx.serialization", owner)
    println(Json.encodeToString(data))
}
```                                 

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-07.kt).

When encoded to JSON this layout results in a nested JSON object:

```text
{"name":"kotlinx.serialization","owner":{"name":"kotlin"}}
```                                 

<!--- TEST -->

### No compression of repeated references

Kotlin serialization if designed for encoding and decoding of plain data. It does not support reconstruction
of arbitrary object graphs with repeated object references. For example, if you try to serialize an object
that references the same `owner` instance twice:

```kotlin
@Serializable
class Repository(val name: String, val owner: User, val maintainer: User)

@Serializable
class User(val name: String)

fun main() {
    val owner = User("kotlin")
    val data = Repository("kotlinx.serialization", owner, owner)
    println(Json.encodeToString(data))
}
```                                 

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-08.kt).

You simply get its value encoded twice:

```text
{"name":"kotlinx.serialization","owner":{"name":"kotlin"},"maintainer":{"name":"kotlin"}}
```

<!--- TEST -->

## Custom JSON configuration

A custom JSON configuration can be specified by creating your own [Json] class instance and specifying its additional 
parameters via [JsonBuilder] notation. This section shows various features that [Json] supports.

<!--- INCLUDE .*-json-.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
-->

### Pretty printing

JSON can be configured to pretty-print the output:

```kotlin
@Serializable 
data class Repository(val name: String, val language: String)

fun main() {                                      
    val json = Json { prettyPrint = true }
    val data = Repository("kotlinx.serialization", "Kotlin")
    println(json.encodeToString(data))
}
```

> You can get the full code [here](../runtime/jvmTest/src/guide/example-json-01.kt).

It gives the following nice result:

```text 
{
    "name": "kotlinx.serialization",
    "language": "Kotlin"
}
``` 

<!--- TEST -->

### Encode defaults 

Default values of properties don't have to be encoded, because they will be reconstructed during encoding anyway.
This is especially useful for nullable properties with null defaults to avoid writing the corresponding 
null values:

```kotlin
@Serializable 
class Repository(
    val name: String, 
    val language: String = "Kotlin",
    val website: String? = null
)

fun main() {
    val json = Json { encodeDefaults = false }
    val data = Repository("kotlinx.serialization")
    println(json.encodeToString(data))
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-json-02.kt).

Produces the following output which has only `name` property:

```text
{"name":"kotlinx.serialization"}
```                 

<!--- TEST -->
