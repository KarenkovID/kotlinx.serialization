// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleJson02

import kotlinx.serialization.*
import kotlinx.serialization.json.*

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
