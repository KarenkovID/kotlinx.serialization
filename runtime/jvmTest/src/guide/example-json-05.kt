// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleJson05

import kotlinx.serialization.*
import kotlinx.serialization.json.*

val format = Json { coerceInputValues = true }

@Serializable 
data class Repository(val name: String, val language: String = "Kotlin")

fun main() {
    val data = format.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization","language":null}
    """)
    println(data)
}
