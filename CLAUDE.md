# Claude Code Project Instructions - digest-kotlin

This is all the memory you need. So use it on kotlinmania projects in this folder.

Embedded runbook obligations: read ../AGENTS.md, ../CLAUDE.md, ../BUILD_TEMPLATE_HANDOFF.md, and in each focused repo AGENTS.md, CLAUDE.md if present, and README.md before choosing work. This is not a read-only monitor and not only a branch-merge task. A valid slot leaves each focused repo materially better or reports the exact hard blocker.

First audit workflow shape against sibling quiet patterns: ci.yml uses workflow_dispatch only, codeql.yml uses weekly schedule plus workflow_dispatch, publish.yml uses release[released] plus workflow_dispatch, and platform workflows use workflow_call only. If old push or pull_request triggers remain, fix them or report the blocker; otherwise continue with branch, build, dependency, publishing, or port work.

Constructive work order: inspect git status, open PRs, remote heads, failed runs or logs, and Gradle, Kotlin, Node/Yarn, lockfile, CodeQL, Dependabot, and template shape. Reconcile branches through one repo branch using no-fast-forward merges: merge current main into the work branch, verify locally, merge the cleaned branch back to main, close merged PRs, and delete only remote branches proven ancestors of main. Branch ownership is irrelevant.

If branch repair is exhausted, fix an actual build, CI, CodeQL, Dependabot, JS toolchain, Android SDK, publishing, or workflow defect with local verification, then commit and push. Remote GitHub Actions are informational only; do brief gh checks after pushes and do not wait on disabled, queued, absent, or missing runs as proof.

Android SDK build normalization: when a repo still has shell setup or stale Android workflow shape, use serial-test-kotlin dc29a78 or anyhow-kotlin 0fd90ee as the pattern. Move setup into build.gradle.kts, delete setup-android-sdk.sh and setup-android-sdk.bat, add ByteArrayInputStream URI Files StandardCopyOption ZipInputStream GradleException imports, use pins command-tools 14742923 compileSdk 34 build-tools 36.0.0, keep .android-sdk project-local, refresh local.properties, handle macOS/Linux/Windows including sdkmanager.bat, add install marker, sdkManagerCommand helper, Gradle URI downloader, ZipInputStream extraction with zip-slip guard, executable permissions, noninteractive license acceptance, package install, sdkmanager-install.log, and clear GradleException failures. Call installProjectAndroidSdk before configuring the Android target and make setupAndroidSdk a Kotlin-backed task, not Exec. Update android, CodeQL, publish, and Windows workflows so setupAndroidSdk runs before Android work or publishing; no WSL, bash, batch, or PowerShell duplicate installer. Verify with stale-reference rg, workflow rg, setupAndroidSdk, compileAndroidMain, build --dry-run, test, and git diff --check.

Porting is the default next action when there is no concrete infra repair. Repo-local CLAUDE.md drives porting work: use its objectives, inventory, and gates; if it is missing, say so and fall back to AGENTS.md plus README.md. Use ast-distance when required, confirm upstream Rust under tmp, read the whole upstream .rs file before Kotlin edits, preserve file mapping and upstream order, and translate comments into Kotlin-facing wording. No stubs, TODO calls, suppress annotations, fake implementations, Rust syntax in Kotlin/KDoc/comments, or hiding missing dependencies.

Git hygiene: do not use git worktree, do not use /tmp scratch, never force-push, avoid git pull, inspect status before fetch/merge/switch, never discard user changes, and stage and commit every dirty file produced in focused repos before ending.

# Kotlinmania CI hourly automation runbook

This runbook is for the hourly Kotlinmania CI/build repair rotation. Each
scheduled slot owns only the repository or repositories named in its prompt.
Do not drift into unrelated repositories unless a direct dependency or merge
blocker requires it; explain any such exception in the run report.

## Required reading

Before doing any work in a slot, read:

- `../AGENTS.md`
- `../CLAUDE.md`
- `../BUILD_TEMPLATE_HANDOFF.md`
- In every focused repo: `AGENTS.md`, `CLAUDE.md`, and `README.md`.

If a focused repo is missing `CLAUDE.md`, note that explicitly and proceed from
the workspace `CLAUDE.md` as the governing completion contract, plus the repo
`AGENTS.md` and the repo `README.md`. A missing repo-local `CLAUDE.md` is not
permission to invent a lighter contract.

This reading is mandatory even for "just CI" or "just build" work. The
workspace-level `AGENTS.md` / `CLAUDE.md` and the repo-level
`AGENTS.md` / `CLAUDE.md` define what counts as done, how to compare against
upstream Rust, what target gates are authoritative, and which shortcuts are
forbidden. Do not infer those rules from memory or from sibling repos alone.

## Non-negotiable completion contract

There is no "close enough" exit for this automation. A slot is not complete
until every focused repo satisfies all of these conditions, unless a hard
external blocker makes one physically impossible and the blocker is reported
with exact evidence:

1. The focused repo is in the proper state defined by its local `CLAUDE.md`.
   If the focused repo has no `CLAUDE.md`, the workspace
   `../CLAUDE.md` is the contract.
2. Real source was ported or materially advanced from upstream Rust in the
   focused repo, following the repo's inventory and gates. CI plumbing,
   workflow cleanup, dependency updates, branch cleanup, dry-runs, or reports
   do not replace the source-porting requirement.
3. All configured targets build. "All targets" means the full target surface
   declared in `build.gradle.kts` and in the repo-local contract. Do not
   collapse this into shorthand. Use the consolidated target surface below as
   the minimum standard template expectation, then add any repo-specific
   targets from the focused repo. If a standard target is missing from a repo
   that should match the normalized template, treat that as a template defect
   to repair or a documented hard blocker, not permission to shrink the gate.
4. Host-specific targets are validated on a host that can actually run or link
   them. A macOS local build is not Windows validation; Windows work must be
   proven by the repo's Windows GitHub Actions job or another real Windows
   runner. The same rule applies to other targets that cannot be validated on
   the current host.
5. The branch, PR, and local checkout state are clean according to the git
   rules below, with every produced dirty file committed in the focused repo.

If these conditions are not met, keep working. Do not end with a status-only
note, a local-only substitute for an unavailable platform, a remote-CI shrug, or
a "nothing obvious to do" summary. Only an exact blocker outside the agent's
control may stop the slot, and the report must say which completion condition
is blocked, which command or remote run proves it, and what remains unfinished.

### Consolidated all-target surface from repo scrape

This list is evidence-backed. On 2026-05-20, a workspace scrape of
`build.gradle.kts` files found the broad target matrix in 214 `*-kotlin` repos.
The scrape included, among others:

- `age-kotlin`
- `allocative-kotlin`
- `anstyle-kotlin`
- `anyhow-kotlin`
- `arboard-kotlin`
- `arc-swap-kotlin`
- `assert-cmd-kotlin`
- `assert-matches-kotlin`
- `async-channel-kotlin`
- `async-io-kotlin`
- `async-stream-kotlin`
- `async-trait-kotlin`
- `aws-config-kotlin`
- `aws-credential-types-kotlin`
- `aws-sigv4-kotlin`
- `aws-types-kotlin`
- `axum-kotlin`
- `base64-kotlin`
- `bitflags-kotlin`
- `bm25-kotlin`
- `btree-kotlin`
- `cansi-kotlin`
- `cc-kotlin`
- `chardetng-kotlin`
- `chrono-kotlin`
- `clap-complete-kotlin`
- `clap-kotlin`
- `codex-lmstudio-kotlin`

The consolidated declaration surface is:

