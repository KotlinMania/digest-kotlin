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

/** Block-level core instance used by wrappers around concrete algorithms. */
interface CoreOf<D> : UpdateCore, FixedOutputCore, Reset, AlgorithmName {
    /** Clone this core state. */
    fun clone(): CoreOf<D>
}

/** Wrapper around a block-level core value. */
class CoreWrapper<C>(
    private val core: C,
) : Update, FixedOutput, FixedOutputReset, Reset, OutputSizeUser, HashMarker
where
    C : UpdateCore,
    C : FixedOutputCore,
    C : Reset {
    private val buffer = Buffer<CoreWrapper<C>>(core.blockSize)

    override val outputSize: Int
        get() = core.outputSize

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
