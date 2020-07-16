// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleJson01

import kotlinx.serialization.*
import kotlinx.serialization.json.*

val format = Json { prettyPrint = true }

@Serializable 
data class Repository(val name: String, val language: String)

fun main() {                                      
    val data = Repository("kotlinx.serialization", "Kotlin")
    println(format.encodeToString(data))
}
