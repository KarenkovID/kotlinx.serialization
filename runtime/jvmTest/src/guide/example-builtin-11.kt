// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleBuiltin11

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Repository(val name: String)

fun main() {
    val map = mapOf(
        1 to Repository("kotlinx.serialization"),
        2 to Repository("kotlinx.coroutines")    
    )
    println(Json.encodeToString(map))
}  
