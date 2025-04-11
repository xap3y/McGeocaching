package eu.xap3y.egghunt.api.enums;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.xagui.interfaces.ButtonListener;
import eu.xap3y.xagui.interfaces.GuiButtonInterface;
import eu.xap3y.xagui.models.GuiButton;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
    }),
    GO_BACK("&cZpět", HeadType.OAK_WOOD_ARROW_LEFT.getHead(), null),
    RELOAD("&c&lReload", new ItemStack(Material.GUNPOWDER), ((e) -> {
        Player player = (Player) e.getWhoClicked();
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        ConfigManager.reloadConfig();
        ConfigManager.reloadStorage();
        eu.xap3y.egghunt.EggHunt.getTexter().response(player, "&aKonfigurace byla znovunačtena");
    })," ", "&eKlikni pro znovunačtení konfigurace");

    private final String name;
    private final ItemStack icon;
    private final ButtonListener onClick;
    private final String[] lore;

    StaticGuiItems(String name, ItemStack icon, ButtonListener onClick, String... lore) {
        this.name = name;
        this.icon = icon;
        this.onClick = onClick;
        this.lore = lore;
    }

    public GuiButton getButton() {
        return new GuiButton(this.getIcon()).setLore(lore).setName(this.getName()).withListener(this.getOnClick() != null ? this.getOnClick() : (e) -> {});
    }
}
