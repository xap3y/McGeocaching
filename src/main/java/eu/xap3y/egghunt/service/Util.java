package eu.xap3y.egghunt.service;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import eu.xap3y.egghunt.EggHunt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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
}
