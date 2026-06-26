// port-lint: source dev/rng.rs
package io.github.kotlinmania.digest.dev

/** Initial RNG state used in tests. */
internal val RNG: XorShiftRng
    get() =
        XorShiftRng(
            x = 0x07873B4Au,
            y = 0xFAAB8FFEu,
            z = 0x1745980Fu,
            w = 0xB0ADB4F3u,
        )

/** Xorshift RNG instance. */
internal class XorShiftRng(
    private var x: UInt,
    private var y: UInt,
    private var z: UInt,
    private var w: UInt,
) {
    fun fill(buf: ByteArray) {
        var index = 0
        while (index + 3 < buf.size) {
            val next = nextU32()
            buf[index] = (next and 0xFFu).toByte()
            buf[index + 1] = ((next shr 8) and 0xFFu).toByte()
            buf[index + 2] = ((next shr 16) and 0xFFu).toByte()
            buf[index + 3] = ((next shr 24) and 0xFFu).toByte()
            index += 4
        }
    }

    fun nextU32(): UInt {
        val currentX = x
        val t = currentX xor (currentX shl 11)
        x = y
        y = z
        z = w
        val currentW = w
        w = currentW xor (currentW shr 19) xor (t xor (t shr 8))
        return w
    }
}
