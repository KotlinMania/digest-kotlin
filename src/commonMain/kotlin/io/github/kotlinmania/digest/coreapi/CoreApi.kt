// port-lint: source core_api.rs
package io.github.kotlinmania.digest.coreapi

import io.github.kotlinmania.digest.Block
import io.github.kotlinmania.digest.BlockSizeUser
import io.github.kotlinmania.digest.Output
import io.github.kotlinmania.digest.OutputSizeUser
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
    private val storage = ByteArray(blockSize)
    private var pos = 0

    /** Number of bytes currently stored in the buffer. */
    val size: Int
        get() = pos

    /** Current buffer cursor position. */
    fun getPos(): Int = pos

    /** Copy of bytes currently stored in the buffer. */
    fun remainingBytes(): ByteArray = storage.copyOf(pos)

    /** Reset this buffer to empty. */
    fun reset() {
        storage.fill(0)
        pos = 0
    }

    /** Add [data] and call [consumer] for every complete block. */
    fun digestBlocks(data: ByteArray, consumer: (List<Block<S>>) -> Unit) {
        var offset = 0
        val ready = mutableListOf<Block<S>>()
        if (pos != 0) {
            val take = minOf(data.size, blockSize - pos)
            data.copyInto(storage, pos, 0, take)
            pos += take
            offset += take
            if (pos == blockSize) {
                ready += storage.copyOf()
                pos = 0
            }
        }
        while (offset + blockSize <= data.size) {
            ready += data.copyOfRange(offset, offset + blockSize)
            offset += blockSize
        }
        if (ready.isNotEmpty()) {
            consumer(ready)
        }
        val remaining = data.size - offset
        if (remaining > 0) {
            data.copyInto(storage, 0, offset, data.size)
            pos = remaining
        }
    }

    /** Pad message with 0x80, zeros, and a 64-bit big-endian message length. */
    fun len64PaddingBe(dataLen: ULong, compress: (Block<S>) -> Unit) {
        digestPad(0x80.toByte(), dataLen.toBigEndianBytes(), compress)
    }

    /** Pad message with 0x80, zeros, and a 64-bit little-endian message length. */
    fun len64PaddingLe(dataLen: ULong, compress: (Block<S>) -> Unit) {
        digestPad(0x80.toByte(), dataLen.toLittleEndianBytes(), compress)
    }

    private fun digestPad(delimiter: Byte, suffix: ByteArray, compress: (Block<S>) -> Unit) {
        require(suffix.size < blockSize) { "padding suffix must fit inside one block" }
        val block = ByteArray(blockSize)
        storage.copyInto(block, 0, 0, pos)
        block[pos] = delimiter
        val suffixOffset = blockSize - suffix.size
        if (blockSize - pos - 1 < suffix.size) {
            compress(block)
            val tail = ByteArray(blockSize)
            suffix.copyInto(tail, suffixOffset)
            compress(tail)
        } else {
            suffix.copyInto(block, suffixOffset)
            compress(block)
        }
        reset()
    }
}

private fun ULong.toBigEndianBytes(): ByteArray {
    val bytes = ByteArray(ULong.SIZE_BYTES)
    for (index in bytes.indices) {
        val shift = (ULong.SIZE_BYTES - 1 - index) * Byte.SIZE_BITS
        bytes[index] = ((this shr shift) and 0xffu).toByte()
    }
    return bytes
}

private fun ULong.toLittleEndianBytes(): ByteArray {
    val bytes = ByteArray(ULong.SIZE_BYTES)
    for (index in bytes.indices) {
        val shift = index * Byte.SIZE_BITS
        bytes[index] = ((this shr shift) and 0xffu).toByte()
    }
    return bytes
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
interface FixedOutputCore :
    UpdateCore,
    BufferKindUser,
    OutputSizeUser {
    /** Finalize state using remaining buffered data and write the result. */
    fun finalizeFixedCore(buffer: Buffer<*>, out: Output<*>)
}

/** Core trait for hash functions with extendable output. */
interface ExtendableOutputCore :
    UpdateCore,
    BufferKindUser {
    /** Retrieve a XOF reader from core state and buffered data. */
    fun finalizeXofCore(buffer: Buffer<*>): XofReaderCore
}

/** Core reader trait for extendable-output function results. */
interface XofReaderCore : BlockSizeUser {
    /** Read the next XOF block. */
    fun readBlock(): Block<*>
}

/** Core trait for hash functions with variable output size. */
interface VariableOutputCore :
    UpdateCore,
    OutputSizeUser,
    BufferKindUser {
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
