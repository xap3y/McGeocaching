# Bukkit project template

### Installation:

1. Clone this repository
```shell
git clone https://github.com/xap3y/TemplateBukkitGradle.git
cd TemplateBukkitGradle
```
2. Refactor the project \
    2.1 change the `group`, `baseCoordinates` and `version` in the `build.gradle` file. \
    2.2 change the `main` and `name` in the `src/main/resources/plugin.yml` file.

#### Fast installation (Linux only):

This will create the project in the active directory

```shell
curl -fsSL https://static.xap3y.tech/install.sh | bash
```

<br>
<details>
<summary> <b>List of features implemented in this project: </b></summary>

- [x] [Paper API 1.20.4](https://jd.papermc.io/paper/1.20.4/)
- [x] [XaLib](https://xalib.xap3y.eu/)
- [x] [XaGUI](https://xagui.xap3y.eu/docs)
- [x] [SkullCreator](https://skullcreator.xap3y.eu)
- [x] [Cloud V2](https://cloud.incendo.org/minecraft/paper/)
- [x] [Blossom](https://blossom.kyori.net)
- [x] [Lombok](https://projectlombok.org/)
- [x] [ParticleNativeAPI](https://github.com/Fierioziy/ParticleNativeAPI)
- [x] [XSeries](https://www.spigotmc.org/threads/xseries-xmaterial-xparticle-xsound-xpotion-titles-actionbar-etc.378136/)
- [x] [bStats](https://bstats.org/)
- [x] [AnvilGUI](https://github.com/WesJD/AnvilGUI)
- [x] [PlaceholderAPI](https://wiki.placeholderapi.com/developers/using-placeholderapi/)
- [x] [SkriptAPI](https://docs.skriptlang.org/javadocs/)
- [x] [TownyAdvanced](https://github.com/TownyAdvanced/Towny)
- [x] [WorldGuardAPI](https://worldguard.enginehub.org/en/latest/developer/)
- [x] [SlimeFun4API](https://slimefun.github.io/javadocs/Slimefun4/docs/)
- [x] [Item-NBT-API](https://github.com/tr7zw/Item-NBT-API)
- [x] [log4j-core](https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core)
- [x] [MariaDB Java Client](https://mariadb.com/kb/en/about-mariadb-connector-j/)
- [x] [LuckPerms API](https://luckperms.net/wiki/Developer-API#gradle)
- [x] [GriefPrevention](https://github.com/GriefPrevention/GriefPrevention/)
- [x] [JUnit](https://junit.org/junit5/) and [MockBukkit](https://github.com/MockBukkit/MockBukkit/tree/v1.20)
- [ ] [ProGuard](https://www.guardsquare.com/proguard) obfuscation

</details>
<br>

> [!WARNING]  
> Not all of the features above are enabled by default! \
> To enable them, you need to uncomment the corresponding lines in the `build.gradle` file.


### Building:

`./gradlew build` - automatically shades all dependencies and creates a single jar file in the `build/libs` directory.
