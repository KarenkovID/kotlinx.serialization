/*
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.protobuf.internal

import kotlinx.serialization.*
import kotlinx.serialization.protobuf.*

/**
 *  Source for all varint operations:
 *  https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/util/Varint.java
 */
internal fun decodeSignedVarintInt(input: ByteArrayInput): Int {
    val raw = input.readVarint32()
    val temp = raw shl 31 shr 31 xor raw shr 1
    // This extra step lets us deal with the largest signed values by treating
    // negative results from read unsigned methods as like unsigned values.
    // Must re-flip the top bit if the original read value had it set.
    return temp xor (raw and (1 shl 31))
}

internal fun decodeSignedVarintLong(input: ByteArrayInput): Long {
    val raw = input.readVarint64(false)
    val temp = raw shl 63 shr 63 xor raw shr 1
    // This extra step lets us deal with the largest signed values by treating
    // negative results from read unsigned methods as like unsigned values
    // Must re-flip the top bit if the original read value had it set.
    return temp xor (raw and (1L shl 63))

}

internal typealias ProtoDesc = Long
internal const val VARINT = 0
internal const val i64 = 1
internal const val SIZE_DELIMITED = 2
internal const val i32 = 5

private const val MASK = Int.MAX_VALUE.toLong() shl 32

@Suppress("NOTHING_TO_INLINE")
internal inline fun ProtoDesc(protoId: Int, type: ProtoIntegerType): ProtoDesc {
    return type.signature or protoId.toLong()
}

internal inline val ProtoDesc.protoId: Int get() = (this and Int.MAX_VALUE.toLong()).toInt()

internal val ProtoDesc.integerType: ProtoIntegerType
    get() = when(this and MASK) {
    ProtoIntegerType.DEFAULT.signature -> ProtoIntegerType.DEFAULT
    ProtoIntegerType.SIGNED.signature -> ProtoIntegerType.SIGNED
    else -> ProtoIntegerType.FIXED
}

internal fun SerialDescriptor.extractParameters(index: Int): ProtoDesc {
    val annotations = getElementAnnotations(index)
    var protoId: Int = index + 1
    var format: ProtoIntegerType = ProtoIntegerType.DEFAULT
    for (i in annotations.indices) { // Allocation-friendly loop
        val annotation = annotations[i]
        if (annotation is ProtoNumber) {
            protoId = annotation.number
        } else if (annotation is ProtoType) {
            format = annotation.type
        }
    }
    return ProtoDesc(protoId, format)
}

internal fun extractProtoId(descriptor: SerialDescriptor, index: Int, zeroBasedDefault: Boolean): Int {
    val annotations = descriptor.getElementAnnotations(index)
    for (i in annotations.indices) { // Allocation-friendly loop
        val annotation = annotations[i]
        if (annotation is ProtoNumber) {
            return annotation.number
        }
    }
    return if (zeroBasedDefault) index else index + 1
}

internal class ProtobufDecodingException(message: String) : SerializationException(message)

internal expect fun Int.reverseBytes(): Int
internal expect fun Long.reverseBytes(): Long
