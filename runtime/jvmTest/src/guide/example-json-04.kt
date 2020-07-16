// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleJson04

import kotlinx.serialization.*
import kotlinx.serialization.json.*

val format = Json { allowStructuredMapKeys = true }

@Serializable 
data class Repository(val name: String)
    
fun main() {             
    val map = mapOf(
        Repository("kotlinx.serialization") to "Serialization",
        Repository("kotlinx.coroutines") to "Coroutines"
    )
    println(format.encodeToString(map))
}
