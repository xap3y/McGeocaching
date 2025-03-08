package eu.xap3y.egghunt.listener;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggLocationDto;
import eu.xap3y.egghunt.api.dto.EggStorageDto;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.service.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BlockListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {

        if (!event.canBuild() || event.isCancelled()) return;

        ItemStack item = event.getItemInHand();

        if (item.getType() != Material.PLAYER_HEAD) return;

        ItemMeta itemMeta = item.getItemMeta();

        if (!itemMeta.getPersistentDataContainer().has(Util.getNamespacedKey())) return;

        else if (!event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            EggHunt.getTexter().response(event.getPlayer(), "&cPro nastavení vejce drž shift!");
            return;
        }

        String eggType = itemMeta.getPersistentDataContainer().get(Util.getNamespacedKey(), PersistentDataType.STRING);

        EggStorageDto storage = ConfigManager.getEggStorageDto();

        if (!storage.getEggs().containsKey(eggType)) {
            event.getItemInHand().setAmount(0);
            event.setCancelled(true);
            EggHunt.getTexter().response(event.getPlayer(), "&cNěco se pokazilo!");
            return;
        }

        String texture = itemMeta.getPersistentDataContainer().get(Util.getNamespacedKeyTexture(), PersistentDataType.STRING);
        Location blockLoc = event.getBlock().getLocation().toBlockLocation();

        EggLocationDto eggLocDto = new EggLocationDto(blockLoc, eggType, texture);

        ConfigManager.saveEggLocation(eggLocDto);

        event.getItemInHand().setAmount(event.getItemInHand().getAmount()+1);

        EggHunt.getTexter().response(event.getPlayer(), "&aVejce nastaveno! &7(" + ConfigManager.getEggStorageDto().getLocations().size() + ")");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreakEvent(BlockBreakEvent event) {

        if (event.isCancelled()) return;

        if (event.getBlock().getType() != Material.PLAYER_HEAD) return;

        Location blockLoc = event.getBlock().getLocation().toBlockLocation();

        EggLocationDto eggLocDto = ConfigManager.getEggStorageDto().getLocations().stream()
                .filter(eggLocationDto -> eggLocationDto.loc().equals(blockLoc))
                .findFirst()
                .orElse(null);

        if (eggLocDto == null) return;

        if (!event.getPlayer().isOp() || !event.getPlayer().hasPermission("egghunt.admin")) {
            event.setCancelled(true);
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            EggHunt.getTexter().response(event.getPlayer(), "&cPro odstranění vejce drž shift!");
            return;
        }

        ConfigManager.getEggStorageDto().getLocations().remove(eggLocDto);
        ConfigManager.removeEggLocation(ConfigManager.compileLocation(eggLocDto));
        EggHunt.getTexter().response(event.getPlayer(), "&aVejce odstraněno! &7(" + ConfigManager.getEggStorageDto().getLocations().size() + ")");
        event.setDropItems(false);
    }
}
