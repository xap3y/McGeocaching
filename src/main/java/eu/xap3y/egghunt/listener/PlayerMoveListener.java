package eu.xap3y.egghunt.listener;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.service.BeepService;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMoveListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.COMPASS) {
            return;
        }

        BeepService.getNearbyPlayers().add(player);
    }



    private void playBeepSound(Player player, double distance) {
        int interval = (int) Math.max(5, Math.min(20, distance / 2));

        new BukkitRunnable() {
            int count = 2;
            @Override
            public void run() {
                if (count-- <= 0 || player.getInventory().getItemInMainHand().getType() != Material.IRON_HOE) {
                    cancel();
                    return;
                }
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.5f);
            }
        }.runTaskTimer(EggHunt.getInstance(), 0, interval);
    }

}
