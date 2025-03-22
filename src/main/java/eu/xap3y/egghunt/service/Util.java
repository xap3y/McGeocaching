package eu.xap3y.egghunt.service;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggLocationDto;
import eu.xap3y.egghunt.manager.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class Util {

    public static ItemStack getTexturedSkull(String base64) {

        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        head.editMeta(SkullMeta.class, skullMeta -> {
            final UUID uuid = UUID.randomUUID();
            final PlayerProfile playerProfile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));
            playerProfile.setProperty(new ProfileProperty("textures", base64));

            skullMeta.setPlayerProfile(playerProfile);
        });

        return head;
    }

    public static NamespacedKey getNamespacedKey() {
        return new NamespacedKey(EggHunt.getInstance(), "egghunt");
    }

    public static NamespacedKey getNamespacedKeyTexture() {
        return new NamespacedKey(EggHunt.getInstance(), "egghunttexture");
    }

    public static void executeRewardCommands(List<String> cmds, String playerName) {
        Bukkit.getScheduler().runTask(EggHunt.getInstance(), () -> {
            cmds.forEach(reward -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replaceAll("%player%", playerName));
            });
        });
    }

    public static void eggDeletionMessage(Player player, EggLocationDto eggLocDto, BlockFace blockFace) {
        Component comp0 = Component.text("§aVejce odstraněno! §7(" + ConfigManager.getEggStorageDto().getLocations().size() + ") ");

        Component comp1 = Component.text("§7[§e§lUNDO§7]").hoverEvent(HoverEvent.showText(Component.text("§7Klikni pro vrácení"))).clickEvent(ClickEvent.callback((source) -> {
            ConfigManager.saveEggLocation(eggLocDto);
            eggLocDto.loc().getBlock().setType(Material.PLAYER_HEAD);
            Skull state = (Skull) eggLocDto.loc().getBlock().getState();
            final UUID uuid = UUID.randomUUID();
            final PlayerProfile playerProfile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));
            playerProfile.setProperty(new ProfileProperty("textures", ConfigManager.getEggStorageDto().getTextures().get(eggLocDto.texture())));
            state.setPlayerProfile(playerProfile);
            if (blockFace != null) state.setRotation(blockFace);
            state.update(false, false);
            EggHunt.getTexter().response(player, "&aVejce bylo vráceno!");
            player.playSound(player, Sound.BLOCK_DECORATED_POT_PLACE, 1f, 1.0f);
        }));

        EggHunt.getTexter().response(player, comp0, comp1);
    }
}
