package eu.xap3y.egghunt.api.enums;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.xagui.interfaces.ButtonListener;
import eu.xap3y.xagui.interfaces.GuiButtonInterface;
import eu.xap3y.xagui.models.GuiButton;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("UnstableApiUsage")
@Getter
public enum StaticGuiItems {

    CLOSE("&cZavřít", HeadType.CLOSE.getHead(), (e) -> {
        e.setCancelled(true);

        Player whoClicked = (Player) e.getWhoClicked();

        Bukkit.getScheduler().runTaskLater(EggHunt.getInstance(), () -> {
            whoClicked.closeInventory();
            whoClicked.playSound(whoClicked.getLocation(), Sound.BLOCK_COPPER_DOOR_CLOSE, .5f, 1f);
        }, 1L);
    });

    private final String name;
    private final ItemStack icon;
    private final ButtonListener onClick;

    StaticGuiItems(String name, ItemStack icon, ButtonListener onClick) {
        this.name = name;
        this.icon = icon;
        this.onClick = onClick;
    }

    public GuiButton getButton() {
        return new GuiButton(this.getIcon()).setName(this.getName()).withListener(this.getOnClick());
    }
}
