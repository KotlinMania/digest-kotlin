// port-lint: source dev.rs
package io.github.kotlinmania.digest.dev

import io.github.kotlinmania.digest.Update

/** Feed approximately 16 MiB of pseudorandom data into [d]. */
fun feedRand16mib(d: Update) {
    val buf = ByteArray(1024)
    val rng = RNG
    val n = 16 * (1 shl 20) / buf.size
    repeat(n) {
        rng.fill(buf)
        d.update(buf)
        d.update(byteArrayOf(42))
    }
}
