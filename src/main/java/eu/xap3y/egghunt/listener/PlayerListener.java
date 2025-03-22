package eu.xap3y.egghunt.listener;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggDto;
import eu.xap3y.egghunt.api.dto.EggLocationDto;
import eu.xap3y.egghunt.api.enums.EggAnimationType;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.service.Util;
import eu.xap3y.egghunt.util.ConfigDb;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        if (!ConfigManager.getEggHuntConfig().enabled()) {
            EggHunt.getTexter().response(event.getPlayer(), "&cEggHunt je vypnutý!");
            return;
        }

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

        List<String> rewardCommands = eggDto.rewards();

        if (eggDto.randomReward()) {
            rewardCommands.clear();
            rewardCommands.add(eggDto.rewards().get(new Random().nextInt(eggDto.rewards().size())));
        }

        Util.executeRewardCommands(rewardCommands, event.getPlayer().getName());

        ConfigManager.addPlayerFoundEgg(event.getPlayer().getUniqueId().toString(), compiledLoc);
        EggHunt.getTexter().response(event.getPlayer(), "&aVejce nalezeno! &7(&e" + (locs.size() + 1) + "&7/&a" + ConfigManager.getEggStorageDto().getLocations().size() + "&7)");

        if (!((locs.size() + 1) == ConfigManager.getEggStorageDto().getLocations().size())) {
            return;
        }

        event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1f, 1f);

        String allEggsFoundMessage = ConfigManager.getEggHuntConfig().allEggsFoundMessage();

        if (allEggsFoundMessage != null && allEggsFoundMessage.length() > 1) {
            EggHunt.getTexter().response(event.getPlayer(), allEggsFoundMessage);
        }

        if (!ConfigManager.getEggHuntConfig().allEggsFoundReward()) {
            return;
        }

        List<String> rewards = ConfigManager.getEggHuntConfig().allEggsFoundRewards();

        if (rewards == null || rewards.isEmpty()) {
            return;
        }

        List<String> allEggsFoundRewardCommands = new ArrayList<>();

        if (ConfigManager.getEggHuntConfig().allEggsFoundRandomReward()) {
            allEggsFoundRewardCommands.add(rewards.get(new Random().nextInt(rewards.size())));
        } else {
            allEggsFoundRewardCommands.addAll(rewards);
        }

        Util.executeRewardCommands(allEggsFoundRewardCommands, event.getPlayer().getName());
    }
}
