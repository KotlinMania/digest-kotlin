// port-lint: ignore - Kotlin formatting helper for translated Rust fmt APIs.
package io.github.kotlinmania.digest.fmt

/** Result returned by formatting helpers. */
typealias FmtResult = Result<Unit>

/** Small formatter used by translated algorithm-name APIs. */
class Formatter {
    private val text = StringBuilder()

    /** Append [value] to this formatter. */
    fun writeString(value: String): FmtResult {
        text.append(value)
        return Result.success(Unit)
    }

    /** Start a debug-struct builder. */
    fun debugStruct(name: String): DebugStruct =
        DebugStruct(this, name)

    override fun toString(): String = text.toString()

    /** Builder for debug-struct shaped output. */
    class DebugStruct internal constructor(
        private val formatter: Formatter,
        name: String,
    ) {
        private var first = true

        init {
            formatter.writeString("$name { ")
        }

        /** Append a field. */
        fun field(name: String, value: Any?): DebugStruct {
            if (!first) {
                formatter.writeString(", ")
            }
            first = false
            formatter.writeString("$name: $value")
            return this
        }

        /** Finish this debug structure. */
        fun finish(): FmtResult {
            formatter.writeString(" }")
            return Result.success(Unit)
        }
    }
}
