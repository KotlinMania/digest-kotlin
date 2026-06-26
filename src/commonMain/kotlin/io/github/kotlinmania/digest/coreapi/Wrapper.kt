// port-lint: source core_api/wrapper.rs
package io.github.kotlinmania.digest.coreapi

import io.github.kotlinmania.digest.BlockSizeUser
import io.github.kotlinmania.digest.ExtendableOutput
import io.github.kotlinmania.digest.ExtendableOutputReset
import io.github.kotlinmania.digest.FixedOutput
import io.github.kotlinmania.digest.FixedOutputReset
import io.github.kotlinmania.digest.HashMarker
import io.github.kotlinmania.digest.Output
import io.github.kotlinmania.digest.OutputSizeUser
import io.github.kotlinmania.digest.Reset
import io.github.kotlinmania.digest.Update
import io.github.kotlinmania.digest.fmt.Formatter

/** Wrapper around [BufferKindUser]. */
class CoreWrapper<C>(
    core: C,
) : Update,
    FixedOutput,
    FixedOutputReset,
    Reset,
    OutputSizeUser,
    BlockSizeUser,
    HashMarker,
    CoreProxy
    where
          C : UpdateCore,
          C : FixedOutputCore,
          C : Reset {
    private val coreImpl: C = core
    private val buffer = Buffer<CoreWrapper<C>>(coreImpl.blockSize)

    override val core: Any get() = coreImpl

    companion object {
        /** Create new wrapper from [core]. */
        fun <C> fromCore(core: C): CoreWrapper<C>
        where
              C : UpdateCore,
              C : FixedOutputCore,
              C : Reset = CoreWrapper(core)
    }

    /** Decompose wrapper into inner parts. */
    fun decompose(): Pair<C, Buffer<CoreWrapper<C>>> = Pair(coreImpl, buffer)

    override val outputSize: Int get() = coreImpl.outputSize
    override val blockSize: Int get() = coreImpl.blockSize

    override fun update(data: ByteArray) {
        buffer.digestBlocks(data) { blocks -> coreImpl.updateBlocks(blocks) }
    }

    override fun finalizeInto(out: Output<*>) {
        coreImpl.finalizeFixedCore(buffer, out)
    }

    override fun finalizeIntoReset(out: Output<*>) {
        coreImpl.finalizeFixedCore(buffer, out)
        reset()
    }

    override fun reset() {
        coreImpl.reset()
        buffer.reset()
    }

    override fun toString(): String {
        val fmt = Formatter()
        (coreImpl as? AlgorithmName)?.writeAlgName(fmt)
            ?: fmt.writeString(coreImpl::class.simpleName ?: "")
        fmt.writeString(" { .. }")
        return fmt.toString()
    }
}

/** Wrapper around a block-level core value for extendable-output algorithms. */
class XofCoreWrapper<C>(
    core: C,
) : Update,
    ExtendableOutput,
    ExtendableOutputReset,
    Reset,
    BlockSizeUser
    where
          C : UpdateCore,
          C : ExtendableOutputCore,
          C : Reset {
    private val coreImpl: C = core
    private val buffer = Buffer<XofCoreWrapper<C>>(coreImpl.blockSize)

    override val blockSize: Int get() = coreImpl.blockSize

    override fun update(data: ByteArray) {
        buffer.digestBlocks(data) { blocks -> coreImpl.updateBlocks(blocks) }
    }

    override fun finalizeXof(): XofReaderCoreWrapper {
        val readerCore = coreImpl.finalizeXofCore(buffer)
        return XofReaderCoreWrapper(readerCore)
    }

    override fun finalizeXofReset(): XofReaderCoreWrapper {
        val readerCore = coreImpl.finalizeXofCore(buffer)
        coreImpl.reset()
        buffer.reset()
        return XofReaderCoreWrapper(readerCore)
    }

    override fun reset() {
        coreImpl.reset()
        buffer.reset()
    }
}
