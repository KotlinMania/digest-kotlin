# Claude Code Project Instructions - digest-kotlin

## Project Overview

`digest-kotlin` is a clean-room Kotlin Multiplatform port of RustCrypto's
[`digest`](https://crates.io/crates/digest) crate. This repo was started from
the current Kotlinmania KMP template to unblock `hmac-kotlin`, whose upstream
Rust crate depends on `digest 0.10.x`.

The upstream Rust source is checked in under `tmp/digest/` for translation
inventory only. Never edit `tmp/`.

## Current Scope

The first source slice ports the public trait and wrapper surface that dependent
RustCrypto crates consume:

- `src/lib.rs` -> `Lib.kt`
- `src/digest.rs` -> `Digest.kt`
- `src/mac.rs` -> `Mac.kt`
- `src/core_api.rs` -> `CoreApi.kt`

Kotlin cannot model Rust associated type constructors exactly. When Rust uses
type-level output, block, or key sizes, this port represents the bytes as
`ByteArray` and exposes sizes through Kotlin interfaces and descriptors. Do not
add Rust-shaped fake static constructors to satisfy a dependent translation;
fix the dependent translation to use Kotlin-native factories or descriptors.

## Translation Rules

- Read the whole upstream `.rs` file before editing its Kotlin counterpart.
- Preserve one Rust file to one Kotlin file mapping.
- Translate declarations in upstream order where possible.
- Translate comments into Kotlin-facing terms.
- Do not add stubs, placeholder implementations, `TODO()`, or `@Suppress`.
- Do not use JVM-only APIs in common source.

## Port-Lint Headers

Every Kotlin source file mapped from Rust starts with:

```kotlin
// port-lint: source <path-relative-to-tmp/digest>
package io.github.kotlinmania.digest
```

For Kotlin support files with no exact Rust counterpart, use
`// port-lint: ignore` and keep the file small.

## Build

```bash
./gradlew setupAndroidSdk --no-daemon --console=plain --no-configuration-cache
./gradlew compileAndroidMain --no-daemon --console=plain --no-configuration-cache
./gradlew test --no-daemon --console=plain --no-configuration-cache
./gradlew build --dry-run --no-daemon --console=plain --no-configuration-cache
```
