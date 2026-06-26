// port-lint: source mac.rs
package io.github.kotlinmania.digest

/** Marker trait for Message Authentication algorithms. */
interface MacMarker

/** Convenience wrapper trait for Message Authentication algorithms. */
interface Mac : OutputSizeUser {
    /** Create a new value from a fixed-size key. */
    fun new(key: Key<*>): Mac

    /** Create a new value from a variable-size key. */
    fun newFromSlice(key: ByteArray): Result<Mac>

    /** Update state using the provided data. */
    fun update(data: ByteArray)

    /** Process input data in a chained manner. */
    fun chainUpdate(data: ByteArray): Mac {
        update(data)
        return this
    }

    /** Obtain the result of a MAC computation. */
    fun finalize(): CtOutput<*>

    /** Obtain the result of a MAC computation and reset the instance. */
    fun finalizeReset(): CtOutput<*>

    /** Reset the MAC instance to its initial state. */
    fun reset()

    /** Check whether [tag] is correct for the processed input. */
    fun verify(tag: Output<*>): Result<Unit> =
        if (constantTimeEquals(finalize().intoBytes(), tag)) {
            Result.success(Unit)
        } else {
            Result.failure(MacError())
        }

    /** Check whether [tag] is correct and reset the instance. */
    fun verifyReset(tag: Output<*>): Result<Unit> =
        if (constantTimeEquals(finalizeReset().intoBytes(), tag)) {
            Result.success(Unit)
        } else {
            Result.failure(MacError())
        }

    /** Check tag correctness using all bytes of the calculated tag. */
    fun verifySlice(tag: ByteArray): Result<Unit> =
        if (tag.size == outputSize) {
            verify(tag)
        } else {
            Result.failure(MacError())
        }

    /** Check tag correctness using all bytes of the calculated tag and reset. */
    fun verifySliceReset(tag: ByteArray): Result<Unit> =
        if (tag.size == outputSize) {
            verifyReset(tag)
        } else {
            Result.failure(MacError())
        }

    /** Check truncated tag correctness using left-side bytes. */
    fun verifyTruncatedLeft(tag: ByteArray): Result<Unit> {
        val expected = finalize().intoBytes()
        return if (tag.isNotEmpty() && tag.size <= expected.size && constantTimeEquals(expected, 0, tag)) {
            Result.success(Unit)
        } else {
            Result.failure(MacError())
        }
    }

    /** Check truncated tag correctness using right-side bytes. */
    fun verifyTruncatedRight(tag: ByteArray): Result<Unit> {
        val expected = finalize().intoBytes()
        val offset = expected.size - tag.size
        return if (tag.isNotEmpty() && offset >= 0 && constantTimeEquals(expected, offset, tag)) {
            Result.success(Unit)
        } else {
            Result.failure(MacError())
        }
    }
}

/** Fixed-size output value with equality based on byte contents. */
class CtOutput<T : OutputSizeUser>(
    bytes: Output<T>,
) {
    private val bytes: Output<T> = bytes.copyOf()

    /** Get the inner output array this type wraps. */
    fun intoBytes(): Output<T> = bytes.copyOf()

    override fun equals(other: Any?): Boolean =
        other is CtOutput<*> && constantTimeEquals(bytes, other.intoBytes())

    override fun hashCode(): Int = bytes.contentHashCode()
}

/** Error for when MAC output is not equal to the expected value. */
class MacError : IllegalArgumentException("MAC tag mismatch")

private fun constantTimeEquals(left: ByteArray, right: ByteArray): Boolean {
    var difference = left.size xor right.size
    val maxSize = maxOf(left.size, right.size)
    for (index in 0 until maxSize) {
        val leftByte = if (index < left.size) left[index].toInt() and 0xff else 0
        val rightByte = if (index < right.size) right[index].toInt() and 0xff else 0
        difference = difference or (leftByte xor rightByte)
    }
    return difference == 0
}

private fun constantTimeEquals(left: ByteArray, leftOffset: Int, right: ByteArray): Boolean {
    var difference = 0
    for (index in right.indices) {
        val leftByte = left[leftOffset + index].toInt() and 0xff
        val rightByte = right[index].toInt() and 0xff
        difference = difference or (leftByte xor rightByte)
    }
    return difference == 0
}
