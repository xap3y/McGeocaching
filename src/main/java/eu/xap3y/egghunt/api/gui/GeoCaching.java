package eu.xap3y.egghunt.api.gui;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.HeadType;
import eu.xap3y.egghunt.api.enums.StaticGuiItems;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiInterface;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class GeoCaching extends VirtualMenu<Boolean> {

    public GeoCaching() {
        super("&b&lGeocaching", 5, EggHunt.getXagui());
    }

    @Override
    public @Nullable GuiInterface build(Boolean aBoolean) {
        GuiInterface gui = getGui();

        Integer[] freeSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int currentPageIndex = 0;

        gui.fillBorder();
        gui.addCloseButton();

        gui.setSlot(4, new GuiButton(HeadType.GEOCACHING.getHead()).setName("&bGeocaching").withClickSound(Sound.ENTITY_STRIDER_RETREAT));

        gui.setSlot(36, StaticGuiItems.GO_BACK.getButton().clone().withListener((e) -> {
            Player player = (Player) e.getWhoClicked();
            eu.xap3y.egghunt.EggHunt.getVirtualGuiRegistry().invoke(GuiType.MAIN, player, null, Boolean.class);
        }).withClickSound(Sound.ITEM_BOOK_PAGE_TURN));

        boolean isEnabled = ConfigManager.getGeocachingConfig().getEnabled();

        String text = isEnabled ? "&aZapnutý" : "&cVypnutý";

        GuiButton toggleButton = new GuiButton((isEnabled ? HeadType.TURNED_ON : HeadType.TURNED_OFF).getHead())
                .setName("&bGeocaching &fje " + text)
                .setLore(" ", "&eKlikni pro přepnutí")
                .withListener((e) -> {
                    Player player = (Player) e.getWhoClicked();
                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, .8f, 1.0f);
                    EggHunt.getInstance().getConfig().set("geocaching.enabled", !isEnabled);
                    EggHunt.getInstance().saveConfig();

                    ConfigManager.getGeocachingConfig().setEnabled(!isEnabled);
                    EggHunt.getBeepService().restart();
                    gui.close(player);
                    Bukkit.getScheduler().runTaskLater(EggHunt.getInstance(), () -> {
                        build(null).open(player);
                    }, 3L);
                });

        int size = ConfigManager.getTreasureDtoList().size();
        GuiButton libraryButton = new GuiButton(Material.BOOKSHELF)
                .setName("&9Knihovna pokladů")
                .setLore(" ",
                        "&fNa serveru je celkem &e" + size + " pokladů",
                        "",
                        (size > 0) ? "&eKlikni pro zobrazení" : "&cNelze zobrazit")
                .withListener((e) -> {
                    Player player = (Player) e.getWhoClicked();
                    if (size < 1) {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, .8f, 1.0f);
                        return;
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, .8f, 1.0f);
                    new TreasuresView().build(null).open(player);
                });

        gui.setSlot(21, toggleButton);
        gui.setSlot(23, libraryButton);

        gui.setSlot(44, StaticGuiItems.RELOAD.getButton());

        return gui;
    }
}
