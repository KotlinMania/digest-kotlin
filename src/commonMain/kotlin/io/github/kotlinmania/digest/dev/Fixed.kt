// port-lint: source dev/fixed.rs
package io.github.kotlinmania.digest.dev

import io.github.kotlinmania.digest.FixedOutput
import io.github.kotlinmania.digest.FixedOutputReset

/**
 * Test a fixed-output resettable digest implementation.
 *
 * Returns a failure description on the first failing check, or `null` on success.
 * Tests whole-message, reset, and chunked ingestion.
 */
fun <D> fixedResetTest(
    input: ByteArray,
    output: ByteArray,
    create: () -> D,
    clone: (D) -> D,
): String?
where D : FixedOutputReset {
    val hasher = create()
    hasher.update(input)
    val hasher2 = clone(hasher)
    if (!hasher.finalizeFixed().contentEquals(output)) return "whole message"

    hasher2.reset()
    hasher2.update(input)
    if (!hasher2.finalizeFixedReset().contentEquals(output)) return "whole message after reset"

    for (n in 1 until minOf(17, input.size)) {
        val h = create()
        var offset = 0
        while (offset < input.size) {
            val end = minOf(offset + n, input.size)
            val chunk = input.copyOfRange(offset, end)
            h.update(chunk)
            hasher2.update(chunk)
            offset = end
        }
        if (!h.finalizeFixed().contentEquals(output)) return "message in chunks"
        if (!hasher2.finalizeFixedReset().contentEquals(output)) return "message in chunks"
    }
    return null
}

/**
 * Test a fixed-output digest implementation.
 *
 * Returns a failure description on the first failing check, or `null` on success.
 * Tests whole-message and chunked ingestion.
 */
fun <D> fixedTest(
    input: ByteArray,
    output: ByteArray,
    create: () -> D,
): String?
where D : FixedOutput {
    val hasher = create()
    hasher.update(input)
    if (!hasher.finalizeFixed().contentEquals(output)) return "whole message"

    for (n in 1 until minOf(17, input.size)) {
        val h = create()
        var offset = 0
        while (offset < input.size) {
            val end = minOf(offset + n, input.size)
            h.update(input.copyOfRange(offset, end))
            offset = end
        }
        if (!h.finalizeFixed().contentEquals(output)) return "message in chunks"
    }
    return null
}
