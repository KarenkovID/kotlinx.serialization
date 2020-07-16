// This file was automatically generated from serialization-guide.md by Knit tool. Do not edit.
package kotlinx.serialization.example.test

import org.junit.Test
import kotlinx.knit.test.*

class GuideTest {
    @Test
    fun testExampleBasic01() {
        captureOutput("ExampleBasic01") { kotlinx.serialization.example.exampleBasic01.main() }.verifyOutputLinesStart(
            "Exception in thread \"main\" kotlinx.serialization.SerializationException: Serializer for class 'Repository' is not found. Mark the class as @Serializable or provide the serializer explicitly."
        )
    }

    @Test
    fun testExampleBasic02() {
        captureOutput("ExampleBasic02") { kotlinx.serialization.example.exampleBasic02.main() }.verifyOutputLines(
            "{\"name\":\"kotlinx.serialization\",\"language\":\"Kotlin\"}"
        )
    }

    @Test
    fun testExampleBasic03() {
        captureOutput("ExampleBasic03") { kotlinx.serialization.example.exampleBasic03.main() }.verifyOutputLines(
            "Repository(name=kotlinx.serialization, language=Kotlin)"
        )
    }

    @Test
    fun testExampleClasses01() {
        captureOutput("ExampleClasses01") { kotlinx.serialization.example.exampleClasses01.main() }.verifyOutputLines(
            "{\"name\":\"Kotlin\",\"stars\":9000}"
        )
    }

    @Test
    fun testExampleClasses02() {
        captureOutput("ExampleClasses02") { kotlinx.serialization.example.exampleClasses02.main() }.verifyOutputLines(
            "{\"owner\":\"kotlin\",\"name\":\"kotlinx.serialization\"}"
        )
    }

    @Test
    fun testExampleClasses03() {
        captureOutput("ExampleClasses03") { kotlinx.serialization.example.exampleClasses03.main() }.verifyOutputLinesStart(
            "Exception in thread \"main\" java.lang.IllegalArgumentException: name cannot be empty"
        )
    }

    @Test
    fun testExampleClasses04() {
        captureOutput("ExampleClasses04") { kotlinx.serialization.example.exampleClasses04.main() }.verifyOutputLinesStart(
            "Exception in thread \"main\" kotlinx.serialization.MissingFieldException: Field 'language' is required, but it was missing"
        )
    }

    @Test
    fun testExampleClasses05() {
        captureOutput("ExampleClasses05") { kotlinx.serialization.example.exampleClasses05.main() }.verifyOutputLines(
            "Repository(name=kotlinx.serialization, language=Kotlin)"
        )
    }

    @Test
    fun testExampleClasses07() {
        captureOutput("ExampleClasses07") { kotlinx.serialization.example.exampleClasses07.main() }.verifyOutputLinesStart(
            "Exception in thread \"main\" kotlinx.serialization.MissingFieldException: Field 'language' is required, but it was missing"
        )
    }

    @Test
    fun testExampleClasses08() {
        captureOutput("ExampleClasses08") { kotlinx.serialization.example.exampleClasses08.main() }.verifyOutputLinesStart(
            "Exception in thread \"main\" kotlinx.serialization.json.JsonDecodingException: Unexpected JSON token at offset 60: Encountered an unknown key 'language'. You can enable 'ignoreUnknownKeys' property in 'Json {}' builder to ignore unknown keys."
        )
    }

    @Test
    fun testExampleClasses09() {
        captureOutput("ExampleClasses09") { kotlinx.serialization.example.exampleClasses09.main() }.verifyOutputLines(
            "{\"name\":\"kotlinx.serialization\",\"language\":\"Kotlin\"}"
        )
    }

    @Test
    fun testExampleClasses10() {
        captureOutput("ExampleClasses10") { kotlinx.serialization.example.exampleClasses10.main() }.verifyOutputLines(
            "{\"name\":\"kotlinx.serialization\",\"renamedTo\":null}"
        )
    }

    @Test
    fun testExampleClasses11() {
        captureOutput("ExampleClasses11") { kotlinx.serialization.example.exampleClasses11.main() }.verifyOutputLinesStart(
            "Exception in thread \"main\" kotlinx.serialization.json.JsonDecodingException: Unexpected JSON token at offset 52: Expected string literal but 'null' literal was found. Use 'coerceInputValues = true' property in 'Json {}` builder to coerce nulls to default values."
        )
    }

    @Test
    fun testExampleClasses12() {
        captureOutput("ExampleClasses12") { kotlinx.serialization.example.exampleClasses12.main() }.verifyOutputLines(
            "{\"name\":\"kotlinx.serialization\",\"owner\":{\"name\":\"kotlin\"}}"
        )
    }

    @Test
    fun testExampleClasses13() {
        captureOutput("ExampleClasses13") { kotlinx.serialization.example.exampleClasses13.main() }.verifyOutputLines(
            "{\"name\":\"kotlinx.serialization\",\"owner\":{\"name\":\"kotlin\"},\"maintainer\":{\"name\":\"kotlin\"}}"
        )
    }

