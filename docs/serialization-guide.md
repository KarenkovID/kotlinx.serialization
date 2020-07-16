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
  * [Optional property initializer call](#optional-property-initializer-call)
  * [Required properties](#required-properties)
  * [Transient properties](#transient-properties)
  * [Defaults are encoded](#defaults-are-encoded)
  * [Nullable properties](#nullable-properties)
  * [Type safety is enforced](#type-safety-is-enforced)
  * [Referenced objects](#referenced-objects)
  * [No compression of repeated references](#no-compression-of-repeated-references)
  * [Serial field names](#serial-field-names)
* [Builtin classes](#builtin-classes)
  * [Numbers](#numbers)
  * [Special floating-point values](#special-floating-point-values)
  * [Long numbers](#long-numbers)
  * [Long numbers as strings](#long-numbers-as-strings)
  * [Enum classes](#enum-classes)
  * [Serial names of enum entries](#serial-names-of-enum-entries)
  * [Pair and triple](#pair-and-triple)
  * [Lists](#lists)
  * [Sets and other collections](#sets-and-other-collections)
  * [Deserializing collections](#deserializing-collections)
  * [Maps](#maps)
* [Custom JSON configuration](#custom-json-configuration)
  * [Pretty printing](#pretty-printing)
  * [Encoding defaults](#encoding-defaults)
  * [Ignoring unknown keys](#ignoring-unknown-keys)
  * [Allowing structured map keys](#allowing-structured-map-keys)
  * [Coercing input values](#coercing-input-values)
  * [Serializing special floating point values](#serializing-special-floating-point-values)

<!--- END -->

## Introduction

Kotlin Serialization is cross-platform and multi-format framework for data serialization &mdash;
converting trees of objects to strings, byte arrays, or other _serial_ representations and back.
Kotlin Serialization fully supports and enforces Kotlin type system, making sure only valid 
objects can be deserialized. 
 
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
the output format representation. An _encoder_ controls this process. Whenever the distinction is not important
both encoding and serialization are used interchangeably. 

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
adding the [Serializable] annotation:

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

Serialization works with private primary constructor and still serializes only backing fields. This code:

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

### Optional property initializer call

When an optional properly is present in the input, the corresponding initializer for this
property is not even called. This is a feature designed for performance, so be careful not
to rely on side-effects in initializers. Consider this example:

 ```kotlin                                                                       
fun computeLanguage(): String {
    println("Computing")
    return "Kotlin"
}

 @Serializable 
 data class Repository(val name: String, val language: String = computeLanguage())
 
 fun main() {
     val data = Json.decodeFromString<Repository>("""
         {"name":"kotlinx.serialization","language":"Kotlin"}
     """)
     println(data)
 }
 ```                                  
 
 > You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-06.kt).
 
Because `language` property was specified in the input, we don't see "Computing" string printed:
 
 ```text
 Repository(name=kotlinx.serialization, language=Kotlin)
 ```   
 
 <!--- TEST -->

### Required properties

A property with a default value can be made required in a serial format with the [Required] annotation.
If we change the previous example by marking `language` property as `@Required`:

```kotlin
@Serializable 
data class Repository(val name: String, @Required val language: String = "Kotlin")

fun main() {
    val data = Json.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization"}
    """)
    println(data)
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-07.kt).

We'll get the following exception:

```text
Exception in thread "main" kotlinx.serialization.MissingFieldException: Field 'language' is required, but it was missing
```   

<!--- TEST LINES_START -->

### Transient properties

A property can be excluded from serialization by marking it with the [Transient] annotation 
(don't confuse it with [kotlin.jvm.Transient]). Such a property must have a default value:

```kotlin
@Serializable 
data class Repository(val name: String, @Transient val language: String = "Kotlin")

fun main() {
    val data = Json.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization","language":"Kotlin"}
    """)
    println(data)
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-08.kt).

Attempt to explicitly specify its value in the serial format, even if the specified
value is equal to the default one, will produce the following exception:

```text
Exception in thread "main" kotlinx.serialization.json.JsonDecodingException: Unexpected JSON token at offset 60: Encountered an unknown key 'language'. You can enable 'ignoreUnknownKeys' property in 'Json {}' builder to ignore unknown keys.
```   

<!--- TEST LINES_START -->

> 'ignoreUnknownKeys' feature is explained in the [Ignoring Unknown Keys section](#ignoring-unknown-keys) section.

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

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-09.kt).

Produces the following output which has `language` property, even though its value is equal to the default one:

```text
{"name":"kotlinx.serialization","language":"Kotlin"}
```                 

See [Encoding defaults][#encoding-defaults] section on how this behavior can be configured for JSON. 

<!--- TEST -->

### Nullable properties

Nullable properties are supported:

```kotlin
@Serializable
class Repository(val name: String, val renamedTo: String? = null)

fun main() {
    val data = Repository("kotlinx.serialization")
    println(Json.encodeToString(data))
}
```               

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-10.kt).

This example explicitly encodes `null` in JSON because [Defaults are encoded](#defaults-are-encoded):

```text
{"name":"kotlinx.serialization","renamedTo":null}
```     

<!--- TEST -->

### Type safety is enforced

Kotlin serialization strongly enforces type safety of the Kotlin programming language. 
In particular, let us try to decode a `null` in a JSON into a non-nullable property `language`:

```kotlin
@Serializable 
data class Repository(val name: String, val language: String = "Kotlin")

fun main() {
    val data = Json.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization","language":null}
    """)
    println(data)
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-11.kt).

Even though the `language` property has a default value, it is still an error to attempt to assign 
the `null` value to it:

```text
Exception in thread "main" kotlinx.serialization.json.JsonDecodingException: Unexpected JSON token at offset 52: Expected string literal but 'null' literal was found. Use 'coerceInputValues = true' property in 'Json {}` builder to coerce nulls to default values.
```                    

<!--- TEST LINES_START -->

However, it is often desired when decoding 3rd-party JSONs to coerce `null` to a default value.
The corresponding feature is explained in the [Coercing input values](#coercing-input-values) section. 

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

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-12.kt).

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

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-13.kt).

You simply get its value encoded twice:

```text
{"name":"kotlinx.serialization","owner":{"name":"kotlin"},"maintainer":{"name":"kotlin"}}
```

<!--- TEST -->

### Serial field names

The names of the properties that are used in encoded representation, JSON in our examples, are equal to 
their names in the source code by default. The name that is used for serialization is called a _serial name_ and
can be changed using the [SerialName] annotation. For example, we can have a `language` property in the source,
with an abbreviated serial name:

```kotlin
@Serializable
class Repository(val name: String, @SerialName("lang") val language: String)

fun main() {
    val data = Repository("kotlinx.serialization", "Kotlin")
    println(Json.encodeToString(data))
}
```                                 

> You can get the full code [here](../runtime/jvmTest/src/guide/example-classes-14.kt).

Now we see that an abbreviated name `lang` is used in JSON output:

```text
{"name":"kotlinx.serialization","lang":"Kotlin"}
```

<!--- TEST -->   

## Builtin classes

In addition to all the primitive types and strings, serialization for some classes from the Kotlin standard library, 
including the standard collections, is built into the Kotlin Serialization. This section covers them.

<!--- INCLUDE .*-builtin-.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
-->

### Numbers

All types of integer and floating point Kotlin numbers can be serialized. 

<!--- INCLUDE
import kotlin.math.*
-->

```kotlin
@Serializable
class Data(
    val answer: Int,
    val pi: Double
)                     

fun main() {
    val data = Data(42, PI)
    println(Json.encodeToString(data))
}
```                                   

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-01.kt).

Their natural representation in JSON is used:

```text
{"answer":42,"pi":3.141592653589793}
```

<!--- TEST -->

> Experimental unsigned numbers as well as other experimental inline classes are not supported by Kotlin serialization yet. 

### Special floating-point values

By default, special floating-point values like [Double.NaN] and infinities are not supported in JSON:

```kotlin
@Serializable
class Data(
    val value: Double
)                     

fun main() {
    val data = Data(Double.NaN)
    println(Json.encodeToString(data))
}
```                                   

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-02.kt).

Exception:

```text
Exception in thread "main" kotlinx.serialization.json.JsonEncodingException: 'NaN' is not a valid 'double' as per JSON specification. You can enable 'serializeSpecialFloatingPointValues' property in 'Json {}' builder to serialize such values.
```   

<!--- TEST LINES_START -->

> 'serializeSpecialFloatingPointValues' feature is explained in 
> the [Serializing special floating point values](#serializing-special-floating-point-values) section.

### Long numbers

Long integers are serializable, too:

```kotlin                
@Serializable
class Data(val signature: Long)

fun main() {
    val data = Data(0x1CAFE2FEED0BABE0)
    println(Json.encodeToString(data))
}
``` 

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-03.kt).

By default, they are serialized to JSON as numbers:

```text
{"signature":2067120338512882656}
```

<!--- TEST -->

### Long numbers as strings

The JSON output from the previous example will get decoded normally by Kotlin serialization running on Kotlin/JS.
However, if we try parse this JSON by native JavaScript methods, we'll get this:

```
JSON.parse("{\"signature\":2067120338512882656}")
▶ {signature: 2067120338512882700} 
```

The full range of Kotlin Long does not fit in the JavaScript number, so its precision gets lost in JavaScript.
A common workaround is to represent long numbers with full precision in JSON string type.
This approach is optionally supported by Kotlin serialization with [LongAsStringSerializer] that
can specified for a given Long property using [Serializable] annotation:

<!--- INCLUDE
import kotlinx.serialization.builtins.*
-->

```kotlin                
@Serializable
class Data(
    @Serializable(with=LongAsStringSerializer::class)
    val signature: Long
)

fun main() {
    val data = Data(0x1CAFE2FEED0BABE0)
    println(Json.encodeToString(data))
}
``` 

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-04.kt).

This JSON will get parsed natively by JavaScript without loss of precision:

```text
{"signature":"2067120338512882656"}
```

<!--- TEST -->

### Enum classes

All enum classes are serializable out of the box without having to mark them `@Serializable`,
as the following example shows:

```kotlin          
// @Serializable annotation is not need for a enum classes
enum class Status { SUPPORTED }
        
@Serializable
class Repository(val name: String, val status: Status) 

fun main() {
    val data = Repository("kotlinx.serialization", Status.SUPPORTED)
    println(Json.encodeToString(data))
}
```                                        

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-05.kt).

In JSON enum gets encoded as a string:

```text
{"name":"kotlinx.serialization","status":"SUPPORTED"}
```   

<!--- TEST -->

### Serial names of enum entries

Serial names of enum entries can be customized with [SerialName] annotation just like 
it was shown for properties in the [Serial field names](#serial-field-names) section.
However, in this case the whole enum class must be marked with [Serializable] annotation:

```kotlin
@Serializable // required because of @SerialName
enum class Status { @SerialName("maintained") SUPPORTED }
        
@Serializable
class Repository(val name: String, val status: Status) 

fun main() {
    val data = Repository("kotlinx.serialization", Status.SUPPORTED)
    println(Json.encodeToString(data))
}
```                                        

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-06.kt).

In JSON enum gets encoded as a string:

```text
{"name":"kotlinx.serialization","status":"maintained"}
```   

<!--- TEST -->

### Pair and triple

Simple data classes [Pair] and [Triple] from the Kotlin standard library are serializable:

```kotlin
@Serializable
class Repository(val name: String)

fun main() {
    val pair = 1 to Repository("kotlinx.serialization")
    println(Json.encodeToString(pair))
}  
```                                

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-07.kt).

```text
{"first":1,"second":{"name":"kotlinx.serialization"}}
```

<!--- TEST -->
 
> Not all classes from the standard library are serializable. In particular ranges and [Regex] class
> are not serializable at the moment. Support for their serialization may be added in the future.  

### Lists 

A [List] of serializable classes can be serialized:

```kotlin
@Serializable
class Repository(val name: String)

fun main() {
    val list = listOf(
        Repository("kotlinx.serialization"),
        Repository("kotlinx.coroutines")    
    )
    println(Json.encodeToString(list))
}  
```

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-08.kt).

The result is represented as a list in JSON:

```text
[{"name":"kotlinx.serialization"},{"name":"kotlinx.coroutines"}]
```     

<!--- TEST -->

### Sets and other collections

Other collections, like a [Set], are also serializable:

Any [List] of serializable classes can be serialized:

```kotlin
@Serializable
class Repository(val name: String)

fun main() {
    val set = setOf(
        Repository("kotlinx.serialization"),
        Repository("kotlinx.coroutines")    
    )
    println(Json.encodeToString(set))
}  
```

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-09.kt).

It is also represented as a list in JSON, like all other collections.

```text
[{"name":"kotlinx.serialization"},{"name":"kotlinx.coroutines"}]
```     

<!--- TEST -->

### Deserializing collections

During deserialization the type of the resulting object is determined by the static type that was specified
in the source code &mdash; either as the type of the property or as the type parameter of the decoding function.
The following example shows how the same JSON list of integers is deserialized into two properties of
different Kotlin types:

```kotlin             
@Serializable
data class Data(
    val a: List<Int>,
    val b: Set<Int>
)
     
fun main() {
    val data = Json.decodeFromString<Data>("""
        {
            "a": [42, 42],
            "b": [42, 42]
        }
    """)
    println(data)
}
```    

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-10.kt).

Because `data.b` property is a [Set], the duplicate values from it had disappeared:

```text
Data(a=[42, 42], b=[42])
```

<!--- TEST -->

### Maps

A [Map] with a primitive key or a enum key and an arbitrary serializable value can be serialized:

```kotlin
@Serializable
class Repository(val name: String)

fun main() {
    val map = mapOf(
        1 to Repository("kotlinx.serialization"),
        2 to Repository("kotlinx.coroutines")    
    )
    println(Json.encodeToString(map))
}  
```                                

> You can get the full code [here](../runtime/jvmTest/src/guide/example-builtin-11.kt).

In JSON it gets represented as an object. In JSON object keys are always strings, so keys are encoded as strings
even if they are numbers in Kotlin, as we can see below:

```text
{"1":{"name":"kotlinx.serialization"},"2":{"name":"kotlinx.coroutines"}}
```

<!--- TEST -->

It is a JSON-specific limitation that keys cannot be composite. 
It can be lifted as shown in [Allowing structured map keys](#allowing-structured-map-keys) section. 
 
## Custom JSON configuration

By default, [Json] implementation is quite strict with respect to invalid inputs, enforces Kotlin type safety, and
restricts Kotlin values that can be serialized so that the resulting JSON representations are standard.
Many non-standard JSON features are supported by creating a custom instance of a JSON _format_.    

JSON format configuration can be specified by creating your own [Json] class instance using an existing 
instance, such as a default `Json` object, and a [Json()] builder function. Additional parameters
are specified in a block via [JsonBuilder] DSL. The resulting `Json` format instance is immutable and thread-safe; 
it can be simply stored in a top-level property. 

This section shows various configuration features that [Json] supports.

<!--- INCLUDE .*-json-.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
-->

### Pretty printing

JSON can be configured to pretty print the output by setting the [prettyPrint][JsonBuilder.prettyPrint] property:

```kotlin
val format = Json { prettyPrint = true }

@Serializable 
data class Repository(val name: String, val language: String)

fun main() {                                      
    val data = Repository("kotlinx.serialization", "Kotlin")
    println(format.encodeToString(data))
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

### Encoding defaults 

Default values of properties don't have to be encoded, because they will be reconstructed during encoding anyway.
It can be configured by [encodeDefaults][JsonBuilder.encodeDefaults] property.
This is especially useful for nullable properties with null defaults to avoid writing the corresponding 
null values:

```kotlin
val format = Json { encodeDefaults = false }

@Serializable 
class Repository(
    val name: String, 
    val language: String = "Kotlin",
    val website: String? = null
)           

fun main() {
    val data = Repository("kotlinx.serialization")
    println(format.encodeToString(data))
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-json-02.kt).

Produces the following output which has only `name` property:

```text
{"name":"kotlinx.serialization"}
```                 

<!--- TEST -->

### Ignoring unknown keys

JSON format is often used to read output of 3rd-party services or in otherwise highly-dynamic environment where
new properties could be added as a part of API evolution. By default, unknown key during deserialization produces an error. 
This behavior can be configured with [ignoreUnknownKeys][JsonBuilder.ignoreUnknownKeys] property:

```kotlin
val format = Json { ignoreUnknownKeys = true }

@Serializable 
data class Repository(val name: String)
    
fun main() {             
    val data = format.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization","language":"Kotlin"}
    """)
    println(data)
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-json-03.kt).

It decodes the object, despite the fact that it is missing a `language` property:
 
```text
Repository(name=kotlinx.serialization)
``` 

<!--- TEST -->

### Allowing structured map keys

JSON format does not natively support the concept of a map with a structured key. Key in JSON objects
are strings and can be used to represent only primitives or enums by default.
A non-standard support for structured key can be enabled with [allowStructuredMapKeys][JsonBuilder.allowStructuredMapKeys] property:

```kotlin
val format = Json { allowStructuredMapKeys = true }

@Serializable 
data class Repository(val name: String)
    
fun main() {             
    val map = mapOf(
        Repository("kotlinx.serialization") to "Serialization",
        Repository("kotlinx.coroutines") to "Coroutines"
    )
    println(format.encodeToString(map))
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-json-04.kt).

The map with structured keys gets represented as `[key1, value1, key2, value2,...]` array:
 
```text
[{"name":"kotlinx.serialization"},"Serialization",{"name":"kotlinx.coroutines"},"Coroutines"]
``` 

<!--- TEST -->

### Coercing input values

JSON formats that are encountered in the wild are often very flexible in terms of types and evolve quickly.
This can lead to exceptions during decoding when the actual values do not match the expected values. 
By default [Json] implementation is strict with respect to input types as was demonstrated in
the [Type safety is enforced](#type-safety-is-enforced) section. It can be somewhat relaxed using
[coerceInputValues][JsonBuilder.coerceInputValues] property. 

This property only has an effect during decoding. It treats a limited subset of invalid input values as if the
corresponding property was missing and uses a default value of the corresponding property instead.
Current list of supported invalid values is:

* `null` inputs for a non-nullable types.
* Unknown values for enums.

> This list may be expanded in the future, so that [Json] instance configured with this property becomes even more
> permissive to invalid value in the input, replacing them with defaults.    

Taking the example from the [Type safety is enforced](#type-safety-is-enforced) section:

```kotlin
val format = Json { coerceInputValues = true }

@Serializable 
data class Repository(val name: String, val language: String = "Kotlin")

fun main() {
    val data = format.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization","language":null}
    """)
    println(data)
}
```                                  

> You can get the full code [here](../runtime/jvmTest/src/guide/example-json-05.kt).

We see that invalid `null` value for the `language` property was coerced into the default value:

```text
Repository(name=kotlinx.serialization, language=Kotlin)
```    

<!--- TEST -->                

### Serializing special floating point values

As we saw in the [Special floating point values](#special-floating-point-values) section, 
by default, special floating-point values like [Double.NaN] and infinities are not supported in JSON.
They can be enabled using [serializeSpecialFloatingPointValues][JsonBuilder.serializeSpecialFloatingPointValues]
property:

```kotlin       
val format = Json { serializeSpecialFloatingPointValues = true }

@Serializable
class Data(
    val value: Double
)                     

fun main() {
    val data = Data(Double.NaN)
    println(format.encodeToString(data))
}
```                                   

> You can get the full code [here](../runtime/jvmTest/src/guide/example-json-06.kt).

This example produces the following non-stardard JSON output, yet it is a widely used encoding for
special values in JVM world:

```text
{"value":NaN}
```   

<!--- TEST -->

<!-- stdlib references -->
[kotlin.jvm.Transient]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-transient/
[Double.NaN]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/-na-n.html
[Pair]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/ 
[Triple]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-triple/ 
[Regex]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/
[List]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/ 
[Set]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/ 
[Map]: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/ 

<!--- MODULE /kotlinx-serialization -->
<!--- INDEX kotlinx.serialization -->
[Serializable]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization/-serializable/index.html
[Required]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization/-required/index.html
[Transient]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization/-transient/index.html
[SerialName]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization/-serial-name/index.html
<!--- INDEX kotlinx.serialization.builtins -->
[LongAsStringSerializer]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.builtins/-long-as-string-serializer/index.html
<!--- INDEX kotlinx.serialization.json -->
[Json.encodeToString]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json/encode-to-string.html
[Json.decodeFromString]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json/decode-from-string.html
[Json]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json/index.html
[Json()]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json.html
[JsonBuilder]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json-builder/index.html
[JsonBuilder.prettyPrint]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json-builder/pretty-print.html
[JsonBuilder.encodeDefaults]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json-builder/encode-defaults.html
[JsonBuilder.ignoreUnknownKeys]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json-builder/ignore-unknown-keys.html
[JsonBuilder.allowStructuredMapKeys]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json-builder/allow-structured-map-keys.html
[JsonBuilder.coerceInputValues]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json-builder/coerce-input-values.html
[JsonBuilder.serializeSpecialFloatingPointValues]: https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization/kotlinx.serialization.json/-json-builder/serialize-special-floating-point-values.html
<!--- END -->

