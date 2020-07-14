// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleClasses03

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Repository(val name: String) {
    init {
        require(name.isNotEmpty()) { "name cannot be empty" }
    }
}

fun main() {
    val data = Json.decodeFromString<Repository>("""
        {"name":""}
    """)
    println(data)
}
