// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleClasses09

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable 
data class Repository(val name: String, val language: String = "Kotlin")

fun main() {
    val data = Repository("kotlinx.serialization")
    println(Json.encodeToString(data))
}
