// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleClasses14

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Repository(val name: String, @SerialName("lang") val language: String)

fun main() {
    val data = Repository("kotlinx.serialization", "Kotlin")
    println(Json.encodeToString(data))
}
