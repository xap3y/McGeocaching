package eu.xap3y.egghunt.api.gui;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggDto;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.StaticGuiItems;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.service.Util;
import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiButtonInterface;
import eu.xap3y.xagui.interfaces.GuiInterface;
import eu.xap3y.xagui.models.GuiButton;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EggView extends VirtualMenu<EggDto> {

    public EggView() {
        super("&eEgg View", 5, eu.xap3y.egghunt.EggHunt.getXagui());
    }

    @Override
    public @Nullable GuiInterface build(EggDto eggDto) {

        List<String> textures = eggDto.textures();

        if (textures == null) {
            return null;
        }

        GuiInterface gui = getGui();

        gui.fillBorder();
        gui.addCloseButton();

        Integer[] freeSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

        if (textures.stream().anyMatch(s -> s.equals("ALL"))) {
            textures = ConfigManager.getEggStorageDto().getTextures().keySet().stream().toList();
        }

        for (String texture : textures) {
            if (texture == null) continue;

            String base64 = ConfigManager.getEggStorageDto().getTextures().get(texture);

            if (base64 == null) continue;

            ItemStack skull = Util.getTexturedSkull(base64);
            GuiButtonInterface eggButton = new GuiButton(skull)
            .setName("&6" + texture)
            .setLore(" ", "&eKlikni pro získáni")
            .withListener((e) -> {
                Player player = (Player) e.getWhoClicked();

                ItemStack skullItem = skull.clone();
                skullItem.setAmount(1);
                ItemMeta meta = skullItem.getItemMeta();
                meta.displayName(Component.text("§f" + texture));

                Component c1 = Component.text("§7" + eggDto.name());
                Component c2 = Component.text("§e" + "Polož vejce na zem.");
                Component finalComponent = Component.text(" ").append(c1).append(Component.text(" ")).append(c2);

                meta.lore(finalComponent.children());

                meta.getPersistentDataContainer().set(Util.getNamespacedKey(), PersistentDataType.STRING, eggDto.name());
                meta.getPersistentDataContainer().set(Util.getNamespacedKeyTexture(), PersistentDataType.STRING, texture);

                skullItem.setItemMeta(meta);

                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);
                player.getInventory().addItem(skullItem);
            });

            gui.setSlot(freeSlots[0], eggButton);
            freeSlots[0]++;
        }

        gui.setSlot(36, StaticGuiItems.GO_BACK.getButton().clone().withListener((e) -> {
            EggHunt.getVirtualGuiRegistry().invoke(GuiType.EGG_HUNT, (org.bukkit.entity.Player) e.getWhoClicked(), null, Boolean.class);
        }));

        return gui;
    }
}