- `macosArm64`
- `iosArm64`
- `iosSimulatorArm64`
- `iosX64`
- `tvosArm64`
- `tvosSimulatorArm64`
- `watchosArm32`
- `watchosArm64`
- `watchosDeviceArm64`
- `watchosSimulatorArm64`
- `linuxX64`
- `linuxArm64`
- `mingwX64`
- `androidNativeArm32`
- `androidNativeArm64`
- `androidNativeX86`
- `androidNativeX64`
- `js` with browser and Node.js environments
- `wasmJs` with browser and Node.js environments
- `wasmWasi` with Node.js execution
- `swiftExport`
- Android KMP library target with main, host-test, and device-test builders
- `jvm`
- the repo-specific `XCFramework("<Name>")`
- all Maven publication variants generated for those targets

The typical `build.gradle.kts` target block for that surface looks like this.
Replace `Example` and `io.github.kotlinmania.example` with the focused repo's
PascalCase framework/module name and package. Do not delete targets from this
shape unless the repo-local `CLAUDE.md` documents a narrower target contract or
the missing target is reported as a blocker.

```kotlin
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

kotlin {
    applyDefaultHierarchyTemplate()

    val xcf = XCFramework("Example")

    macosArm64 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }

    iosArm64 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }
    iosSimulatorArm64 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }
    iosX64 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }

    tvosArm64 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }
    tvosSimulatorArm64 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }

    watchosArm32 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }
    watchosArm64 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }
    watchosDeviceArm64 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }
    watchosSimulatorArm64 {
        binaries.framework { baseName = "Example"; xcf.add(this) }
    }

    linuxX64()
    linuxArm64()
    mingwX64()

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }

    swiftExport {
        moduleName = "Example"
        flattenPackage = "io.github.kotlinmania.example"
    }

    android {
        namespace = "io.github.kotlinmania.example"
        compileSdk = 34
        minSdk = 24
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
    }

    jvm()

    jvmToolchain(21)
}
```

The consolidated build-validation task surface is:

- Android KMP: `compileAndroidMain`, `compileAndroidHostTest`,
  `compileAndroidDeviceTest`, `assembleAndroidMain`, `assembleUnitTest`,
  `assembleAndroidTest`, `assembleAndroidDeviceTest`, and
  `testAndroidHostTest` when generated.
- JVM: `jvmMainClasses`, `jvmTestClasses`, and `jvmTest`.
- JS: `jsMainClasses`, `jsTestClasses`, `jsBrowserTest`, `jsNodeTest`, and
  `jsTest`.
- Wasm-JS: `wasmJsMainClasses`, `wasmJsTestClasses`, `wasmJsBrowserTest`,
  `wasmJsNodeTest`, and `wasmJsTest`.
- Wasm-WASI: `wasmWasiMainClasses`, `wasmWasiTestClasses`,
  `wasmWasiNodeTest`, and `wasmWasiTest`.
- Native binary aggregators and test binary aggregators:
  `macosArm64Binaries`, `macosArm64TestBinaries`, `iosArm64Binaries`,
  `iosArm64TestBinaries`, `iosSimulatorArm64Binaries`,
  `iosSimulatorArm64TestBinaries`, `iosX64Binaries`, `iosX64TestBinaries`,
  `tvosArm64Binaries`, `tvosArm64TestBinaries`,
  `tvosSimulatorArm64Binaries`, `tvosSimulatorArm64TestBinaries`,
  `watchosArm32Binaries`, `watchosArm32TestBinaries`,
  `watchosArm64Binaries`, `watchosArm64TestBinaries`,
  `watchosDeviceArm64Binaries`, `watchosDeviceArm64TestBinaries`,
  `watchosSimulatorArm64Binaries`, `watchosSimulatorArm64TestBinaries`,
  `linuxX64Binaries`, `linuxX64TestBinaries`, `linuxArm64Binaries`,
  `linuxArm64TestBinaries`, `mingwX64Binaries`, `mingwX64TestBinaries`,
  `androidNativeArm32Binaries`, `androidNativeArm32TestBinaries`,
  `androidNativeArm64Binaries`, `androidNativeArm64TestBinaries`,
  `androidNativeX86Binaries`, `androidNativeX86TestBinaries`,
  `androidNativeX64Binaries`, and `androidNativeX64TestBinaries`.
- Native host test tasks where Gradle generates and the runner can execute
  them: `macosArm64Test`, `linuxX64Test`, `linuxArm64Test`, and
  `mingwX64Test`.
- Swift/Xcode export: `embedSwiftExportForXcode` when `swiftExport` is
  configured.
- XCFramework assembly: `assemble<Name>XCFramework`,
  `assemble<Name>DebugXCFramework`, `assemble<Name>ReleaseXCFramework`, and the
  generated fat-framework tasks
  `assembleDebugIosFatFrameworkFor<Name>XCFramework`,
  `assembleReleaseIosFatFrameworkFor<Name>XCFramework`,
  `assembleDebugIosSimulatorFatFrameworkFor<Name>XCFramework`,
  `assembleReleaseIosSimulatorFatFrameworkFor<Name>XCFramework`,
  `assembleDebugMacosFatFrameworkFor<Name>XCFramework`,
  `assembleReleaseMacosFatFrameworkFor<Name>XCFramework`,
  `assembleDebugTvosFatFrameworkFor<Name>XCFramework`,
  `assembleReleaseTvosFatFrameworkFor<Name>XCFramework`,
  `assembleDebugTvosSimulatorFatFrameworkFor<Name>XCFramework`,
  `assembleReleaseTvosSimulatorFatFrameworkFor<Name>XCFramework`,
  `assembleDebugWatchosFatFrameworkFor<Name>XCFramework`,
  `assembleReleaseWatchosFatFrameworkFor<Name>XCFramework`,
  `assembleDebugWatchosSimulatorFatFrameworkFor<Name>XCFramework`, and
  `assembleReleaseWatchosSimulatorFatFrameworkFor<Name>XCFramework`.
- Publication metadata/export tasks generated for every configured target,
  including `exportCommonSourceSetsMetadataLocationsForMetadataApiElements`,
  `exportRootPublicationCoordinatesForMetadataApiElements`,
  `exportCrossCompilationMetadataForAndroidNativeArm32ApiElements`,
  `exportCrossCompilationMetadataForAndroidNativeArm64ApiElements`,
  `exportCrossCompilationMetadataForAndroidNativeX86ApiElements`,
  `exportCrossCompilationMetadataForAndroidNativeX64ApiElements`,
  `exportCrossCompilationMetadataForIosArm64ApiElements`,
  `exportCrossCompilationMetadataForIosSimulatorArm64ApiElements`,
  `exportCrossCompilationMetadataForIosX64ApiElements`,
  `exportCrossCompilationMetadataForLinuxArm64ApiElements`,
  `exportCrossCompilationMetadataForLinuxX64ApiElements`,
  `exportCrossCompilationMetadataForMacosArm64ApiElements`,
  `exportCrossCompilationMetadataForMingwX64ApiElements`,
  `exportCrossCompilationMetadataForTvosArm64ApiElements`,
  `exportCrossCompilationMetadataForTvosSimulatorArm64ApiElements`,
  `exportCrossCompilationMetadataForWatchosArm32ApiElements`,
  `exportCrossCompilationMetadataForWatchosArm64ApiElements`,
  `exportCrossCompilationMetadataForWatchosDeviceArm64ApiElements`,
  `exportCrossCompilationMetadataForWatchosSimulatorArm64ApiElements`,
  `exportTargetPublicationCoordinatesForAndroidApiElements`,
  `exportTargetPublicationCoordinatesForAndroidRuntimeElements`,
  `exportTargetPublicationCoordinatesForAndroidNativeArm32ApiElements`,
  `exportTargetPublicationCoordinatesForAndroidNativeArm64ApiElements`,
  `exportTargetPublicationCoordinatesForAndroidNativeX86ApiElements`,
  `exportTargetPublicationCoordinatesForAndroidNativeX64ApiElements`,
  `exportTargetPublicationCoordinatesForIosArm64ApiElements`,
  `exportTargetPublicationCoordinatesForIosSimulatorArm64ApiElements`,
  `exportTargetPublicationCoordinatesForIosX64ApiElements`,
  `exportTargetPublicationCoordinatesForJsApiElements`,
  `exportTargetPublicationCoordinatesForJsRuntimeElements`,
  `exportTargetPublicationCoordinatesForJvmApiElements`,
  `exportTargetPublicationCoordinatesForJvmRuntimeElements`,
  `exportTargetPublicationCoordinatesForLinuxArm64ApiElements`,
  `exportTargetPublicationCoordinatesForLinuxX64ApiElements`,
  `exportTargetPublicationCoordinatesForMacosArm64ApiElements`,
  `exportTargetPublicationCoordinatesForMingwX64ApiElements`,
  `exportTargetPublicationCoordinatesForTvosArm64ApiElements`,
  `exportTargetPublicationCoordinatesForTvosSimulatorArm64ApiElements`,
  `exportTargetPublicationCoordinatesForWasmJsApiElements`,
  `exportTargetPublicationCoordinatesForWasmJsRuntimeElements`,
  `exportTargetPublicationCoordinatesForWasmWasiApiElements`,
  `exportTargetPublicationCoordinatesForWasmWasiRuntimeElements`,
  `exportTargetPublicationCoordinatesForWatchosArm32ApiElements`,
  `exportTargetPublicationCoordinatesForWatchosArm64ApiElements`,
  `exportTargetPublicationCoordinatesForWatchosDeviceArm64ApiElements`, and
  `exportTargetPublicationCoordinatesForWatchosSimulatorArm64ApiElements`.

