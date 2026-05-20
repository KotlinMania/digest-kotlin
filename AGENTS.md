# Agent guide - digest-kotlin

This repository is the Kotlin Multiplatform port of the upstream RustCrypto
`digest` crate. The upstream source lives under `tmp/digest/` and is read-only.

The port is in parity mode. Read `CLAUDE.md`, `README.md`, and the upstream Rust
file before editing any mapped Kotlin file. Preserve the Rust file to Kotlin
file mapping, translate top to bottom, and keep comments Kotlin-facing.

No stubs, placeholder bodies, `TODO()`, fake implementations, or
`@Suppress(...)`. If a dependent crate such as `hmac-kotlin` needs a symbol,
port the corresponding upstream `digest` item faithfully instead of adding a
local shim in the dependent repo.

Build gates:

```bash
./gradlew setupAndroidSdk --no-daemon --console=plain --no-configuration-cache
./gradlew compileAndroidMain --no-daemon --console=plain --no-configuration-cache
./gradlew test --no-daemon --console=plain --no-configuration-cache
./gradlew build --dry-run --no-daemon --console=plain --no-configuration-cache
```
