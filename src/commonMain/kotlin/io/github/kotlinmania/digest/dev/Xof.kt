// port-lint: source dev/xof.rs
package io.github.kotlinmania.digest.dev

import io.github.kotlinmania.digest.ExtendableOutputReset

/**
 * Test a resettable extendable-output function (XOF) implementation.
 *
 * Returns a failure description on the first failing check, or `null` on success.
 * Tests whole-message, reset, and chunked ingestion.
 */
fun <D> xofResetTest(
    input: ByteArray,
    output: ByteArray,
    create: () -> D,
    clone: (D) -> D,
): String?
where D : ExtendableOutputReset {
    val buf = ByteArray(output.size)
    val hasher = create()
    hasher.update(input)
    val hasher2 = clone(hasher)

    hasher.finalizeXofInto(buf)
    if (!buf.contentEquals(output)) return "whole message"
    buf.fill(0)

    hasher2.reset()
    hasher2.update(input)
    hasher2.finalizeXofResetInto(buf)
    if (!buf.contentEquals(output)) return "whole message after reset"
    buf.fill(0)

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
        h.finalizeXofInto(buf)
        if (!buf.contentEquals(output)) return "message in chunks"
        buf.fill(0)

        hasher2.finalizeXofResetInto(buf)
        if (!buf.contentEquals(output)) return "message in chunks"
        buf.fill(0)
    }
    return null
}
