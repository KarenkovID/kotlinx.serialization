// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleJson06

import kotlinx.serialization.*
import kotlinx.serialization.json.*

val format = Json { serializeSpecialFloatingPointValues = true }

@Serializable
class Data(
    val value: Double
)                     

fun main() {
    val data = Data(Double.NaN)
    println(format.encodeToString(data))
}