The validation rule is the whole set, not whichever tasks happen to be cheap on
the current host. If the local machine cannot execute or link a task for a
configured target, use the matching GitHub Actions workflow or another real
runner for that platform and report the run URL and conclusion.

## Workflow-shape audit

At the start of every slot, inspect each focused repo's
`.github/workflows/*.yml` and `.github/workflows/*.yaml` files and make sure
they match the quiet patterns used by sibling repos. The historical bad shape
that must not regress is:

```yaml
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]
```

If another agent has already touched the workflow YAML, still verify the file
and the remote workflow enable/disable state against the patterns below; do not
assume the cost issue is resolved just because the file changed.

Follow the quiet patterns from sibling repos that did not have the bad
push/PR trigger:

- `ci.yml`: `workflow_dispatch:` only. It may orchestrate reusable platform
  workflows, but those platform workflow files must use `workflow_call:` only.
- `codeql.yml`: weekly `schedule` plus `workflow_dispatch:` only; no `push`
  and no `pull_request`.
- `publish.yml`: `release` with `types: [released]` plus `workflow_dispatch:`
  only. Manual dispatch must be safe by default; do not add PR or push
  publishing triggers.
- Platform workflows such as `ios.yml`, `linux.yml`, `windows.yml`, `js.yml`,
  `macos.yml`, `tvos.yml`, `watchos.yml`, `android.yml`,
  `android-native.yml`, `linux.yml`, `windows.yml`, `js.yml`, and `wasm.yml`:
  `workflow_call:` only unless a repo-local document gives a concrete approved
  exception.

When a workflow still has the old paid trigger and is enabled remotely, avoid
spending one last full CI run on the fix push: use
`gh workflow disable <workflow>` for that repo, commit and push the trigger
change, then re-enable the workflow after the new quiet trigger is on `main`.
If `gh workflow disable` is blocked, report the exact blocker and still make
the YAML change locally when safe.

Every slot report must include a workflow-shape audit line for each focused repo:
which workflow files were checked, whether any `push`/`pull_request` triggers
remain, whether any remote workflows were disabled/re-enabled, and the commit
that changed them. A report that skips this audit is incomplete.

## Constructive outcome requirement

This rotation is not a read-only monitor or a status-digest job. A valid slot
must leave each focused repo materially better than it was at the start and
must include a real source-porting advance. Infrastructure repair is welcome,
but it is not enough by itself. After the required branch, PR, CI, Dependabot,
and template inspection, do not stop at "nothing to merge," "remote Actions did
not run," "local build passed," or "the workflows are quiet." Keep moving until
the repo meets its local `CLAUDE.md` completion contract and all configured
targets build.

Work in this order, but do not treat an earlier item as permission to skip
source porting or all-target validation:

1. Reconcile branch state: merge an open or stale branch through the required
   no-fast-forward flow, close the PR after merge, or delete a remote branch
   only after proving it is already an ancestor of `main`.
2. Fix a real build, CI, CodeQL, Dependabot, JS-toolchain, Android SDK,
   publishing, or workflow defect; verify it locally; commit it; push it.
3. Advance the port according to the focused repo's local `CLAUDE.md` /
   `AGENTS.md`: use the required ast-distance or repo-local inventory, choose a
   complete upstream file/function/test gap, read the upstream Rust source, port
   a coherent slice, run the appropriate gate, commit it, and push it.
4. If the repo has releasable code that is not published, or a failed publish
   workflow for the current version, fix the publish blocker or run/dispatch
   the repo's documented Maven Central publishing path when credentials and
   permissions allow it.
5. Return to source porting after any infrastructure repair. A run that only
   fixes CI plumbing has not satisfied this automation unless the repo-local
   `CLAUDE.md` explicitly says the repo has no remaining source-porting work
   and the all-target build gate has been proved.

The following are not sufficient outcomes by themselves:

- Fetching/pruning and reporting that there are no open PRs.
- Listing old workflow failures without fixing a current local cause.
- Running tests against unchanged code and reporting that they pass.
- Pushing an already-existing local commit without verifying that it resolves a
  concrete branch, security, CI, publishing, or porting objective.
- Reporting that GitHub Actions are disabled, queued, absent, or did not appear
  after push.
- Passing a macOS local build while iOS, tvOS, watchOS, Swift export, Windows,
  Linux, Android, Android Native, JS, Wasm-JS, Wasm-WASI, XCFramework,
  publication, or other configured targets remain unbuilt or unverified.
- Merging or reporting a workflow-only fix without also porting a real upstream
  source slice.

If a slot truly cannot make a constructive change, the report must say the slot
is blocked, not complete, and name the exact blocker: for example, user-owned
dirty changes that conflict with the focused files, missing upstream Rust
source, no runnable `ast_distance` in a repo that requires it, missing Maven
publishing credentials, an unpublished dependency that cannot resolve, a
platform runner that cannot be dispatched, or a failing gate whose root cause is
outside the focused repo. A no-change report without that level of blocker is a
failed slot.

## Scope

The main job is CI/build/branch repair:

1. Use `gh` CLI to inspect the focused repo's open PRs, failed workflow runs,
   failed checks, and logs.
2. Merge PR branches and active work together into one branch per repo. Do not
   split work into a fresh branch per fix.
3. For every PR or branch being reconciled, first merge the current main branch
   into that branch with a real merge commit, resolve conflicts and build issues
   there, verify locally, then merge the cleaned branch back to main.
4. Use no fast-forward merges for this work.
5. Close PRs with `gh` after their branch has been merged to main.
6. Branch ownership does not matter. A slot owns the merge state of every branch
   in its focused repo, including stale remote branches, prior automation
   branches, Dependabot branches, and branches created by humans. Do not skip a
   branch just because this run did not create it.
7. At the start and end of branch repair, fetch/prune and enumerate focused-repo
   remote heads and PRs. For each non-main branch: merge it to main through the
   no-fast-forward reconciliation flow above, or, if it is already an ancestor
   of main, delete the stale remote branch after verifying ancestry with
   `git merge-base --is-ancestor <branch> main`. If a branch cannot be merged
   or safely deleted, report the concrete blocker.
