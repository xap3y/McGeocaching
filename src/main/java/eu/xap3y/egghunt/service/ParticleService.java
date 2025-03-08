package eu.xap3y.egghunt.service;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggLocationDto;
import eu.xap3y.egghunt.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParticleService {

    private BukkitTask task;
    private BukkitTask playerFetcher;

    public void init() {

        Location[] locTemp = ConfigManager.getEggStorageDto().getLocations().stream()
                .map(EggLocationDto::loc)
                .toArray(Location[]::new);

        Map<Location, Collection<Player>> locPlayers = new HashMap<>();

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(EggHunt.getInstance(), () -> {
            for (Location loc : locTemp) {
                if (!locPlayers.containsKey(loc)) {
                    continue;
                }

                Collection<Player> players = locPlayers.get(loc);
                if (players.isEmpty()) {
                    continue;
                }

                // Randomize location, curerntly its bottom corner of the block
                double x = Math.random();
                double y = Math.random();
                double z = Math.random();
                Location loc1 = loc.clone().add(x, y, z);
                EggHunt.getParApi().LIST_1_8.VILLAGER_HAPPY
                        .packet(true, loc1)
                        .sendInRadiusTo(players, 20);
                Location loc2 = loc.clone().add(Math.random(), Math.random(), Math.random());
                EggHunt.getParApi().LIST_1_8.VILLAGER_HAPPY
                        .packet(true, loc2)
                        .sendInRadiusTo(players, 20);

            }
        }, 0L, 10L);

        playerFetcher = Bukkit.getScheduler().runTaskTimer(EggHunt.getInstance(), () -> {
            for (Location loc : locTemp) {
                Collection<Player> players = loc.getNearbyPlayers(20);
                if (players.isEmpty()) continue;
                locPlayers.put(loc, players);
            }
        }, 0L, 80L);
    }

    public void restart() {
        destroy();
        init();
    }

    public void destroy() {
        task.cancel();
        playerFetcher.cancel();
        playerFetcher = null;
        task = null;
    }
}
