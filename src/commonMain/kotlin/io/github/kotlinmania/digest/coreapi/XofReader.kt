// port-lint: source core_api/xof_reader.rs
package io.github.kotlinmania.digest.coreapi

import io.github.kotlinmania.digest.XofReader

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
                for (byte in core.readBlock()) pending.addLast(byte)
            }
            val take = minOf(pending.size, remaining)
            repeat(take) { buffer[offset++] = pending.removeFirst() }
            remaining -= take
        }
    }
}