8. Compare Gradle, Kotlin, Node/Yarn, lockfile, CodeQL, Dependabot, and workflow
   shape against `../BUILD_TEMPLATE_HANDOFF.md`,
   `mime-kotlin`, and the quiet workflow patterns above; keep repo-specific
   substitutions intact.
9. Fix concrete build, CI, merge, typo, and missing implementation issues when
   the code is present enough to repair. Do not add stubs, placeholder bodies,
   `TODO()`, fake implementations, or `@Suppress`.
10. After every push to remote, dispatch or inspect the repo's documented CI
    workflows when they are the only way to prove configured targets on their
    real host. Do not claim iOS, tvOS, watchOS, Swift export, Windows, Linux,
    Android, Android Native, JS, Wasm-JS, Wasm-WASI, XCFramework, publication,
    or other configured targets are validated unless the corresponding local or
    remote job actually ran to completion.
    If the needed runner, workflow, token, or permission is unavailable, keep
    repairing locally where possible and report the exact platform-validation
    blocker instead of substituting a narrower build.

Dependabot PRs and branches are normal integration work for this rotation. If
Dependabot opens a PR for a focused repo, merge that dependency change into
`main` as part of the slot's responsibilities, preserving the repo's required
lockfile/update workflow. If post-push Dependabot jobs reveal blocked security
updates without an open PR, carry the update intent into `main` directly when
the fix is concrete and local verification can prove it.

Remote GitHub Actions are not optional when they are the only available proof
for a configured target. Quiet workflow triggers remain required to control
cost, but manual dispatch is part of validation when the repo's full target
surface cannot be proved on the local machine. If a required run stays queued,
has no jobs assigned, cannot be dispatched, or never appears after manual
dispatch, record the exact run/workflow/ref/token state and continue every
other repair and porting step that can be completed. Such a slot is blocked on
that platform validation; it is not successful.

Remote workflow logs are also repair inputs. When a remote job fails for the
focused repo, fetch the failed logs, fix the concrete cause in the focused repo
or a direct dependency allowed by this runbook, and rerun the needed workflow.
Do not stop at "remote Actions failed" when the failure is actionable.

CodeQL and Dependabot tooling are installed locally for this workspace. When a
remote CodeQL or Dependabot job is disabled, queued, or unavailable, prefer
local investigation and reproduction over waiting for GitHub Actions. For
CodeQL, use the repo's local Gradle extraction/build tasks and any locally
available CodeQL CLI workflow. For Dependabot security work, inspect the local
Gradle/Yarn/npm state, update the build files and tracked lockfiles with the
repo's documented upgrade tasks, and prove the result locally.

## Porting is part of CI/build repair

Do not treat CI repair as separate from porting. These Kotlin repos are ports
of upstream Rust code under each repo's `tmp/` tree. Every slot must port or
materially advance real source in each focused repo. Whenever the run exposes a
gap against that Rust source and the gap is within the focused repo, close it as
part of the work.

If branch/PR/security/template triage finds no concrete repair to make,
porting is still required. Do not end the run just because CI is clean, remote
Actions are quiet, dependencies are current, or there are no open PRs. The
focused repo's local `CLAUDE.md` drives porting work: read it before choosing
an objective, follow its inventory and gates, and only fall back to the
workspace `CLAUDE.md` plus repo `AGENTS.md` / `README.md` when the repo
genuinely has no `CLAUDE.md`. In parity-mode repos this normally means running
the repo's ast-distance workflow before choosing work; if no runnable
ast-distance is available where the repo requires one, that is a blocker to
report rather than permission to return a status-only update.

Mandatory porting discipline for this automation:

- Read the workspace `AGENTS.md` and `CLAUDE.md`, then the focused repo's
  `AGENTS.md`, `CLAUDE.md`, and `README.md`, before choosing or changing work.
  For porting, the repo-local `CLAUDE.md` is the driver; do not substitute a
  generic branch-merge or CI checklist for the porting objective it names.
- Confirm the upstream Rust source exists under the repo's documented `tmp/`
  path before touching Kotlin source. If the repo has `tools/ast_distance/`,
  use the repo's documented ast-distance workflow as the inventory for missing
  files, missing functions, provenance/header drift, and cheat-detection
  failures.
- If a compiler/test/build failure points to an unported Rust item, port the
  Rust item faithfully instead of papering over the failure with a local shim.
- If a build-system change reveals missing generated artifacts or a target gap,
  still inspect whether a Rust/Kotlin parity gap is involved before declaring
  the work done.
- If touching a Kotlin file mapped to Rust, read the whole upstream `.rs` file
  first, preserve the one Rust file to one Kotlin file mapping, translate
  top-to-bottom in upstream order, and keep comments Kotlin-facing translations
  of upstream content.
- Do not add stubs, placeholders, `TODO()`, fake implementations, or
  `@Suppress(...)`. Missing behavior means the upstream dependency or item
  needs to be ported.
- Report any skipped porting gap explicitly as a blocker with the exact Rust
  path under `tmp/`, the Kotlin file or missing file involved, and the reason it
  could not be completed in the slot.
- Do not count generated inventories, comment-only churn, workflow edits,
  dependency bumps, or formatting as the required source-porting advance. The
  source slice must change Kotlin implementation or tests in a way that moves
  the repo closer to the upstream Rust behavior named by the local contract.

## Git rules

- Do not use `git worktree`.
- Do not use `/tmp` for scratch files. Put reports, logs, and helper outputs
  under the focused repo's `tmp/` folder or under
  `../automation-artifacts/`.
- Never force-push.
- Avoid `git pull`; use explicit `git fetch` and explicit merge commands.
- Before any fetch/merge/switch, inspect `git status --short --branch`,
  including untracked files.
- Never discard or revert changes you did not make. Work with existing dirty
  state; if it affects the task, fold it into the integration branch with a
  real commit.
- Do not leave remote branches behind in a focused repo when they have already
  been merged. Deleting a stale remote branch is allowed only after verifying it
  is an ancestor of `main`, or after the branch has been merged to `main` during
  the run. Never delete an unmerged branch to make the branch list look clean.
- Before ending the run, every dirty file in every focused repo must be staged
  and committed. There are no exceptions for files dirtied during the run.

## Translation/source guardrails

This is a translation workspace, not a redesign workspace. If a CI fix requires
touching Kotlin source:

- Read the whole upstream Rust file before changing the Kotlin file.
- Preserve one Rust file to one Kotlin file mapping.
- Translate top to bottom in upstream order.
- Keep comments as translated source content, not porting notes.
- Do not leak Rust syntax into Kotlin code, KDoc, or inline comments.
- Do not optimize, re-architect, or smooth the upstream document during a CI fix.

## Verification, validation, and after-action report

The report is not a duty by itself and never counts as the slot outcome. It is
only the evidence trail after branch repair, build repair, porting, publishing,
or a named hard blocker has already been handled. Do not treat a request for a
list, summary, handoff, or shareable report as permission to skip validation or
implementation.

If the user provides a list of claimed changes, a commit summary, or a proposed
handoff, treat it as claims to verify against the focused repo. Validate the
claim set with the same obligations as a normal slot: inspect the current
branch and remote state, compare the actual files, search for stale paths or
forbidden patterns, run the stated local gates when they are relevant, check
commit ancestry and push state, and fix any concrete defect you find. Only
after that validation may you report which claims are true, false, obsolete, or
blocked. Do not praise, rewrite, or mark a user-provided change list as
"shareable" until the underlying work has been verified.

Run the narrowest local Gradle/test commands that prove the repair, then the
full all-target build gates required by the repo-local `CLAUDE.md` or, when it
is missing, the workspace `CLAUDE.md`. `./gradlew test`, `compileAndroidMain`,
or `build --dry-run` may be useful intermediate checks, but they are not a
substitute for proving the configured target surface. When a target cannot be
built or tested on the local host, dispatch the matching GitHub Actions
workflow or use another real runner for that platform. A local macOS build is
not Windows validation.

