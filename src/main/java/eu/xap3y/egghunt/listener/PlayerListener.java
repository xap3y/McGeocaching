package eu.xap3y.egghunt.listener;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggDto;
import eu.xap3y.egghunt.api.dto.EggLocationDto;
import eu.xap3y.egghunt.api.enums.EggAnimationType;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.util.ConfigDb;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractionCreateEvent(PlayerInteractEvent event) {

        if(event.getHand() == EquipmentSlot.OFF_HAND) return;

        else if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.PLAYER_HEAD) return;

        else if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        if (event.getAction().isLeftClick() && event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }



        Block block = event.getClickedBlock();

        Location blockLoc = block.getLocation().toBlockLocation();

        EggLocationDto egg = ConfigManager.getEggStorageDto().getLocations().stream().filter(eggLocationDto -> eggLocationDto.loc().equals(blockLoc)).findFirst().orElse(null);

        if (egg == null) return;

        event.setCancelled(true);

        List<String> locs = ConfigManager.getPlayerEggsFounded(event.getPlayer().getUniqueId().toString());
        String compiledLoc = ConfigManager.compileLocation(egg);
        if (locs.contains(compiledLoc)) {
            EggHunt.getTexter().response(event.getPlayer(), "&cToto vejce jsi již našel!");
            return;
        }

        EggDto eggDto = ConfigManager.getEggStorageDto().getEggs().get(egg.eggType());

        if (eggDto == null) {
            EggHunt.getTexter().response(event.getPlayer(), "&cNěco se pokazilo!");
            return;
        }

        EggAnimationType animation = EggAnimationType.fromString(eggDto.animation());

        if (animation != null) {
            ConfigDb.getAnimations().get(animation).start(blockLoc.clone().add(0.5, 0.5, 0.5), event.getPlayer());
        }

        eggDto.rewards().stream().forEach(reward -> {
            Bukkit.getScheduler().runTask(EggHunt.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replaceAll("%player%", event.getPlayer().getName()));
            });
        });

        ConfigManager.addPlayerFoundEgg(event.getPlayer().getUniqueId().toString(), compiledLoc);
        EggHunt.getTexter().response(event.getPlayer(), "&aVejce nalezeno! &7(&e" + (locs.size() + 1) + "&7/&a" + ConfigManager.getEggStorageDto().getLocations().size() + "&7)");
    }
}
