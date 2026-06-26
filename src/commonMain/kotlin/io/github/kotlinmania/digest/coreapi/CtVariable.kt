// port-lint: source core_api/ct_variable.rs
package io.github.kotlinmania.digest.coreapi

import io.github.kotlinmania.digest.Block
import io.github.kotlinmania.digest.InvalidOutputSize
import io.github.kotlinmania.digest.Output
import io.github.kotlinmania.digest.OutputSizeUser
import io.github.kotlinmania.digest.Reset

/** Dummy type used with [CtVariableCoreWrapper] when the result has no known object identifier. */
data object NoOid

/** Wrapper around [VariableOutputCore] which selects output size at construction time. */
class CtVariableCoreWrapper(
    private var inner: VariableOutputCore,
    override val outputSize: Int,
) : UpdateCore,
    FixedOutputCore,
    BufferKindUser,
    OutputSizeUser,
    Reset {
    override val blockSize: Int get() = inner.blockSize
    override val bufferKind: BufferKind get() = inner.bufferKind

    override fun updateBlocks(blocks: List<Block<*>>) {
        inner.updateBlocks(blocks)
    }

    override fun finalizeFixedCore(buffer: Buffer<*>, out: Output<*>) {
        val fullRes = ByteArray(inner.outputSize)
        inner.finalizeVariableCore(buffer, fullRes)
        val n = out.size
        val m = fullRes.size - n
        when (inner.truncSide) {
            TruncSide.Left -> fullRes.copyInto(out, 0, 0, n)
            TruncSide.Right -> fullRes.copyInto(out, 0, m, fullRes.size)
        }
    }

    override fun reset() {
        inner
            .new(outputSize)
            .onSuccess { inner = it }
            .onFailure {
                if (it is InvalidOutputSize) {
                    (inner as? Reset)?.reset()
                } else {
                    throw it
                }
            }
    }
}
