package eu.xap3y.egghunt.manager;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggDto;
import eu.xap3y.egghunt.api.dto.EggLocationDto;
import eu.xap3y.egghunt.api.dto.EggStorageDto;
import eu.xap3y.egghunt.service.Texter;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigManager {

    @Getter
    private static EggStorageDto eggStorageDto;

    private static YamlConfiguration yamlConfiguration;

    private static YamlConfiguration playerYamlConfiguration;

    private static final File file = new File(EggHunt.getInstance().getDataFolder(), "storage.yml");
    private static final File playerStorageFile = new File(EggHunt.getInstance().getDataFolder(), "player-storage.yml");

    public static void reloadConfig() {
        if (!EggHunt.getInstance().getDataFolder().exists()) {
            EggHunt.getInstance().getDataFolder().mkdir();
        }

        EggHunt.getInstance().saveDefaultConfig();
        EggHunt.getInstance().reloadConfig();

        String prefix = EggHunt.getInstance().getConfig().getString("prefix");
        if (prefix == null) prefix = "&7[&bEggHunt&7] &r";
        EggHunt.setTexter(new Texter(prefix, false, null));
    }

    @SneakyThrows
    public static void reloadStorage() {
        if (!file.exists()) {
            file.createNewFile();
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

        for (Map.Entry<String, String> texture : eggTextures.entrySet()) {
            EggHunt.getTexter().console("&7- &e" + texture.getKey() + " &7(" + texture.getValue() + ")");
        }

        final List<Map<?, ?>> eggs = yamlConfiguration.getMapList("egg-hunt.eggs.list");

        eggStorageDto.getEggs().clear();

        if (!eggs.isEmpty()) {
            for (Map<?, ?> egg : eggs) {
                String name = (String) egg.get("name");
                String animation = (String) egg.get("animation");
                Boolean randomReward = (Boolean) egg.get("random-reward");
                List<String> textures = (List<String>) egg.get("textures");
                List<String> rewards = (List<String>) egg.get("rewards");

                EggDto eggDto = new EggDto(name, animation, randomReward, rewards, textures);

                getEggStorageDto().getEggs().put(name, eggDto);
            }
        }

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


    public static String compileLocation(EggLocationDto egg) {
        String[] loc = new String[6];

        loc[0] = String.valueOf(egg.loc().getX());
        loc[1] = String.valueOf(egg.loc().getY());
        loc[2] = String.valueOf(egg.loc().getZ());

        loc[3] = egg.loc().getWorld().getName();
        loc[4] = egg.texture();
        loc[5] = egg.eggType();

        return String.join(";", loc);
    }

    public static void saveEggLocation(EggLocationDto egg) {
        String loc = compileLocation(egg);
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
