// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleBuiltin08

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Repository(val name: String)

fun main() {
    val list = listOf(
        Repository("kotlinx.serialization"),
        Repository("kotlinx.coroutines")    
    )
    println(Json.encodeToString(list))
}  
