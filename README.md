# digest-kotlin

Kotlin Multiplatform port of RustCrypto's [`digest`](https://crates.io/crates/digest)
crate. The initial target is `digest 0.10.7`, the version resolved by
`hmac-kotlin/tmp/hmac/Cargo.lock`.

This repo was started to provide the real sibling dependency that
`hmac-kotlin` imports as `io.github.kotlinmania.digest.*`.

## Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:digest-kotlin:0.1.0")
}
```

## Building

```bash
./gradlew setupAndroidSdk --no-daemon --console=plain --no-configuration-cache
./gradlew compileAndroidMain --no-daemon --console=plain --no-configuration-cache
./gradlew test --no-daemon --console=plain --no-configuration-cache
./gradlew build --dry-run --no-daemon --console=plain --no-configuration-cache
```

## Upstream

Upstream source is stored under `tmp/digest/` and must remain read-only. Kotlin
files mapped from upstream carry `// port-lint: source ...` headers.

## License

Licensed under either MIT or Apache-2.0, matching the upstream `digest` crate.