Before ending a successful slot, the report must include evidence for:

- the required source-porting slice, including the upstream Rust path and the
  Kotlin implementation or test files changed
- the repo-local `CLAUDE.md` contract used, or the explicit fallback to the
  workspace `CLAUDE.md`
- all configured target build validation, with local commands and remote run
  URLs for platform-specific jobs
- any platform or publication gate that remains blocked, with exact evidence

Include a concise final after-action report with:

- focused repo or repos
- branch used
- constructive outcome made, or the exact hard blocker that prevented one
- PRs/branches inspected, merged, or closed
- local commands run and outcomes
- remote workflow state, including required platform jobs such as Windows when
  local hardware cannot validate them
- commits made
- unresolved blockers

## 2026-05-19 Android SDK setup: move installer code into `build.gradle.kts`

Do not add or keep project-level `setup-android-sdk.sh` or
`setup-android-sdk.bat` files as the Android SDK installer path. The normalized
pattern makes Android SDK setup a Gradle/Kotlin concern inside each repo's
`build.gradle.kts`, so Windows CI does not depend on WSL, shell scripts, batch
files, PowerShell installer copies, Android Studio, a system SDK, or a sibling
repo's SDK.

Reference implementations:

- `../anyhow-kotlin/build.gradle.kts`
  (`0fd90ee Move Android SDK setup into Gradle`)
- `../serial-test-kotlin`
  (`dc29a78 Move Android SDK setup into Gradle`)

When applying this normalization to a repo, carry the whole pattern, not just
the task name:

1. Move Android SDK setup out of shell/batch files and into `build.gradle.kts`;
   delete tracked `setup-android-sdk.sh` / `setup-android-sdk.bat` installers.
2. Add the required imports: `java.io.ByteArrayInputStream`, `java.net.URI`,
   `java.nio.file.Files`, `java.nio.file.StandardCopyOption`,
   `java.util.zip.ZipInputStream`, and `org.gradle.api.GradleException`.
3. Use the workspace Android SDK pins exactly:

```kotlin
val androidCommandLineToolsRevision = "14742923"
val projectCompileSdk = "34"
val projectAndroidBuildTools = "36.0.0"
```

4. Keep the SDK project-local under `.android-sdk`; write or refresh
   `local.properties` with `sdk.dir=<repo>/.android-sdk` on every run.
5. Add host OS detection for macOS, Linux, and Windows, including
   `sdkmanager.bat` on Windows and `sdkmanager` elsewhere.
6. Add an `.install-complete` marker plus an actual package-presence predicate
   so repeated Gradle invocations are idempotent and fast only after the local
   SDK has the required packages. A marker alone is not enough. The cached path
   must verify `sdkmanager` and these directories before returning:

```kotlin
val requiredAndroidSdkPackageDirs = listOf(
    projectAndroidSdkDir.resolve("platform-tools"),
    projectAndroidSdkDir.resolve("platforms/android-$projectCompileSdk"),
    projectAndroidSdkDir.resolve("build-tools/$projectAndroidBuildTools"),
)

fun isProjectAndroidSdkInstalled(): Boolean =
    androidSdkInstallMarker.exists() &&
        androidSdkManager.exists() &&
        requiredAndroidSdkPackageDirs.all { it.exists() }
```

If the marker exists but any required package directory is missing, repair by
running `sdkmanager` again instead of trusting the stale cache.
7. Add `sdkManagerCommand(...)` so Gradle invokes SDK manager directly on
   Unix-like hosts and through `cmd /c` on Windows.
8. Download command-line tools from Gradle using
   `URI(...).toURL().openStream()`; do not depend on `curl`.
9. Extract with `ZipInputStream`; do not depend on `unzip`.
10. Guard against zip-slip by resolving extracted entries against the canonical
    `cmdline-tools/latest` directory before writing files.
11. Preserve executable permissions for non-Windows SDK binaries after
    extraction.
12. Accept licenses non-interactively from Gradle using a finite
    `ByteArrayInputStream` of `y\n` answers.
13. Install `platform-tools`, `platforms;android-34`, and
    `build-tools;36.0.0` from Gradle.
14. Preserve SDK manager output in `.android-sdk/sdkmanager-install.log`.
15. Fail installer errors with clear `GradleException` messages, including log
    contents where useful.
16. Call `installProjectAndroidSdk(...)` at configuration time before
    `kotlin { android { ... } }`, because the Android Gradle plugin resolves
    SDK location during configuration.
17. Convert `tasks.register<Exec>("setupAndroidSdk")` into a Kotlin-backed task
    that calls `installProjectAndroidSdk(...)`; it must not shell out.

The workflow YAML is part of this fix. Every workflow command that touches
Android work must invoke the same Gradle entrypoint first:

- `android.yml`: run `setupAndroidSdk` before `compileAndroidMain`,
  `assembleUnitTest`, and `assembleAndroidTest`.
- `codeql.yml`: update comments and the `java-kotlin` CodeQL extraction step so
  `setupAndroidSdk` runs before `compileAndroidMain`, `compileKotlinJs`, and
  `compileKotlinWasmJs`. GitHub CodeQL still calls this `build-mode: manual`,
  but the workflow text must make clear that this is a narrow traced CodeQL
  extraction command, not the repo's full Gradle build contract. The real
  `build` task is wired in `build.gradle.kts` to compile/link the configured
  target surface.
- `publish.yml`: run `setupAndroidSdk` before both the dry-run
  `compileAndroidMain androidSourcesJar` command and the real
  `publishAndReleaseToMavenCentral` command.
- `windows.yml`: use the normalized multi-line Gradle command, with no WSL,
  bash, batch-file installer, or PowerShell duplicate:

```yaml
      - name: Build and test Windows target
        run: >
          ./gradlew
          setupAndroidSdk
          mingwX64Test
          --no-configuration-cache
```

This matters even though the Windows job's target test is `mingwX64Test`: the
job is the Windows-runner proof that `setupAndroidSdk` is callable through
Gradle/Kotlin alone.

Search the repo to prove no script path or old dispatch helper remains:

```bash
rg -n "setup-android-sdk\\.sh|setup-android-sdk\\.bat|androidSdkSetupCommand|bash.*setup-android-sdk|cmd.*setup-android-sdk" \
  . -g '!build/**' -g '!.gradle/**' -g '!.android-sdk/**'
```

Search workflows to prove Android-related commands include `setupAndroidSdk`
before Android work:

```bash
rg -n "setupAndroidSdk|compileAndroidMain|assembleUnitTest|androidSourcesJar|assembleAndroidTest|publishAndReleaseToMavenCentral" \
  .github/workflows -g '*.yml'
```

The expected result is that each workflow command containing an Android Gradle
task also contains `setupAndroidSdk` earlier in that command or in the
immediately preceding Gradle step.

Prove the Gradle-backed installer and build graph locally:

```bash
./gradlew setupAndroidSdk --no-daemon --console=plain --no-configuration-cache
./gradlew setupAndroidSdk --no-daemon --console=plain --no-configuration-cache   # repeat: must take the cached path
./gradlew compileAndroidMain --no-daemon --console=plain --no-configuration-cache
./gradlew build --dry-run --no-daemon --console=plain --no-configuration-cache
./gradlew test --no-daemon --console=plain --no-configuration-cache
git diff --check
```

The repeated `setupAndroidSdk` invocation must report the cached SDK path after
checking required packages; it must not reinstall solely because an Android
workflow requested an Android build.

## 2026-05-19 CodeQL manual JVM build: extract Android AAR classes

