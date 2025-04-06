package eu.xap3y.egghunt.api.gui;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.HeadType;
import eu.xap3y.egghunt.api.enums.StaticGuiItems;
import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiInterface;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class GeoCaching extends VirtualMenu<Boolean> {

    public GeoCaching() {
        super("&b&lGeocaching", 6, EggHunt.getXagui());
    }

    @Override
    public @Nullable GuiInterface build(Boolean aBoolean) {
        GuiInterface gui = getGui();

        Integer[] freeSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int currentPageIndex = 0;

        gui.fillBorder();
        gui.addCloseButton();

        gui.setSlot(4, new GuiButton(HeadType.GEOCACHING.getHead()).setName("&6Geocaching"));

        gui.setSlot(36, StaticGuiItems.GO_BACK.getButton().clone().withListener((e) -> {
            Player player = (Player) e.getWhoClicked();
            eu.xap3y.egghunt.EggHunt.getVirtualGuiRegistry().invoke(GuiType.MAIN, player, null, Boolean.class);
        }).withClickSound(Sound.ITEM_BOOK_PAGE_TURN));



        return gui;
    }
}
