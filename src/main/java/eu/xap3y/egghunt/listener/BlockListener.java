package eu.xap3y.egghunt.listener;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggLocationDto;
import eu.xap3y.egghunt.api.dto.EggStorageDto;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.service.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
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

        Component comp1 = Component.text("§7[§e§lUNDO§7]").hoverEvent(HoverEvent.showText(Component.text("§7Klikni pro odebrání vejce"))).clickEvent(ClickEvent.callback((source) -> {
            if (eggLocDto.loc().getBlock().getType() != Material.PLAYER_HEAD) {
                EggHunt.getTexter().response(event.getPlayer(), "&cToto vejce neexistuje!");
                return;
            }
            ConfigManager.getEggStorageDto().getLocations().remove(eggLocDto);
            ConfigManager.removeEggLocation(ConfigManager.compileLocation(eggLocDto));
            eggLocDto.loc().getBlock().setType(Material.AIR);
            event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_DECORATED_POT_BREAK, 1f, 1.0f);
            EggHunt.getTexter().response(event.getPlayer(), "&cVejce odebráno!");
        }));

        Component comp0 = Component.text("§aVejce nastaveno! §7(" + ConfigManager.getEggStorageDto().getLocations().size() + ")").append(Component.text(" "));

        EggHunt.getTexter().response(event.getPlayer(), comp0, comp1);
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

        BlockFace blockFace = event.getBlock().getFace(event.getPlayer().getLocation().getBlock());

        ConfigManager.getEggStorageDto().getLocations().remove(eggLocDto);
        ConfigManager.removeEggLocation(ConfigManager.compileLocation(eggLocDto));
        //EggHunt.getTexter().response(event.getPlayer(), "&aVejce odstraněno! &7(" + ConfigManager.getEggStorageDto().getLocations().size() + ")");
        event.setDropItems(false);

        Util.eggDeletionMessage(event.getPlayer(), eggLocDto, blockFace);
    }
}