The manual `codeqlCompileJvm` pattern must not leave Android AAR extraction as
empty scaffolding. If a `codeqlCompileJvm` task compiles `commonMain` with the
embeddable compiler, any Android-only sibling artifacts needed on the compiler
classpath must be resolved as AARs, unzipped to their `classes.jar`, and those
extracted jars must be included in the CodeQL compiler classpath. The reference
implementation is:

- `../syn-kotlin/build.gradle.kts`
- `codeqlAndroidAar("io.github.kotlinmania:proc-macro2-kotlin-android:0.1.2")`
- `codeqlAndroidAar("io.github.kotlinmania:quote-kotlin-android:0.1.2")`

The same dedicated `codeqlAndroidAar` configuration pattern is also present in
`quote-kotlin` and `ansi-to-tui-kotlin`. Prefer that pattern over mixing AARs
into `codeqlSourceClasspath`.

Required shape for a repo that uses `codeqlCompileJvm` and needs Android
artifacts:

1. Define a resolvable, non-consumable `codeqlAndroidAar` configuration.
2. Add real published Android artifact coordinates to it. Do not add fake,
   test-only, or currently unpublished coordinates merely to make the list
   non-empty.
3. Add `inputs.files(codeqlAndroidAar).withNormalizer(ClasspathNormalizer::class.java)`.
4. In `doFirst`, resolve `codeqlAndroidAar`, copy `classes.jar` out of each AAR
   into `build/codeql/android-aar/<artifact>/classes.jar`, and append those
   extracted jars to `codeqlSourceClasspath.resolve()` before building the
   `-classpath` argument.
5. Run the CodeQL task and prove that at least one real AAR classes jar was
   extracted when the repo has Android AAR dependencies:

```bash
./gradlew --no-daemon codeqlCompileJvm --no-configuration-cache --console=plain
find build/codeql/android-aar build/generated/codeql-aar-classes -name classes.jar -print 2>/dev/null
```

If a repo has no Android AAR dependency yet, do not mark the AAR-extraction work
complete just because `codeqlAndroidAar.resolve()` returns an empty set. Either
populate it with real coordinates or record in the PR/run report that no
published Android AAR dependency exists yet.

### Current offender list from the 2026-05-19 audit

Audit details were saved at
`../automation-artifacts/2026-05-19-kotlinmania-ci-hourly-roster/codeql-aar-audit.md`.

Repos with `codeqlAndroidAar` extraction scaffolding but no
`codeqlAndroidAar(...)` dependencies:

- `../anyhow-kotlin`
- `../derive-more-kotlin`
- `../dotenvy-kotlin`
- `../ed25519-dalek-kotlin`
- `../envie-kotlin`
- `../hmac-kotlin`
- `../include-dir-kotlin`
- `../lalrpop-util-kotlin`
- `../log-kotlin`
- `../tokio-kotlin`
- `../unicode-ident-kotlin`

Repos with manual CodeQL JVM setup that still need to be corrected or
normalized to the `syn-kotlin` AAR-extraction pattern:

- `../maplit-kotlin` depends on
  `io.github.kotlinmania:btree-kotlin:0.2.1`, but has no Android AAR extraction
  path in its `codeqlCompileJvm` classpath.
- `../tungstenite-kotlin` extracts AAR
  `classes.jar` files from `codeqlSourceClasspath`, including
  `io.github.kotlinmania:bytes-kotlin-android:0.2.0`, but should be normalized
  to the dedicated `codeqlAndroidAar` configuration used by `syn-kotlin`.
- `../gethostname-kotlin` has a CodeQL
  workflow that runs `./gradlew codeqlCompileJvm`, but `build.gradle.kts` does
  not define that task.

Specific blocker found while checking `hmac-kotlin`: the tempting coordinates
`io.github.kotlinmania:sha1-kotlin-android:0.1.0` and
`io.github.kotlinmania:sha2-kotlin-android:0.1.0` do not currently resolve from
Google Maven or Maven Central. Do not add those to `hmac-kotlin` until those
Android artifacts are actually published or another real Android AAR dependency
exists.

## 2026-05-19 build gate incident: `build` must compile every configured target

### Summary

During slot `Kotlinmania CI H08.3` for
`../async-channel-kotlin`, local verification
exposed a serious build-contract gap: `./gradlew build` could report success
without producing all configured Kotlin target build assets. The repo had the
standard broad Kotlinmania target surface, but the default Gradle `build` graph
did not link every native test binary and did not assemble the registered
XCFrameworks.

This is not an `async-channel-kotlin`-only concern. A sibling-repo survey showed
the same target declarations and the same host-portable custom `test` task
pattern across many `*-kotlin` repos, but no existing workspace-wide pattern
that wires `build` to all target binary aggregators. Treat this as a systemic
audit item: most generated-template repos are likely capable of the same false
positive until proven otherwise.

### What happened in `async-channel-kotlin`

The focused repo declares these configured Kotlin targets in
`build.gradle.kts`:

- Android KMP target with main, host test, and device test builders.
- JVM.
- JS browser and Node.
- Wasm-JS browser and Node.
- Wasm-WASI Node.
- Apple native framework targets:
  `macosArm64`, `iosArm64`, `iosSimulatorArm64`, `iosX64`,
  `tvosArm64`, `tvosSimulatorArm64`, `watchosArm32`, `watchosArm64`,
  `watchosDeviceArm64`, and `watchosSimulatorArm64`.
- Non-Apple native targets: `linuxX64`, `linuxArm64`, and `mingwX64`.
- Android Native targets:
  `androidNativeArm32`, `androidNativeArm64`, `androidNativeX86`, and
  `androidNativeX64`.

Initial `./gradlew build --no-daemon` succeeded, but artifact inspection showed
that success was incomplete for the stricter Kotlinmania requirement that
`build` compile/link every possible configured target:

- Compile outputs existed under `build/classes/kotlin/<target>` for every
  configured target.
- Publication-facing jars/KLIBs and the Android AAR existed.
- Host-runnable test reports existed for the usual host-portable set:
  macOS, iOS simulator, tvOS simulator, watchOS simulator, JVM, JS, Wasm-JS,
  Wasm-WASI, and Android host tests.
- But `build` did not assemble the registered XCFrameworks.
- But `build` did not link every native debug test executable. Missing before
  explicit supplemental commands included the Android Native test binaries,
  `linuxArm64`, and Apple device-target test binaries such as `iosArm64`,
  `tvosArm64`, `watchosArm32`, `watchosArm64`, and
  `watchosDeviceArm64`.

This means the old `build` task was an incomplete verification gate. It proved
some host-portable tests and many compile outputs, but it did not prove the full
configured target surface.

### Sibling-repo pattern checked

Before patching, sibling projects were inspected for existing Linux/MinGW and
Android Native handling. The shared pattern was:

- Repos declare `linuxX64()`, `linuxArm64()`, `mingwX64()`, and all four
  `androidNative*()` targets.
- Repos define a custom `test` task described as host-portable:
  macOS, JS, Wasm-JS, Android unit, and related host-runnable checks.
- That `test` task intentionally does not run non-host native tests.

That host-portable `test` shape is reasonable for runnable tests, but it is not
a sufficient `build` contract. The corrected interpretation is:

- Keep `test` host-portable unless a repo has a reason to make it heavier.
- Make `build` the strict artifact gate that compiles and links every configured
  target, including non-host-native `*TestBinaries`.

### Commands used to prove the gap and the fix

The following commands were used during the investigation and repair:

```bash
./gradlew build --no-daemon
find build/libs -maxdepth 1 -type f -print
find build/bin -maxdepth 4 -type f -print
find build/classes/kotlin -maxdepth 4 -type d -print
find build/XCFrameworks -maxdepth 8 -type f -print
```

Supplemental commands proved what `build` had missed:

