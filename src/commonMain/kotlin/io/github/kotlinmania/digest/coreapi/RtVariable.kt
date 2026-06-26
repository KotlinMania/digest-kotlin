// port-lint: source core_api/rt_variable.rs
package io.github.kotlinmania.digest.coreapi

import io.github.kotlinmania.digest.InvalidBufferSize
import io.github.kotlinmania.digest.InvalidOutputSize
import io.github.kotlinmania.digest.Reset
import io.github.kotlinmania.digest.Update
import io.github.kotlinmania.digest.VariableOutput
import io.github.kotlinmania.digest.VariableOutputReset

/** Wrapper around [VariableOutputCore] which selects output size at run time. */
class RtVariableCoreWrapper(
    private val core: VariableOutputCore,
    private val outputSizeValue: Int,
) : Update,
    VariableOutput,
    VariableOutputReset,
    Reset {
    private val buffer = Buffer<RtVariableCoreWrapper>(core.blockSize)

    companion object {
        /** Create a runtime-size wrapper using [prototype]'s variable-output constructor. */
        fun new(prototype: VariableOutputCore, outputSize: Int): Result<RtVariableCoreWrapper> =
            prototype
                .new(outputSize)
                .fold(
                    onSuccess = { Result.success(RtVariableCoreWrapper(it, outputSize)) },
                    onFailure = { Result.failure(if (it is InvalidOutputSize) it else InvalidOutputSize()) },
                )
    }

    override val maxOutputSize: Int get() = core.outputSize

    override fun outputSize(): Int = outputSizeValue

    override fun update(data: ByteArray) {
        buffer.digestBlocks(data) { blocks -> core.updateBlocks(blocks) }
    }

    private fun finalizeDirty(out: ByteArray): Result<Unit> {
        if (out.size != outputSizeValue || out.size > maxOutputSize) {
            return Result.failure(InvalidBufferSize())
        }
        val fullRes = ByteArray(core.outputSize)
        core.finalizeVariableCore(buffer, fullRes)
        val n = out.size
        val m = fullRes.size - n
        when (core.truncSide) {
            TruncSide.Left -> fullRes.copyInto(out, 0, 0, n)
            TruncSide.Right -> fullRes.copyInto(out, 0, m, fullRes.size)
        }
        return Result.success(Unit)
    }

    override fun finalizeVariable(out: ByteArray): Result<Unit> = finalizeDirty(out)

    override fun finalizeVariableReset(out: ByteArray): Result<Unit> {
        finalizeDirty(out).onFailure { return Result.failure(it) }
        (core as? Reset)?.reset()
        buffer.reset()
        return Result.success(Unit)
    }

    override fun reset() {
        (core as? Reset)?.reset()
        buffer.reset()
    }
}
