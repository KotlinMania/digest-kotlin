// port-lint: ignore - Kotlin smoke tests for the initial digest API slice.
package io.github.kotlinmania.digest

import io.github.kotlinmania.digest.dev.feedRand16Mib
import io.github.kotlinmania.digest.dev.fixedResetTest
import io.github.kotlinmania.digest.dev.fixedTest
import io.github.kotlinmania.digest.dev.macTest
import io.github.kotlinmania.digest.dev.resettableMacTest
import io.github.kotlinmania.digest.dev.variableResetTest
import io.github.kotlinmania.digest.dev.variableTest
import io.github.kotlinmania.digest.dev.xofResetTest
import io.github.kotlinmania.digest.fmt.Formatter
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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

    @Test
    fun coreWrapperFromCoreAndDecompose() {
        val core = StaticFixedCore()
        val wrapper = CoreWrapper.fromCore(core)
        val (c, _) = wrapper.decompose()
        assertEquals(core, c)
    }

    @Test
    fun coreWrapperImplementsCoreProxy() {
        val core = StaticFixedCore()
        val wrapper: CoreProxy = CoreWrapper.fromCore(core)
        assertEquals(core, wrapper.core)
    }

    @Test
    fun coreWrapperToStringUsesAlgorithmName() {
        val wrapper = CoreWrapper.fromCore(StaticFixedCore())
        assertTrue(wrapper.toString().contains("StaticFixed"))
    }

    @Test
    fun xofCoreWrapperReadsBlocks() {
        val wrapper = XofCoreWrapper(StaticXofCore())
        val buf = ByteArray(3)
        wrapper.finalizeXof().read(buf)
        assertContentEquals(byteArrayOf(0xBB.toByte(), 0xBB.toByte(), 0xBB.toByte()), buf)
    }

    @Test
    fun xofCoreWrapperFinalizeReset() {
        val wrapper = XofCoreWrapper(StaticXofCore())
        val buf = ByteArray(2)
        wrapper.finalizeXofReset().read(buf)
        assertContentEquals(byteArrayOf(0xBB.toByte(), 0xBB.toByte()), buf)
    }

    @Test
    fun ctVariableCoreWrapperTruncatesLeft() {
        val core = StaticVariableCore(byteArrayOf(1, 2, 3, 4), TruncSide.Left)
        val wrapper = CtVariableCoreWrapper(core, 2)
        val buf = Buffer<CtVariableCoreWrapper>(1)
        val out = ByteArray(2)
        wrapper.finalizeFixedCore(buf, out)
        assertContentEquals(byteArrayOf(1, 2), out)
    }

    @Test
    fun ctVariableCoreWrapperTruncatesRight() {
        val core = StaticVariableCore(byteArrayOf(1, 2, 3, 4), TruncSide.Right)
        val wrapper = CtVariableCoreWrapper(core, 2)
        val buf = Buffer<CtVariableCoreWrapper>(1)
        val out = ByteArray(2)
        wrapper.finalizeFixedCore(buf, out)
        assertContentEquals(byteArrayOf(3, 4), out)
    }

    @Test
    fun bufferKindEagerAndLazyAreDistinct() {
        val eager: BufferKind = Eager
        val lazy: BufferKind = Lazy
        assertTrue(eager != lazy)
    }

    @Test
    fun feedRand16MibPushesData() {
        var total = 0
        val sink = object : Update { override fun update(data: ByteArray) { total += data.size } }
        feedRand16Mib(sink)
        assertTrue(total > 0)
    }

    @Test
    fun devFixedResetTestPassesForStaticHasher() {
        val expected = byteArrayOf(0xAA.toByte(), 0xBB.toByte())
        val result = fixedResetTest(
            input = byteArrayOf(1, 2, 3),
            output = expected,
            create = { StaticHasher(expected) },
            clone = { h -> h.clone() },
        )
        assertNull(result)
    }

    @Test
    fun devFixedTestPassesForStaticHasher() {
        val expected = byteArrayOf(0xCC.toByte())
        val result = fixedTest(
            input = byteArrayOf(1, 2),
            output = expected,
            create = { StaticHasher(expected) },
        )
        assertNull(result)
    }

    @Test
    fun devVariableResetTestPassesForStaticVariable() {
        val expected = byteArrayOf(0x11, 0x22)
        val result = variableResetTest(
            input = byteArrayOf(5, 6),
            output = expected,
            create = { size -> StaticVariable(expected.copyOf(size)) },
            clone = { v -> v.clone() },
        )
        assertNull(result)
    }

    @Test
    fun devVariableTestPassesForStaticVariable() {
        val expected = byteArrayOf(0x33)
        val result = variableTest(
            input = byteArrayOf(7),
            output = expected,
            create = { size -> StaticVariable(expected.copyOf(size)) },
        )
        assertNull(result)
    }

    @Test
    fun devXofResetTestPassesForStaticXof() {
        val expected = byteArrayOf(0xBB.toByte(), 0xBB.toByte())
        val result = xofResetTest(
            input = byteArrayOf(9),
            output = expected,
            create = { StaticXof() },
            clone = { StaticXof() },
        )
        assertNull(result)
    }

    @Test
    fun devMacTestPassesForStaticMac() {
        val tag = byteArrayOf(7, 8)
        val result = macTest(
            key = tag,
            input = byteArrayOf(1, 2, 3),
            tag = tag,
            create = { key -> StaticMac(key) },
        )
        assertNull(result)
    }

    @Test
    fun devResettableMacTestPassesForStaticMac() {
        val tag = byteArrayOf(5, 6)
        val result = resettableMacTest(
            key = tag,
            input = byteArrayOf(9),
            tag = tag,
            create = { key -> StaticMac(key) },
        )
        assertNull(result)
    }

    // --- test implementations ---

    private object SampleSize : OutputSizeUser {
        override val outputSize: Int = 3
    }

    private class StaticFixedCore : UpdateCore, FixedOutputCore, BufferKindUser, OutputSizeUser, Reset, AlgorithmName {
        override val blockSize = 1
        override val outputSize = 2
        override val bufferKind: BufferKind = Eager
        override fun updateBlocks(blocks: List<Block<*>>) = Unit
        override fun finalizeFixedCore(buffer: Buffer<*>, out: Output<*>) { out.fill(0xAA.toByte()) }
        override fun reset() = Unit
        override fun writeAlgName(formatter: Formatter): io.github.kotlinmania.digest.fmt.FmtResult =
            formatter.writeString("StaticFixed")
    }

    private class StaticXofReaderCore : XofReaderCore {
        override val blockSize = 1
        override fun readBlock(): Block<*> = byteArrayOf(0xBB.toByte())
    }

    private class StaticXofCore : UpdateCore, ExtendableOutputCore, BufferKindUser, Reset {
        override val blockSize = 1
        override val bufferKind: BufferKind = Lazy
        override fun updateBlocks(blocks: List<Block<*>>) = Unit
        override fun finalizeXofCore(buffer: Buffer<*>): XofReaderCore = StaticXofReaderCore()
        override fun reset() = Unit
    }

    private class StaticVariableCore(
        private val output: ByteArray,
        override val truncSide: TruncSide,
    ) : VariableOutputCore, Reset {
        override val blockSize = 1
        override val outputSize = output.size
        override val bufferKind: BufferKind = Eager
        override fun updateBlocks(blocks: List<Block<*>>) = Unit
        override fun finalizeVariableCore(buffer: Buffer<*>, out: Output<*>) { output.copyInto(out) }
        override fun reset() = Unit
    }

    private class StaticHasher(
        private val result: ByteArray,
    ) : FixedOutputReset {
        override val outputSize = result.size
        override fun update(data: ByteArray) = Unit
        override fun finalizeInto(out: Output<*>) { result.copyInto(out) }
        override fun finalizeIntoReset(out: Output<*>) { result.copyInto(out) }
        override fun reset() = Unit
        fun clone(): StaticHasher = StaticHasher(result.copyOf())
    }

    private class StaticVariable(private val result: ByteArray) : VariableOutputReset {
        override val maxOutputSize = result.size
        override fun outputSize() = result.size
        override fun update(data: ByteArray) = Unit
        override fun finalizeVariable(out: ByteArray): Result<Unit> {
            if (out.size != result.size) return Result.failure(InvalidBufferSize())
            result.copyInto(out)
            return Result.success(Unit)
        }
        override fun finalizeVariableReset(out: ByteArray): Result<Unit> = finalizeVariable(out)
        override fun reset() = Unit
        fun clone(): StaticVariable = StaticVariable(result.copyOf())
    }

    private class StaticXof : ExtendableOutputReset {
        override fun update(data: ByteArray) = Unit
        override fun finalizeXof(): XofReader = StaticXofReader()
        override fun finalizeXofReset(): XofReader = StaticXofReader()
        override fun reset() = Unit
    }

    private class StaticXofReader : XofReader {
        override fun read(buffer: ByteArray) { buffer.fill(0xBB.toByte()) }
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
