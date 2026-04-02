# MixinLoadConditions (MLC)

`MixinLoadConditions` is a small Sponge Mixin extension that adds one new annotation for mixin classes:

```java
@LoadCondition(
    loadIf = {"corelib"},
    loadIfAny = {"moda", "modb"},
    ignoreIfAny = {"modc"}
)
@Mixin(SomeTarget.class)
public class ExampleMixin {
}
```

## Behavior

- `loadIf` means every listed mod id must be loaded or the mixin is skipped.
- `loadIfAny` means at least one listed mod id must be loaded or the mixin is skipped.
- `ignoreIfAny` means if any listed mod id is loaded, the mixin is skipped.
- `ignoreIf` remains supported as a backwards-compatible alias for the same any-of exclusion behavior.
- Either exclusion field wins if both positive and negative conditions would match.
- Empty arrays mean "no condition".

That lets you express rules like "only load when `corelib` is present and either
`moda` or `modb` is loaded".

## How it works

The library ships a tiny Forge bootstrap mixin config that registers a global Mixin `IExtension`. Right before Mixin applies pending mixins to a target class, the extension inspects each mixin class for `@LoadCondition` and removes mixins whose conditions fail.

## Notes

- This project currently only targets Forge.
- No Fabric or NeoForge support is currently present, open an issue/pr if you do require it.

## How to use in your project

First, add this repository to your project's build script:

```kts
repositories {
    maven("https://repo.expandium.net/releases")
}
```
Next, add the MixinExtras dependency to your project based on your build system:

**Latest version:** ![GitHub Release](https://img.shields.io/github/v/release/ko-lja/MixinLoadConditions)

<details><summary>ModDevGradle</summary>

```gradle
dependencies {
    compileOnly(annotationProcessor("lu.kolja:mixinloadconditions:0.1.2"))
    implementation(jarJar("lu.kolja:mixinloadconditions:0.1.2"))
}
```

Make sure to use the `-all` jar when bundling MLC yourself.

</details>
<details><summary>ForgeGradle</summary>

```gradle
dependencies {
    compileOnly(annotationProcessor("lu.kolja:mixinloadconditions:0.1.2"))
    implementation(jarJar("lu.kolja:mixinloadconditions:0.1.2")) {
        jarJar.ranged(it, "[0.1.2,)")
    }
}
```
</details>

### Credits
- This extension has taken some inspiration from Sponge Mixins itself and from MixinExtras
