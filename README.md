# Spring Lotus LIB

> To implement a specific API, this mod may modify some behaviors.
> If you encounter situations where some functions do not work after installing this mod,
> or if this mod breaks the behavior of other mods, please send me an issue.

A Minecraft dependency library mod for my personal use.

## Usage

Spring Lotus supports Fabric, NeoForge, and Forge (Forge compatibility is not provided from version 26.1 onwards).

```kotlin
repositories {
    mavenCentral()
    maven("https://mvnc.pkg.one/releases")
    maven("https://mvnc.pkg.one/snapshots")
}

dependencies {
    compileOnly("one.pkg.libsl:modcommon:[VERSION]")
}

```

Spring Lotus has been released on Modrinth and CurseForge, and you should not package it into your own mod as Shadow or
JarInJar.

### Fabric

```json
{
  "depends": {
    "springlotus": ">=1.0.0 <1.1.0"
  }
}
```

### NeoForge/Forge

```toml
[[dependencies."${mod_id}"]]
modId = "springlotus"
type = "required"
versionRange = "[1.0,)"
ordering = "AFTER"
side = "BOTH"
```

## Version Specification

SpringLotus adopts the following version specification:

```
B.A.N
1.0.0
11.2.1
```

As: Breaking API changes. Adding new APIs. No API changes.

## License

The source code is licensed under the LGPL-3.0-only license.

2026 404Setup. All rights reserved.

Some of the code for Event comes from the [Fabric API](https://github.com/FabricMC/fabric-api).

----

**[OreUI](https://github.com/mojang/ore-ui) is a product of Mojang Studio. Spring Lotus's OreUI custom component library
only references the design of OreUI and does not use any OreUI code. Spring Lotus is not affiliated with Mojang Studio.
**

Spring Lotus borrows a small portion of [Fabric API](https://github.com/FabricMC/fabric-api) code for a simpler
implementation, which is used to integrate a small number of cross platform APIs that I frequently use. Licensed under
the [Apache License 2.0](https://github.com/FabricMC/fabric-api/blob/26.1.1/LICENSE).