package eu.xap3y.egghunt.service;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.TreasureDto;
import eu.xap3y.egghunt.manager.ConfigManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeepService {

    private BukkitTask task;

    @Getter
    private static Set<Player> nearbyPlayers = new HashSet<Player>();

    @Getter
    private static Map<UUID, BossBar> bossBarMapper = new ConcurrentHashMap<UUID, BossBar>();

    public void init() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(EggHunt.getInstance(), () -> {
            for (Player player : nearbyPlayers) {
                if (!player.isOnline() || player.getInventory().getItemInMainHand().getType() != Material.COMPASS) {
                    nearbyPlayers.remove(player);
                    // Remove bossbar
                    bossBarMapper.get(player.getUniqueId()).setVisible(false);
                    bossBarMapper.get(player.getUniqueId()).removeAll();
                    bossBarMapper.remove(player.getUniqueId());
                    continue;
                }

                TreasureDto closestTreasure = getClosestTreasure(player.getLocation());

                if (closestTreasure == null) {
                    continue;
                }

                player.setCompassTarget(closestTreasure.location());

                double distance = player.getLocation().distance(closestTreasure.location());
                if (EggHunt.getInstance().getConfig().getBoolean("debug", false)) EggHunt.getTexter().response(player, "[DEBUG] Distance: " + distance);
                //player.sendActionBar(Component.text("§ePoklad v blizkosti: §c" + String.format("%.1f", distance) + " bloků"));

                Sound sound;
                float pitch;
                boolean doubleSound = false;
                boolean tripleSound = false;
                long doubleSoundDelay = 10L;

                if (distance > 40) {
                    sound = Sound.BLOCK_NOTE_BLOCK_BASS;
                } else {
                    sound = Sound.BLOCK_NOTE_BLOCK_BELL;
                }

                BarColor barColor = BarColor.RED;

                if  (distance < 10) {
                    doubleSound = true;
                }

                if (distance < 1) {
                    barColor = BarColor.WHITE;
                    tripleSound = true;
                    doubleSoundDelay = 7L;
                    pitch = 1.6f;
                }
                else if (distance < 5) {
                    barColor = BarColor.GREEN;
                    tripleSound = true;
                    doubleSoundDelay = 7L;
                    pitch = 1.6f;
                } else if (distance < 10) {
                    barColor = BarColor.GREEN;
                    pitch = 1.5f;
                } else if (distance < 15) {
                    barColor = BarColor.YELLOW;
                    pitch = 1.4f;
                } else if (distance < 20) {
                    barColor = BarColor.YELLOW;
                    pitch = 1.2f;
                } else if (distance < 25) {
                    barColor = BarColor.YELLOW;
                    pitch = 1f;
                } else if (distance < 30) {
                    barColor = BarColor.YELLOW;
                    pitch = .8f;
                } else if (distance < 35) {
                    pitch = .6f;
                } else if (distance < 40) {
                    pitch = .4f;
                } else if (distance < 50) {
                    pitch = 1.4f;
                } else if (distance < 60)  {
                    pitch = 1f;
                } else {
                    pitch = .8f;
                }

                final BossBar bar = bossBarMapper.get(player.getUniqueId());

                if (bar == null) {
                    final BossBar bossBar = Bukkit.createBossBar(
                            Texter.colored("&ePoklad v blízkosti"),
                            barColor,
                            BarStyle.SEGMENTED_20,
                            BarFlag.DARKEN_SKY
                    );

                    bossBar.setProgress(1 - (distance / 100));
                    bossBar.addPlayer(player);
                    bossBar.setVisible(true);
                    bossBarMapper.put(player.getUniqueId(), bossBar);
                } else {
                    bar.setColor(barColor);
                    bar.setProgress(1 - (distance / 100));
                }

                player.playSound(player, sound, 1.0f, pitch);

                if (doubleSound) {
                    Bukkit.getScheduler().runTaskLater(EggHunt.getInstance(), () -> {
                        player.playSound(player, sound, 1.0f, pitch);
                    }, doubleSoundDelay);

                    if (tripleSound) {
                        Bukkit.getScheduler().runTaskLater(EggHunt.getInstance(), () -> {
                            player.playSound(player, sound, 1.0f, pitch);
                        }, doubleSoundDelay * 2);
                    }
                }
            }
        }, 0L, 20L);
    }

    public void restart() {
        destroy();
        init();
    }

    public void destroy() {
        task.cancel();
        task = null;
    }

    private TreasureDto getClosestTreasure(Location playerLoc) {
        return ConfigManager.getTreasureDtoList().stream()
                .min(Comparator.comparingDouble(loc -> loc.location().distance(playerLoc)))
                .orElse(null);
    }
}