```bash
./gradlew \
  androidNativeArm32Binaries androidNativeArm64Binaries \
  androidNativeX64Binaries androidNativeX86Binaries \
  iosArm64Binaries iosSimulatorArm64Binaries iosX64Binaries \
  linuxArm64Binaries linuxX64Binaries macosArm64Binaries mingwX64Binaries \
  tvosArm64Binaries tvosSimulatorArm64Binaries \
  watchosArm32Binaries watchosArm64Binaries \
  watchosDeviceArm64Binaries watchosSimulatorArm64Binaries \
  assembleAsyncChannelXCFramework \
  --no-daemon

./gradlew \
  androidNativeArm32TestBinaries androidNativeArm64TestBinaries \
  androidNativeX64TestBinaries androidNativeX86TestBinaries \
  iosArm64TestBinaries iosSimulatorArm64TestBinaries iosX64TestBinaries \
  linuxArm64TestBinaries linuxX64TestBinaries macosArm64TestBinaries \
  mingwX64TestBinaries tvosArm64TestBinaries \
  tvosSimulatorArm64TestBinaries watchosArm32TestBinaries \
  watchosArm64TestBinaries watchosDeviceArm64TestBinaries \
  watchosSimulatorArm64TestBinaries \
  --no-daemon
```

After patching `build.gradle.kts`, these commands were used as acceptance
checks:

```bash
./gradlew build --dry-run --console=plain --no-daemon
./gradlew clean build --no-daemon
./gradlew build --no-daemon
```

The dry-run must show the previously missed work in the `build` graph,
including:

- `linkDebugTestAndroidNativeArm32`
- `linkDebugTestAndroidNativeArm64`
- `linkDebugTestAndroidNativeX64`
- `linkDebugTestAndroidNativeX86`
- `linkDebugTestLinuxArm64`
- `linkDebugTestMingwX64`
- Apple device and simulator test links, including `iosArm64`,
  `tvosArm64`, `watchosArm32`, `watchosArm64`, and
  `watchosDeviceArm64`
- `assembleAndroidDeviceTest` / `assembleAndroidTest`
- `assembleAsyncChannelDebugXCFramework`
- `assembleAsyncChannelReleaseXCFramework`

The clean build passed:

```text
BUILD SUCCESSFUL in 5m 33s
225 actionable tasks: 213 executed, 12 up-to-date
```

The final non-clean build on the committed patch passed:

```text
BUILD SUCCESSFUL in 12s
210 actionable tasks: 6 executed, 204 up-to-date
```

### Patch pattern applied in `async-channel-kotlin`

Commit:

```text
35cb05a Make build compile every target
```

Local branch state after commit:

```text
main...origin/main [ahead 1]
```

The direct push to `origin/main` was blocked by the approval policy for
default-branch pushes, so CI has not seen this commit yet.

The patch adds an explicit full-target build contract near the existing custom
`test` task:

```kotlin
val fullTargetBuildTaskNames = setOf(
    "compileAndroidMain",
    "compileAndroidHostTest",
    "compileAndroidDeviceTest",
    "assembleAndroidMain",
    "assembleUnitTest",
    "assembleAndroidTest",
    "jvmMainClasses",
    "jvmTestClasses",
    "jsMainClasses",
    "jsTestClasses",
    "wasmJsMainClasses",
    "wasmJsTestClasses",
    "wasmWasiMainClasses",
    "wasmWasiTestClasses",
    "androidNativeArm32Binaries",
    "androidNativeArm32TestBinaries",
    "androidNativeArm64Binaries",
    "androidNativeArm64TestBinaries",
    "androidNativeX64Binaries",
    "androidNativeX64TestBinaries",
    "androidNativeX86Binaries",
    "androidNativeX86TestBinaries",
    "iosArm64Binaries",
    "iosArm64TestBinaries",
    "iosSimulatorArm64Binaries",
    "iosSimulatorArm64TestBinaries",
    "iosX64Binaries",
    "iosX64TestBinaries",
    "linuxArm64Binaries",
    "linuxArm64TestBinaries",
    "linuxX64Binaries",
    "linuxX64TestBinaries",
    "macosArm64Binaries",
    "macosArm64TestBinaries",
    "mingwX64Binaries",
    "mingwX64TestBinaries",
    "tvosArm64Binaries",
    "tvosArm64TestBinaries",
    "tvosSimulatorArm64Binaries",
    "tvosSimulatorArm64TestBinaries",
    "watchosArm32Binaries",
    "watchosArm32TestBinaries",
    "watchosArm64Binaries",
    "watchosArm64TestBinaries",
    "watchosDeviceArm64Binaries",
    "watchosDeviceArm64TestBinaries",
    "watchosSimulatorArm64Binaries",
    "watchosSimulatorArm64TestBinaries",
    "assembleAsyncChannelXCFramework",
)

tasks.named("build") {
    dependsOn(fullTargetBuildTaskNames)
}

afterEvaluate {
    tasks.named("build") {
        dependsOn(
            tasks.matching {
                name.endsWith("MainClasses") ||
                    name.endsWith("TestClasses") ||
                    name.endsWith("Binaries") ||
                    name.endsWith("XCFramework")
            },
        )
    }
}
```

Why both explicit and dynamic wiring:

- The explicit set is the audit contract for the repo's current target surface.
  It makes missing target work visible in review.
- The dynamic late matcher is a safety net for future generated tasks, but it
  was not enough by itself during testing. A dynamic-only attempt initially
  picked up class/compile tasks but dropped some native test binary links from
  the dry-run graph. Keep the explicit list unless a better template-level API
  is introduced and proven.

### Artifact evidence after the fix

After the strict build ran, native test executables existed for all configured
native targets:

```text
build/bin/androidNativeArm32/debugTest/test.kexe
build/bin/androidNativeArm64/debugTest/test.kexe
build/bin/androidNativeX64/debugTest/test.kexe
build/bin/androidNativeX86/debugTest/test.kexe
build/bin/iosArm64/debugTest/test.kexe
build/bin/iosSimulatorArm64/debugTest/test.kexe
build/bin/iosX64/debugTest/test.kexe
build/bin/linuxArm64/debugTest/test.kexe
build/bin/linuxX64/debugTest/test.kexe
build/bin/macosArm64/debugTest/test.kexe
build/bin/mingwX64/debugTest/test.exe
build/bin/tvosArm64/debugTest/test.kexe
build/bin/tvosSimulatorArm64/debugTest/test.kexe
build/bin/watchosArm32/debugTest/test.kexe
build/bin/watchosArm64/debugTest/test.kexe
build/bin/watchosDeviceArm64/debugTest/test.kexe
build/bin/watchosSimulatorArm64/debugTest/test.kexe
```

XCFramework outputs also existed:

```text
build/XCFrameworks/debug/AsyncChannel.xcframework
build/XCFrameworks/release/AsyncChannel.xcframework
```

### Rollout guidance for other repos

For each `*-kotlin` repo with the standard broad target matrix:

1. Inspect the target declarations in `build.gradle.kts`.
2. Run `./gradlew build --dry-run --console=plain --no-daemon`.
3. Confirm that `build` includes every configured target's main and test
   binary aggregator where Gradle exposes one.
4. Confirm that Android main/host/device compile and assemble tasks are present
   when the Android KMP target is configured.
5. Confirm that registered XCFramework tasks are present when the repo defines
   `XCFramework(...)`.
6. If the graph is incomplete, add the same explicit `fullTargetBuildTaskNames`
   pattern, substituting the repo-specific XCFramework task name.
7. Prove with:

```bash
./gradlew build --dry-run --console=plain --no-daemon
./gradlew clean build --no-daemon
```

Do not rely on `./gradlew test` for this requirement. In this workspace,
`test` is intentionally host-portable in many repos. The requirement that all
possible Kotlin targets are built belongs on `build`, not on `test`.