    @Test
    fun testExampleClasses14() {
        captureOutput("ExampleClasses14") { kotlinx.serialization.example.exampleClasses14.main() }.verifyOutputLines(
            "{\"name\":\"kotlinx.serialization\",\"lang\":\"Kotlin\"}"
        )
    }

    @Test
    fun testExampleBuiltin01() {
        captureOutput("ExampleBuiltin01") { kotlinx.serialization.example.exampleBuiltin01.main() }.verifyOutputLines(
            "{\"answer\":42,\"pi\":3.141592653589793}"
        )
    }

    @Test
    fun testExampleBuiltin02() {
        captureOutput("ExampleBuiltin02") { kotlinx.serialization.example.exampleBuiltin02.main() }.verifyOutputLinesStart(
            "Exception in thread \"main\" kotlinx.serialization.json.JsonEncodingException: 'NaN' is not a valid 'double' as per JSON specification. You can enable 'serializeSpecialFloatingPointValues' property in 'Json {}' builder to serialize such values."
        )
    }

    @Test
    fun testExampleBuiltin03() {
        captureOutput("ExampleBuiltin03") { kotlinx.serialization.example.exampleBuiltin03.main() }.verifyOutputLines(
            "{\"signature\":2067120338512882656}"
        )
    }

    @Test
    fun testExampleBuiltin04() {
        captureOutput("ExampleBuiltin04") { kotlinx.serialization.example.exampleBuiltin04.main() }.verifyOutputLines(
            "{\"signature\":\"2067120338512882656\"}"
        )
    }

    @Test
    fun testExampleBuiltin05() {
        captureOutput("ExampleBuiltin05") { kotlinx.serialization.example.exampleBuiltin05.main() }.verifyOutputLines(
            "{\"name\":\"kotlinx.serialization\",\"status\":\"SUPPORTED\"}"
        )
    }

    @Test
    fun testExampleBuiltin06() {
        captureOutput("ExampleBuiltin06") { kotlinx.serialization.example.exampleBuiltin06.main() }.verifyOutputLines(
            "{\"name\":\"kotlinx.serialization\",\"status\":\"maintained\"}"
        )
    }

    @Test
    fun testExampleBuiltin07() {
        captureOutput("ExampleBuiltin07") { kotlinx.serialization.example.exampleBuiltin07.main() }.verifyOutputLines(
            "{\"first\":1,\"second\":{\"name\":\"kotlinx.serialization\"}}"
        )
    }

    @Test
    fun testExampleBuiltin08() {
        captureOutput("ExampleBuiltin08") { kotlinx.serialization.example.exampleBuiltin08.main() }.verifyOutputLines(
            "[{\"name\":\"kotlinx.serialization\"},{\"name\":\"kotlinx.coroutines\"}]"
        )
    }

    @Test
    fun testExampleBuiltin09() {
        captureOutput("ExampleBuiltin09") { kotlinx.serialization.example.exampleBuiltin09.main() }.verifyOutputLines(
            "[{\"name\":\"kotlinx.serialization\"},{\"name\":\"kotlinx.coroutines\"}]"
        )
    }

    @Test
    fun testExampleBuiltin10() {
        captureOutput("ExampleBuiltin10") { kotlinx.serialization.example.exampleBuiltin10.main() }.verifyOutputLines(
            "Data(a=[42, 42], b=[42])"
        )
    }

    @Test
    fun testExampleBuiltin11() {
        captureOutput("ExampleBuiltin11") { kotlinx.serialization.example.exampleBuiltin11.main() }.verifyOutputLines(
            "{\"1\":{\"name\":\"kotlinx.serialization\"},\"2\":{\"name\":\"kotlinx.coroutines\"}}"
        )
    }

    @Test
    fun testExampleJson01() {
        captureOutput("ExampleJson01") { kotlinx.serialization.example.exampleJson01.main() }.verifyOutputLines(
            "{",
            "    \"name\": \"kotlinx.serialization\",",
            "    \"language\": \"Kotlin\"",
            "}"
        )
    }

    @Test
    fun testExampleJson02() {
        captureOutput("ExampleJson02") { kotlinx.serialization.example.exampleJson02.main() }.verifyOutputLines(
            "{\"name\":\"kotlinx.serialization\"}"
        )
    }

    @Test
    fun testExampleJson03() {
        captureOutput("ExampleJson03") { kotlinx.serialization.example.exampleJson03.main() }.verifyOutputLines(
            "Repository(name=kotlinx.serialization)"
        )
    }

    @Test
    fun testExampleJson04() {
        captureOutput("ExampleJson04") { kotlinx.serialization.example.exampleJson04.main() }.verifyOutputLines(
            "[{\"name\":\"kotlinx.serialization\"},\"Serialization\",{\"name\":\"kotlinx.coroutines\"},\"Coroutines\"]"
        )
    }

    @Test
    fun testExampleJson05() {
        captureOutput("ExampleJson05") { kotlinx.serialization.example.exampleJson05.main() }.verifyOutputLines(
            "Repository(name=kotlinx.serialization, language=Kotlin)"
        )
    }

    @Test
    fun testExampleJson06() {
        captureOutput("ExampleJson06") { kotlinx.serialization.example.exampleJson06.main() }.verifyOutputLines(
            "{\"value\":NaN}"
        )
    }
}
