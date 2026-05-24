// port-lint: source src/dev.rs
package io.github.kotlinmania.digest.dev

import io.github.kotlinmania.digest.Update

/** Feed approximately 16 MiB of pseudorandom data into [d]. */
fun feedRand16Mib(d: Update) {
    val buf = ByteArray(1024)
    val rng = XorShiftRng()
    val n = 16 * (1 shl 20) / buf.size
    repeat(n) {
        rng.fill(buf)
        d.update(buf)
        d.update(byteArrayOf(42))
    }
}

private class XorShiftRng(
    private var x: UInt = 0x07873B4Au,
    private var y: UInt = 0xFAAB8FFEu,
    private var z: UInt = 0x1745980Fu,
    private var w: UInt = 0xB0ADB4F3u,
) {
    fun fill(buf: ByteArray) {
        var i = 0
        while (i + 3 < buf.size) {
            val n = nextU32()
            buf[i] = (n and 0xFFu).toByte()
            buf[i + 1] = ((n shr 8) and 0xFFu).toByte()
            buf[i + 2] = ((n shr 16) and 0xFFu).toByte()
            buf[i + 3] = ((n shr 24) and 0xFFu).toByte()
            i += 4
        }
    }

    private fun nextU32(): UInt {
        val xv = x
        val t = xv xor (xv shl 11)
        x = y
        y = z
        z = w
        val wv = w
        w = wv xor (wv shr 19) xor (t xor (t shr 8))
        return w
    }
}