For long target surfaces, a single clean `build` may exceed an interactive
session limit while still doing useful work. Do not count a killed or truncated
session as a pass. If that happens, split only to isolate and materialize the
expensive pieces, then run a final `./gradlew build --no-daemon
--console=plain --no-configuration-cache` that reaches `BUILD SUCCESSFUL`.
Also inspect artifacts after the final build:

```bash
find build/bin -maxdepth 4 -type f \( -name 'test.kexe' -o -name 'test.exe' \) -print | sort
find build/XCFrameworks -maxdepth 5 -type d -name '*.xcframework' -print | sort
```

For a standard broad-target repo, expected native test executables include all
configured Android Native, iOS, Linux, macOS, MinGW, tvOS, and watchOS targets;
expected XCFramework outputs include both debug and release directories with
the repo-specific framework name.

### Current blocker

The original local-only blocker was cleared after sandbox permissions changed:

```text
35cb05a Make build compile every target
origin/main contains 35cb05a
```

Immediate `gh run list --commit 35cb05a` did not show a run, so remote CI
still needs a later GitHub Actions visibility check if a hosted status is
required. The local build gate itself is proven by `./gradlew build --no-daemon`
on the expanded task graph.

## Async-channel porting follow-up from H08.3

The build-gate work exposed an upstream parity gap: `tmp/async-channel/tests/`
had both `bounded.rs` and `unbounded.rs`, but the Kotlin repo only had a mapped
`UnboundedTest.kt`. Per the slot rule above, the run did not stop at build
wiring; it ported a bounded-channel slice before ending.

### Files changed

- `src/commonMain/kotlin/io/github/kotlinmania/asyncchannel/Sender.kt`
    - Added `Sender.forceSend(msg: T): ForceSendOutcome<T>`.
    - Added `ForceSendOutcome` to model upstream
      `Result<Option<T>, SendError<T>>`.
- `src/commonTest/kotlin/io/github/kotlinmania/asyncchannel/BoundedTest.kt`
    - New `// port-lint: source tests/bounded.rs` file.
    - Ports feasible bounded tests for smoke, capacity, len/empty/full,
      try-receive, receive, try-send, send, force-send, close/error paths,
      sender/receiver counts, receiver wakeup, SPSC/MPMC stress, and weak
      sender/receiver upgrade behavior.
- `build.gradle.kts`
    - Added a `js.nodejs` Mocha timeout of `5m` so the faithful Rust-sized SPSC
      and MPMC stress ports are not capped by Mocha's default two-second timeout.
    - Kept browser and Wasm test harnesses unchanged; a broader first pass caused
      unnecessary JS package/lock churn and was narrowed back before commit.

### Validation commands

```bash
./gradlew jvmTest --no-daemon
./gradlew kotlinUpgradeYarnLock kotlinWasmUpgradeYarnLock --rerun-tasks --no-daemon
./gradlew jsNodeTest --no-daemon
./gradlew build --no-daemon
git diff --check
```

Results:

- `jvmTest` passed after trimming tests that expose known semantic blockers.
- `jsNodeTest` initially failed only on bounded `spsc` and `mpmc` timeouts.
  The `js.nodejs` Mocha timeout fix preserved the Rust-sized stress counts and
  made the target pass.
- The final `./gradlew build --no-daemon` passed with `210 actionable tasks:
  10 executed, 200 up-to-date`.
- `kotlin-js-store/yarn.lock` returned to no diff after the timeout change was
  narrowed to JS Node only.

### Remaining parity blockers

These are real gaps against `tmp/async-channel/src/lib.rs` and
`tmp/async-channel/tests/bounded.rs`, not test harness failures:

- `Sender.closed()` / the upstream `Closed` future is not ported.
- `send_blocking` and `recv_blocking` are not ported for non-wasm/std targets.
- Stream/`next()` behavior is not ported, so `mpmc_stream` is still blocked.
- `close_wakes_sender` is not passing: the current Kotlin bounded channel does
  not wake a suspended sender when the receiver closes.
- Drop semantics are not modeled; sender/receiver counts only increase on
  `clone()` and are not decremented by object lifetime the way upstream Rust
  handles `Drop`.
- The concurrent bounded `len <= CAP` invariant from upstream failed during
  investigation because the current Kotlin size counter can temporarily exceed
  capacity under pressure.

The next async-channel porting pass should start with close/drop semantics and
then revisit the skipped `close_wakes_sender`, blocking APIs, and stream tests.

Embedded runbook obligations: read ../AGENTS.md, ../CLAUDE.md, ../BUILD_TEMPLATE_HANDOFF.md, and in each focused repo AGENTS.md, CLAUDE.md if present, and README.md before choosing work. This is not a read-only monitor and not only a branch-merge task. A valid slot leaves each focused repo materially better or reports the exact hard blocker.

First audit workflow shape against sibling quiet patterns: ci.yml uses workflow_dispatch only, codeql.yml uses weekly schedule plus workflow_dispatch, publish.yml uses release[released] plus workflow_dispatch, and platform workflows use workflow_call only. If old push or pull_request triggers remain, fix them or report the blocker; otherwise continue with branch, build, dependency, publishing, or port work.

Constructive work order: inspect git status, open PRs, remote heads, failed runs or logs, and Gradle, Kotlin, Node/Yarn, lockfile, CodeQL, Dependabot, and template shape. Reconcile branches through one repo branch using no-fast-forward merges: merge current main into the work branch, verify locally, merge the cleaned branch back to main, close merged PRs, and delete only remote branches proven ancestors of main. Branch ownership is irrelevant.

If branch repair is exhausted, fix an actual build, CI, CodeQL, Dependabot, JS toolchain, Android SDK, publishing, or workflow defect with local verification, then commit and push. Remote GitHub Actions are informational only; do brief gh checks after pushes and do not wait on disabled, queued, absent, or missing runs as proof.

Android SDK build normalization: when a repo still has shell setup or stale Android workflow shape, use serial-test-kotlin dc29a78 or anyhow-kotlin 0fd90ee as the pattern. Move setup into build.gradle.kts, delete setup-android-sdk.sh and setup-android-sdk.bat, add ByteArrayInputStream URI Files StandardCopyOption ZipInputStream GradleException imports, use pins command-tools 14742923 compileSdk 34 build-tools 36.0.0, keep .android-sdk project-local, refresh local.properties, handle macOS/Linux/Windows including sdkmanager.bat, add install marker, sdkManagerCommand helper, Gradle URI downloader, ZipInputStream extraction with zip-slip guard, executable permissions, noninteractive license acceptance, package install, sdkmanager-install.log, and clear GradleException failures. Call installProjectAndroidSdk before configuring the Android target and make setupAndroidSdk a Kotlin-backed task, not Exec. Update android, CodeQL, publish, and Windows workflows so setupAndroidSdk runs before Android work or publishing; no WSL, bash, batch, or PowerShell duplicate installer. Verify with stale-reference rg, workflow rg, setupAndroidSdk, compileAndroidMain, build --dry-run, test, and git diff --check.

Porting is the default next action when there is no concrete infra repair. Repo-local CLAUDE.md drives porting work: use its objectives, inventory, and gates; if it is missing, say so and fall back to AGENTS.md plus README.md. Use ast-distance when required, confirm upstream Rust under tmp, read the whole upstream .rs file before Kotlin edits, preserve file mapping and upstream order, and translate comments into Kotlin-facing wording. No stubs, TODO calls, suppress annotations, fake implementations, Rust syntax in Kotlin/KDoc/comments, or hiding missing dependencies.

Git hygiene: do not use git worktree, do not use /tmp scratch, never force-push, avoid git pull, inspect status before fetch/merge/switch, never discard user changes, and stage and commit every dirty file produced in focused repos before ending.

Before ending, report the workflow-shape audit, repos, branch, constructive outcome or hard blocker, PRs and branches inspected/merged/closed, local commands and outcomes, remote workflow state if checked, commits, and unresolved blockers.
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
