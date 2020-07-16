// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleBuiltin07

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Repository(val name: String)

fun main() {
    val pair = 1 to Repository("kotlinx.serialization")
    println(Json.encodeToString(pair))
}  
