package eu.xap3y.egghunt.manager;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.*;
import eu.xap3y.egghunt.api.model.EggHuntConfig;
import eu.xap3y.egghunt.api.model.GeocachingConfig;
import eu.xap3y.egghunt.service.ParticleService;
import eu.xap3y.egghunt.service.Texter;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class ConfigManager {

    @Getter
    private static EggStorageDto eggStorageDto = new EggStorageDto();

    @Getter
    private static EggHuntConfig eggHuntConfig;

    @Getter
    private static GeocachingConfig geocachingConfig;

    private static YamlConfiguration yamlConfiguration;

    private static YamlConfiguration playerYamlConfiguration;

    @Getter
    private static ArrayList<TreasureDto> treasureDtoList = new ArrayList<>();

    private static final File file = new File(EggHunt.getInstance().getDataFolder(), "storage.yml");
    private static final File playerStorageFile = new File(EggHunt.getInstance().getDataFolder(), "player-storage.yml");

    public static void reloadConfig() {
        if (!EggHunt.getInstance().getDataFolder().exists()) {
            EggHunt.getInstance().getDataFolder().mkdir();
        }

        EggHunt.getInstance().saveDefaultConfig();
        EggHunt.getInstance().reloadConfig();

        FileConfiguration cfg = EggHunt.getInstance().getConfig();

        String prefix = cfg.getString("prefix");
        if (prefix == null) prefix = "&7[&bGeocaching&7] &r";
        EggHunt.setTexter(new Texter(prefix, false, null));

        eggHuntConfig = new EggHuntConfig(
                cfg.getBoolean("egghunt.enabled", true),
                cfg.getBoolean("egghunt.all_eggs_found_reward", false),
                cfg.getBoolean("egghunt.all_eggs_found_random_reward", false),
                cfg.getStringList("egghunt.all_eggs_found_rewards"),
                cfg.getString("egghunt.all_eggs_found_message", "")
        );

        EggHunt.getParticleService().restart();

        geocachingConfig = new GeocachingConfig(
                cfg.getBoolean("geocaching.enabled", true),
                cfg.getInt("geocaching.minimum_distance", 80)
        );

        EggHunt.getBeepService().cleanUp();
    }

    public static void removeTreasureLocation(TreasureDto treasureDto) {
        removeTreasureLocation(compileTreasureLocation(treasureDto));
        treasureDtoList.remove(treasureDto);
    }

    public static void removeTreasureLocation(String loc) {
        List<String> locations = yamlConfiguration.getStringList("treasures.locations");
        locations.remove(loc);
        yamlConfiguration.set("treasures.locations", locations);

        try {
            yamlConfiguration.save(file);
        } catch (Exception e) {
            EggHunt.getTexter().console("&4Nepodařilo se uložit konfiguraci storage.yml");
        }
    }

    private static void reloadTreasureLocations() {
        List<String> locations = yamlConfiguration.getStringList("treasures.locations");

        if (locations.isEmpty()) return;

        // Parse treasures.locations
        for (String location : locations) {
            EggHunt.getTexter().console("&7Loading treasure location: " + location);
            TreasureDto treasure = decompileTreasureLocation(location);
            if (treasure == null) {
                EggHunt.getTexter().console("&cIS NULL!");
                EggHunt.getTexter().console("&4Nepodařilo se načíst poklad na lokaci: " + location + " - odebráno z konfigurace!");
                removeTreasureLocation(location);
                continue;
            }
            EggHunt.getTexter().console("&fAdding Location " + treasure.location().toString());
            treasureDtoList.add(treasure);
        }
    }

    private static void reloadEggLocations() {
        List<String> locations = yamlConfiguration.getStringList("egg-hunt.locations");

        if (locations.isEmpty()) return;

        // Parse egg-hunt.locations
        for (String location : locations) {
            String[] loc = location.split(";");
            double x = Double.parseDouble(loc[0]);
            double y = Double.parseDouble(loc[1]);
            double z = Double.parseDouble(loc[2]);
            String world = loc[3];
            String texture = loc[4];
            String eggName = loc[5];

            World worldObj = EggHunt.getInstance().getServer().getWorld(world);

            if (worldObj == null) {
                EggHunt.getTexter().console("&4Nepodařilo se načíst vejce na lokaci: " + location + " - svět neexistuje. Odebráno z konfigurace!");
                removeEggLocation(location);
                continue;
            }

            Location blockLoc = new org.bukkit.Location(worldObj, x, y, z);

            if (blockLoc.getBlock().getType() != org.bukkit.Material.PLAYER_HEAD) {
                EggHunt.getTexter().console("&4Nepodařilo se načíst vejce na lokaci: " + blockLoc.toString() + " - blok není PLAYER_HEAD. Odebráno z konfigurace!");
                removeEggLocation(location);
                continue;
            }

            EggLocationDto eggLoc = new EggLocationDto(blockLoc, eggName, texture);
            eggStorageDto.getLocations().add(eggLoc);
        }
    }

    @SneakyThrows
    public static void reloadStorage() {
        if (!file.exists()) {
            EggHunt.getInstance().saveResource("storage.yml", false);
            Bukkit.getScheduler().runTaskLater(EggHunt.getInstance(), ConfigManager::reloadStorage, 40L);
            return;
        }

        if (!playerStorageFile.exists()) {
            playerStorageFile.createNewFile();
        }

        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        playerYamlConfiguration = YamlConfiguration.loadConfiguration(playerStorageFile);

        eggStorageDto = new EggStorageDto();

        Set<String> keys = yamlConfiguration.getConfigurationSection("egg-hunt.textures").getKeys(false);

        final Map<String, String> eggTextures = new HashMap<>();

        for (String key : keys) {
            String texture = yamlConfiguration.getString("egg-hunt.textures" + "." + key);
            if (texture != null) {
                eggTextures.put(key, texture);
            }
        }

        getEggStorageDto().setTextures(eggTextures);

        if (EggHunt.getInstance().getConfig().getBoolean("debug", false)) {
            for (Map.Entry<String, String> texture : eggTextures.entrySet()) {
                EggHunt.getTexter().console("&7- &e" + texture.getKey() + " &7(" + texture.getValue() + ")");
            }
        }


        final List<Map<?, ?>> eggs = yamlConfiguration.getMapList("egg-hunt.list");

        eggStorageDto.getEggs().clear();

        if (!eggs.isEmpty()) {
            for (Map<?, ?> egg : eggs) {
                String name = (String) egg.get("name");
                String animation = (String) egg.get("animation");
                List<String> animationPool = (List<String>) egg.get("animation-pool");
                Boolean randomReward = (Boolean) egg.get("random-reward");
                List<String> textures = (List<String>) egg.get("textures");
                List<String> rewards = (List<String>) egg.get("rewards");

                EggDto eggDto = new EggDto(name, animation, randomReward, rewards, textures, animationPool);

                getEggStorageDto().getEggs().put(name, eggDto);
            }
        }
        reloadEggLocations();
        reloadTreasureLocations();
    }

    @Nullable
    public static Location parseLocation(String loc) {
        String[] location = loc.split(";");
        double x = Double.parseDouble(location[0]);
        double y = Double.parseDouble(location[1]);
        double z = Double.parseDouble(location[2]);
        String worldName = location[3];

        World world = EggHunt.getInstance().getServer().getWorld(worldName);
        if (world == null) {
            return null;
        }

        return new Location(world, x, y, z);
    }

    public static String compileLocation(Location loc) {
        String[] location = new String[4];

        location[0] = String.valueOf(loc.toBlockLocation().getX());
        location[1] = String.valueOf(loc.toBlockLocation().getY());
        location[2] = String.valueOf(loc.toBlockLocation().getZ());
        location[3] = loc.getWorld().getName();

        return String.join(";", location);
    }

    public static String compileEggLocation(EggLocationDto egg) {
        String[] loc = new String[3];

        loc[0] = compileLocation(egg.loc());
        loc[1] = egg.texture();
        loc[2] = egg.eggType();

        return String.join(";", loc);
    }

    public static String compileTreasureLocation(TreasureDto treasure) {
        String[] loc = new String[2];

        loc[0] = compileLocation(treasure.location());
        loc[1] = treasure.name();

        return String.join(";", loc);
    }

    @Nullable
    public static TreasureDto decompileTreasureLocation(String loc) {
        Location location = parseLocation(loc);
        String[] locs = loc.split(";");
        EggHunt.getTexter().console("&7Parsed location: 1=" + locs[0] + " 2=" + locs[1] + " 3=" + locs[2] + " 4=" + locs[3] + " 5=" + locs[4]);
        //x;y;z;world;name
        String name = locs[4];

        if (location == null) {
            return null;
        }

        return new TreasureDto(location, name);
    }

    public static boolean saveTreasureLocation(TreasureDto treasure) {
        if (treasureDtoList.contains(treasure)) {
            return false;
        }
        String loc = compileTreasureLocation(treasure);
        List<String> locations = yamlConfiguration.getStringList("treasures.locations");
        locations.add(loc);
        yamlConfiguration.set("treasures.locations", locations);
        treasureDtoList.add(treasure);
        try {
            yamlConfiguration.save(file);
            return true;
        } catch (Exception e) {
            EggHunt.getTexter().console("&4Nepodařilo se uložit konfiguraci storage.yml");
            return false;
        }
    }

    public static void saveEggLocation(EggLocationDto egg) {
        String loc = compileEggLocation(egg);
        List<String> locations = yamlConfiguration.getStringList("egg-hunt.locations");
        locations.add(loc);
        yamlConfiguration.set("egg-hunt.locations", locations);
        eggStorageDto.getLocations().add(egg);
        try {
            yamlConfiguration.save(file);
            EggHunt.getParticleService().restart();
        } catch (Exception e) {
            EggHunt.getTexter().console("&4Nepodařilo se uložit konfiguraci storage.yml");
        }
    }

    public static void removeEggLocation(String loc) {
        List<String> locations = yamlConfiguration.getStringList("egg-hunt.locations");
        locations.remove(loc);
        yamlConfiguration.set("egg-hunt.locations", locations);

        Set<String> uuids = playerYamlConfiguration.getConfigurationSection("players").getKeys(false);

        for (String uuid : uuids) {
            List<String> playerLocations = getPlayerEggsFounded(uuid);
            playerLocations.remove(loc);
            playerYamlConfiguration.set("players." + uuid, playerLocations);
        }

        try {
            yamlConfiguration.save(file);
            playerYamlConfiguration.save(playerStorageFile);
            EggHunt.getParticleService().restart();
        } catch (Exception e) {
            EggHunt.getTexter().console("&4Nepodařilo se uložit konfiguraci storage.yml");
        }
    }

    public static List<String> getPlayerEggsFounded(String uuid) {
        return playerYamlConfiguration.getStringList("players." + uuid);
    }

    public static void addPlayerFoundEgg(String uuid, String egg) {
        List<String> locations = playerYamlConfiguration.getStringList("players." + uuid);
        locations.add(egg);
        playerYamlConfiguration.set("players." + uuid, locations);
        try {
            playerYamlConfiguration.save(playerStorageFile);
        } catch (Exception e) {
            EggHunt.getTexter().console("&4Nepodařilo se uložit konfiguraci player-storage.yml");
        }
    }
}
