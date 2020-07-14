// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleClasses02

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable 
class Repository private constructor(val owner: String, val name: String) {
    constructor(path: String) : this(
        owner = path.substringBefore('/'),    
        name = path.substringAfter('/')
    )                        

    val path: String
        get() = "$owner/$name"  
}

fun main() {
    println(Json.encodeToString(Repository("kotlin/kotlinx.serialization")))
}
