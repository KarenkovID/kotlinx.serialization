// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleBasic03

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable 
data class Repository(val name: String, val language: String)

fun main() {
    val data = Json.decodeFromString<Repository>("""
        {"name":"kotlinx.serialization","language":"Kotlin"}
    """)
    println(data)
}
