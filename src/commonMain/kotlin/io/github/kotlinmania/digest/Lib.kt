// port-lint: source src/lib.rs
package io.github.kotlinmania.digest

/**
 * Traits which describe cryptographic hash functions and message
 * authentication algorithms.
 *
 * The Rust crate organizes this surface into high-level convenience traits,
 * mid-level algorithm traits, marker traits, and lower-level block traits.
 * Kotlin does not have Rust associated types, so byte arrays carry fixed-size
 * outputs, blocks, and keys while interfaces expose the corresponding sizes.
 */

/** Fixed-size output bytes produced by an algorithm. */
typealias Output<S> = ByteArray

/** Fixed-size key bytes accepted by a keyed algorithm. */
typealias Key<S> = ByteArray

/** Fixed-size input block bytes consumed by a block-level algorithm. */
typealias Block<S> = ByteArray

/** Returns this key as a byte slice. */
fun ByteArray.asSlice(): ByteArray = this

/** Types which consume data with byte granularity. */
interface Update {
    /** Update state using the provided data. */
    fun update(data: ByteArray)
}

/** Types which can reset themselves to their initial state. */
interface Reset {
    /** Reset the value to its initial state. */
    fun reset()
}

/** Types with a fixed output size. */
interface OutputSizeUser {
    /** Output size in bytes. */
    val outputSize: Int
}

/** Types with a fixed input block size. */
interface BlockSizeUser {
    /** Block size in bytes. */
    val blockSize: Int
}

/** Types with a fixed key size. */
interface KeySizeUser {
    /** Key size in bytes. */
    val keySize: Int
}

/** Types initialized from a key. */
interface KeyInit<S> {
    /** Create a value from a fixed-size key. */
    fun new(key: Key<S>): S

    /** Create a value from a variable-size key. */
    fun newFromSlice(key: ByteArray): Result<S>
}

/** Trait for hash functions with fixed-size output. */
interface FixedOutput : Update, OutputSizeUser {
    /** Consume this value and write the result into the provided output. */
    fun finalizeInto(out: Output<*>)

    /** Retrieve the result. */
    fun finalizeFixed(): Output<*> {
        val out = ByteArray(outputSize)
        finalizeInto(out)
        return out
    }
}

/** Trait for hash functions with fixed-size output able to reset themselves. */
interface FixedOutputReset : FixedOutput, Reset {
    /** Write the result into the provided output and reset state. */
    fun finalizeIntoReset(out: Output<*>)

    /** Retrieve the result and reset state. */
    fun finalizeFixedReset(): Output<*> {
        val out = ByteArray(outputSize)
        finalizeIntoReset(out)
        return out
    }
}

/** Reader types used to extract extendable output from a XOF result. */
interface XofReader {
    /** Read output into [buffer]. */
    fun read(buffer: ByteArray)
}

/** Trait for hash functions with extendable output. */
interface ExtendableOutput : Update {
    /** Retrieve a XOF reader and consume the hasher state. */
    fun finalizeXof(): XofReader

    /** Finalize XOF output into [out]. */
    fun finalizeXofInto(out: ByteArray) {
        finalizeXof().read(out)
    }
}

/** Trait for hash functions with extendable output able to reset themselves. */
interface ExtendableOutputReset : ExtendableOutput, Reset {
    /** Retrieve a XOF reader and reset the hasher state. */
    fun finalizeXofReset(): XofReader

    /** Finalize XOF output into [out] and reset the hasher state. */
    fun finalizeXofResetInto(out: ByteArray) {
        finalizeXofReset().read(out)
    }
}

/** Trait for hash functions with variable-size output. */
interface VariableOutput : Update {
    /** Maximum size of the output hash. */
    val maxOutputSize: Int

    /** Get the configured output size of this hasher. */
    fun outputSize(): Int

    /** Write the result into [out]. */
    fun finalizeVariable(out: ByteArray): Result<Unit>
}

/** Trait for variable-size output hash functions able to reset themselves. */
interface VariableOutputReset : VariableOutput, Reset {
    /** Write the result into [out] and reset state. */
    fun finalizeVariableReset(out: ByteArray): Result<Unit>
}

/** Error used in variable hash traits. */
class InvalidOutputSize : IllegalArgumentException("invalid output size")

/** Buffer length is not equal to hash output size. */
class InvalidBufferSize : IllegalArgumentException("invalid buffer length")

/** Key length is not valid for a keyed algorithm. */
class InvalidLength : IllegalArgumentException("invalid key length")
