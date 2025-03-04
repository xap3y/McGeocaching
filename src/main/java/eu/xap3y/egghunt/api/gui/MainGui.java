package eu.xap3y.egghunt.api.gui;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.HeadType;
import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiButtonInterface;
import eu.xap3y.xagui.interfaces.GuiInterface;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MainGui extends VirtualMenu<Boolean> {

    public MainGui() {
        super("&b&lGeocaching", 5, EggHunt.getXagui());
    }


    @Override
    public @Nullable GuiInterface build(Boolean b) {
        GuiInterface gui = getGui();

        gui.fillBorder();
        gui.addCloseButton();

        GuiButtonInterface eggHunt = new GuiButton(HeadType.EASTER_EGG.getHead())
                .setName("&6Egg Hunt")
                .setLore(" ", "&eKlikni pro zobrazení")
                .withListener((e) -> {
                    EggHunt.getVirtualGuiRegistry().invoke(GuiType.EGG_HUNT, (Player) e.getWhoClicked(), null, Boolean.class);
                });

        GuiButtonInterface unknown = new GuiButton(HeadType.UNKNOWN.getHead()).setName("&7Brzy...");

        gui.setSlot(21, eggHunt);
        gui.setSlot(23, unknown);

        return gui;
    }
}
