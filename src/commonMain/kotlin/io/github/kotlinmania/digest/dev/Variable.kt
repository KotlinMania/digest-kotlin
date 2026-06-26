// port-lint: source dev/variable.rs
package io.github.kotlinmania.digest.dev

import io.github.kotlinmania.digest.VariableOutput
import io.github.kotlinmania.digest.VariableOutputReset

/**
 * Test a variable-output resettable digest implementation.
 *
 * [create] must produce a fresh hasher for the given output size.
 * Returns a failure description on the first failing check, or `null` on success.
 * Tests whole-message, reset, and chunked ingestion.
 */
fun <D> variableResetTest(
    input: ByteArray,
    output: ByteArray,
    create: (Int) -> D,
    clone: (D) -> D,
): String?
where D : VariableOutputReset {
    val buf = ByteArray(output.size)
    val hasher = create(output.size)
    hasher.update(input)
    val hasher2 = clone(hasher)

    hasher.finalizeVariable(buf).getOrElse { return "finalize failed" }
    if (!buf.contentEquals(output)) return "whole message"
    buf.fill(0)

    hasher2.reset()
    hasher2.update(input)
    hasher2.finalizeVariableReset(buf).getOrElse { return "finalize_reset failed" }
    if (!buf.contentEquals(output)) return "whole message after reset"
    buf.fill(0)

    for (n in 1 until minOf(17, input.size)) {
        val h = create(output.size)
        var offset = 0
        while (offset < input.size) {
            val end = minOf(offset + n, input.size)
            val chunk = input.copyOfRange(offset, end)
            h.update(chunk)
            hasher2.update(chunk)
            offset = end
        }
        h.finalizeVariable(buf).getOrElse { return "finalize failed in chunks" }
        if (!buf.contentEquals(output)) return "message in chunks"
        buf.fill(0)

        hasher2.finalizeVariableReset(buf).getOrElse { return "finalize_reset failed in chunks" }
        if (!buf.contentEquals(output)) return "message in chunks"
        buf.fill(0)
    }
    return null
}

/**
 * Test a variable-output digest implementation.
 *
 * [create] must produce a fresh hasher for the given output size.
 * Returns a failure description on the first failing check, or `null` on success.
 * Tests whole-message and chunked ingestion.
 */
fun <D> variableTest(
    input: ByteArray,
    output: ByteArray,
    create: (Int) -> D,
): String?
where D : VariableOutput {
    val buf = ByteArray(output.size)

    val hasher = create(output.size)
    hasher.update(input)
    hasher.finalizeVariable(buf).getOrElse { return "finalize failed" }
    if (!buf.contentEquals(output)) return "whole message"
    buf.fill(0)

    for (n in 1 until minOf(17, input.size)) {
        val h = create(output.size)
        var offset = 0
        while (offset < input.size) {
            val end = minOf(offset + n, input.size)
            h.update(input.copyOfRange(offset, end))
            offset = end
        }
        h.finalizeVariable(buf).getOrElse { return "finalize failed in chunks" }
        if (!buf.contentEquals(output)) return "message in chunks"
        buf.fill(0)
    }
    return null
}
