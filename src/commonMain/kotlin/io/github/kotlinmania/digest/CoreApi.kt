// port-lint: source src/core_api.rs
package io.github.kotlinmania.digest

import io.github.kotlinmania.digest.fmt.FmtResult
import io.github.kotlinmania.digest.fmt.Formatter

/** Types that can write their algorithm name. */
interface AlgorithmName {
    /** Write the algorithm name into [formatter]. */
    fun writeAlgName(formatter: Formatter): FmtResult
}

/** Block-buffer behavior marker. */
sealed interface BufferKind

/** Eager block-buffer behavior. */
data object Eager : BufferKind

/** Lazy block-buffer behavior. */
data object Lazy : BufferKind

/** Buffer type used by block-level algorithms. */
class Buffer<S>(
    private val blockSize: Int,
) {
    private val pending = mutableListOf<Byte>()

    /** Number of bytes currently stored in the buffer. */
    val size: Int
        get() = pending.size

    /** Reset this buffer to empty. */
    fun reset() {
        pending.clear()
    }

    /** Add [data] and call [consumer] for every complete block. */
    fun digestBlocks(data: ByteArray, consumer: (Block<S>) -> Unit) {
        for (byte in data) {
            pending += byte
            if (pending.size == blockSize) {
                consumer(pending.toByteArray())
                pending.clear()
            }
        }
    }
}

/** Types which consume data in blocks. */
interface UpdateCore : BlockSizeUser {
    /** Update state using the provided data blocks. */
    fun updateBlocks(blocks: List<Block<*>>)
}

/** Types which use [Buffer] functionality. */
interface BufferKindUser : BlockSizeUser {
    /** Buffer kind over which this type operates. */
    val bufferKind: BufferKind
}

/** Core trait for hash functions with fixed output size. */
interface FixedOutputCore : UpdateCore, BufferKindUser, OutputSizeUser {
    /** Finalize state using remaining buffered data and write the result. */
    fun finalizeFixedCore(buffer: Buffer<*>, out: Output<*>)
}

/** Core trait for hash functions with extendable output. */
interface ExtendableOutputCore : UpdateCore, BufferKindUser {
    /** Retrieve a XOF reader from core state and buffered data. */
    fun finalizeXofCore(buffer: Buffer<*>): XofReaderCore
}

/** Core reader trait for extendable-output function results. */
interface XofReaderCore : BlockSizeUser {
    /** Read the next XOF block. */
    fun readBlock(): Block<*>
}

/** Core trait for hash functions with variable output size. */
interface VariableOutputCore : UpdateCore, OutputSizeUser, BufferKindUser {
    /** Side which should be used in a truncated result. */
    val truncSide: TruncSide

    /** Finalize state and write the full hashing result into [out]. */
    fun finalizeVariableCore(buffer: Buffer<*>, out: Output<*>)
}

/** Side used when truncating variable-size output. */
enum class TruncSide {
    /** Truncate the left side. */
    Left,

    /** Truncate the right side. */
    Right,
}

/** A type which points to its block-level core representation. */
interface CoreProxy {
    /** Core type descriptor for Kotlin callers. */
    val core: Any
}

/** Wrapper around a block-level core value for fixed-output algorithms. */
class CoreWrapper<C>(
    private val core: C,
) : Update, FixedOutput, FixedOutputReset, Reset, OutputSizeUser, BlockSizeUser, HashMarker
where
    C : UpdateCore,
    C : FixedOutputCore,
    C : Reset {
    private val buffer = Buffer<CoreWrapper<C>>(core.blockSize)

    companion object {
        /** Create a new wrapper from [core]. */
        fun <C> fromCore(core: C): CoreWrapper<C>
        where
            C : UpdateCore,
            C : FixedOutputCore,
            C : Reset = CoreWrapper(core)
    }

    /** Decompose this wrapper into its core and buffer. */
    fun decompose(): Pair<C, Buffer<CoreWrapper<C>>> = Pair(core, buffer)

    override val outputSize: Int
        get() = core.outputSize

    override val blockSize: Int
        get() = core.blockSize

    override fun update(data: ByteArray) {
        buffer.digestBlocks(data) { block -> core.updateBlocks(listOf(block)) }
    }

    override fun finalizeInto(out: Output<*>) {
        core.finalizeFixedCore(buffer, out)
    }

    override fun finalizeIntoReset(out: Output<*>) {
        core.finalizeFixedCore(buffer, out)
        reset()
    }

    override fun reset() {
        buffer.reset()
        core.reset()
    }
}

/** Wrapper around [XofReaderCore] implementations. */
class XofReaderCoreWrapper(
    private val core: XofReaderCore,
) : XofReader {
    private val pending = ArrayDeque<Byte>()

    override fun read(buffer: ByteArray) {
        var offset = 0
        var remaining = buffer.size
        while (remaining > 0) {
            if (pending.isEmpty()) {
                for (b in core.readBlock()) pending.addLast(b)
            }
            val take = minOf(pending.size, remaining)
            repeat(take) { buffer[offset++] = pending.removeFirst() }
            remaining -= take
        }
    }
}

/**
 * Wrapper around [VariableOutputCore] with output size chosen at construction time.
 *
 * Implements [FixedOutputCore] by running the variable-output finalization and
 * truncating the result to [outputSize] according to [VariableOutputCore.truncSide].
 */
class CtVariableCoreWrapper(
    private val inner: VariableOutputCore,
    override val outputSize: Int,
) : UpdateCore, FixedOutputCore, BufferKindUser, OutputSizeUser, Reset {
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
        (inner as? Reset)?.reset()
    }
}

/**
 * Wrapper around [VariableOutputCore] with output size chosen at run time.
 *
 * Handles its own block buffer and implements the slice-based [VariableOutput]
 * and [VariableOutputReset] traits.
 */
class RtVariableCoreWrapper(
    private val core: VariableOutputCore,
    private val outputSizeValue: Int,
) : Update, VariableOutput, VariableOutputReset, Reset {
    private val buffer = Buffer<RtVariableCoreWrapper>(core.blockSize)

    override val maxOutputSize: Int get() = core.outputSize

    override fun outputSize(): Int = outputSizeValue

    override fun update(data: ByteArray) {
        buffer.digestBlocks(data) { block -> core.updateBlocks(listOf(block)) }
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
        buffer.reset()
        (core as? Reset)?.reset()
        return Result.success(Unit)
    }

    override fun reset() {
        buffer.reset()
        (core as? Reset)?.reset()
    }
}
