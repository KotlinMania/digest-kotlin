// port-lint: source src/digest.rs
package io.github.kotlinmania.digest

import kotlin.reflect.KClass

/** Marker trait for cryptographic hash functions. */
interface HashMarker

/** Factory for hash implementations used by Kotlin callers. */
interface DigestFactory<D> where D : Digest {
    /** Create a new hasher instance. */
    fun new(): D

    /** Output size in bytes. */
    val outputSize: Int

    /** Block size in bytes. */
    val blockSize: Int
}

/**
 * Convenience wrapper trait covering cryptographic hash functions with fixed
 * output size.
 */
interface Digest : Update, FixedOutput, FixedOutputReset, Reset, HashMarker, OutputSizeUser {
    /** Create a new hasher with [data] already processed. */
    fun newWithPrefix(data: ByteArray): Digest {
        update(data)
        return this
    }

    /** Process input data in a chained manner. */
    fun chainUpdate(data: ByteArray): Digest {
        update(data)
        return this
    }

    /** Retrieve the result and consume this hasher's current state. */
    fun finalize(): Output<Digest> = finalizeFixed()

    /** Retrieve the result and reset this hasher. */
    fun finalizeReset(): Output<Digest> = finalizeFixedReset()

    companion object {
        private val factories = mutableMapOf<KClass<*>, DigestFactory<*>>()

        /** Register a concrete hash implementation factory. */
        fun <D> register(type: KClass<D>, factory: DigestFactory<D>) where D : Digest {
            factories[type] = factory
        }

        /** Create a hasher for [type]. */
        fun new(type: KClass<out Digest>): Digest = factory(type).new()

        /** Return the output size for [type]. */
        fun outputSize(type: KClass<out Digest>): Int = factory(type).outputSize

        /** Return the block size for [type]. */
        fun blockSize(type: KClass<out Digest>): Int = factory(type).blockSize

        /** Compute a digest for [data] using [type]. */
        fun digest(type: KClass<out Digest>, data: ByteArray): Output<Digest> {
            val hasher = new(type)
            hasher.update(data)
            return hasher.finalizeFixed()
        }

        private fun factory(type: KClass<out Digest>): DigestFactory<out Digest> {
            return factories[type]
                ?: throw IllegalStateException("No digest factory registered for ${type.simpleName}")
        }
    }
}

/** Modification of [Digest] suitable for object-style dispatch. */
interface DynDigest : Update, Reset {
    /** Write the result into [buf]. */
    fun finalizeInto(buf: ByteArray): Result<Unit>

    /** Write the result into [out] and reset the hasher. */
    fun finalizeIntoReset(out: ByteArray): Result<Unit>

    /** Get the output size of this hasher. */
    fun outputSize(): Int

    /** Clone hasher state into another dynamic digest. */
    fun boxClone(): DynDigest
}
