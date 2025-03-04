package eu.xap3y.egghunt.api.gui;

import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiInterface;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class EggHunt extends VirtualMenu<Boolean> {

    public EggHunt() {
        super("Test", 5, eu.xap3y.egghunt.EggHunt.getXagui());
    }

    @Override
    public @Nullable GuiInterface build(Boolean b) {
        GuiInterface gui = getGui();

        gui.fillBorder();
        gui.addCloseButton();

        gui.setSlot(10, Material.OAK_DOOR);
        gui.setSlot(11, Material.OAK_DOOR);

        return gui;
    }
}
