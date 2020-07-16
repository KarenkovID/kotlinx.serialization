// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.exampleClasses13

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Repository(val name: String, val owner: User, val maintainer: User)

@Serializable
class User(val name: String)

fun main() {
    val owner = User("kotlin")
    val data = Repository("kotlinx.serialization", owner, owner)
    println(Json.encodeToString(data))
}
