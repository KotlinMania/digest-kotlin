// port-lint: ignore - Kotlin smoke tests for the initial digest API slice.
package io.github.kotlinmania.digest

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DigestApiTest {
    @Test
    fun ctOutputCopiesBytes() {
        val bytes = byteArrayOf(1, 2, 3)
        val output = CtOutput<SampleSize>(bytes)
        bytes[0] = 9

        assertContentEquals(byteArrayOf(1, 2, 3), output.intoBytes())
    }

    @Test
    fun bufferEmitsCompleteBlocks() {
        val blocks = mutableListOf<ByteArray>()
        val buffer = Buffer<SampleSize>(2)

        buffer.digestBlocks(byteArrayOf(1, 2, 3, 4, 5)) { block -> blocks += block }

        assertEquals(2, blocks.size)
        assertContentEquals(byteArrayOf(1, 2), blocks[0])
        assertContentEquals(byteArrayOf(3, 4), blocks[1])
        assertEquals(1, buffer.size)
    }

    @Test
    fun macVerificationUsesBytes() {
        val mac = StaticMac(byteArrayOf(7, 8))

        assertTrue(mac.verifySlice(byteArrayOf(7, 8)).isSuccess)
        assertTrue(mac.verifyTruncatedLeft(byteArrayOf(7)).isSuccess)
        assertTrue(mac.verifyTruncatedRight(byteArrayOf(8)).isSuccess)
    }

    private object SampleSize : OutputSizeUser {
        override val outputSize: Int = 3
    }

    private class StaticMac(
        private val result: ByteArray,
    ) : Mac {
        override val outputSize: Int = result.size

        override fun new(key: Key<*>): Mac = StaticMac(key)

        override fun newFromSlice(key: ByteArray): Result<Mac> =
            Result.success(StaticMac(key))

        override fun update(data: ByteArray) = Unit

        override fun finalize(): CtOutput<*> = CtOutput<SampleSize>(result)

        override fun finalizeReset(): CtOutput<*> = finalize()

        override fun reset() = Unit
    }
}
