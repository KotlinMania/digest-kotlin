// port-lint: source src/dev/mac.rs
package io.github.kotlinmania.digest.dev

import io.github.kotlinmania.digest.Mac

/**
 * Test a MAC implementation against a single (key, input, tag) vector.
 *
 * [create] produces a fresh MAC initialized from the given key bytes.
 * [truncSide] controls which part of the tag to compare: `"left"` uses the
 * first `tag.size` bytes, `"right"` uses the last `tag.size` bytes, and
 * `""` (default) compares the full output.
 *
 * Returns a failure description on the first failing check, or `null` on success.
 */
fun macTest(
    key: ByteArray,
    input: ByteArray,
    tag: ByteArray,
    create: (ByteArray) -> Mac,
    truncSide: String = "",
): String? {
    val mac = create(key)
    mac.update(input)
    val result = mac.finalize().intoBytes()
    val n = tag.size
    val resultBytes = when (truncSide) {
        "left" -> result.copyOfRange(0, n)
        "right" -> result.copyOfRange(result.size - n, result.size)
        else -> result
    }
    if (!resultBytes.contentEquals(tag)) return "whole message"

    for (chunkSize in 1 until minOf(64, input.size)) {
        val m = create(key)
        var offset = 0
        while (offset < input.size) {
            val end = minOf(offset + chunkSize, input.size)
            m.update(input.copyOfRange(offset, end))
            offset = end
        }
        val res = when (truncSide) {
            "left" -> m.verifyTruncatedLeft(tag)
            "right" -> m.verifyTruncatedRight(tag)
            else -> m.verifySlice(tag)
        }
        if (res.isFailure) return "chunked message"
    }
    return null
}

/**
 * Test a resettable MAC implementation against a single (key, input, tag) vector.
 *
 * [create] produces a fresh MAC initialized from the given key bytes.
 * [truncSide] controls tag comparison side: `"left"`, `"right"`, or `""` (full).
 *
 * Returns a failure description on the first failing check, or `null` on success.
 */
fun resettableMacTest(
    key: ByteArray,
    input: ByteArray,
    tag: ByteArray,
    create: (ByteArray) -> Mac,
    truncSide: String = "",
): String? {
    val mac = create(key)
    mac.update(input)
    val result = mac.finalizeReset().intoBytes()
    val n = tag.size
    val resultBytes = when (truncSide) {
        "left" -> result.copyOfRange(0, n)
        "right" -> result.copyOfRange(result.size - n, result.size)
        else -> result
    }
    if (!resultBytes.contentEquals(tag)) return "whole message"

    mac.update(input)
    val afterReset = when (truncSide) {
        "left" -> mac.verifyTruncatedLeft(tag)
        "right" -> mac.verifyTruncatedRight(tag)
        else -> mac.verifySlice(tag)
    }
    if (afterReset.isFailure) return "after reset"

    for (chunkSize in 1 until minOf(64, input.size)) {
        val m = create(key)
        var offset = 0
        while (offset < input.size) {
            val end = minOf(offset + chunkSize, input.size)
            m.update(input.copyOfRange(offset, end))
            offset = end
        }
        val res = when (truncSide) {
            "left" -> m.verifyTruncatedLeft(tag)
            "right" -> m.verifyTruncatedRight(tag)
            else -> m.verifySlice(tag)
        }
        if (res.isFailure) return "chunked message"
    }
    return null
}
