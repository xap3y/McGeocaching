package eu.xap3y.egghunt.api.gui;

import eu.xap3y.egghunt.api.dto.EggDto;
import eu.xap3y.egghunt.api.dto.EggStorageDto;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.StaticGuiItems;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiButtonInterface;
import eu.xap3y.xagui.interfaces.GuiInterface;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EggHunt extends VirtualMenu<Boolean> {

    public EggHunt() {
        super("Test", 5, eu.xap3y.egghunt.EggHunt.getXagui());
    }

    @Override
    public @Nullable GuiInterface build(Boolean b) {
        GuiInterface gui = getGui();

        gui.fillBorder();
        gui.addCloseButton();

        Integer[] freeSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

        EggStorageDto eggStorageDto = ConfigManager.getEggStorageDto();

        for (Map.Entry<String, EggDto> egg : eggStorageDto.getEggs().entrySet()) {
            if (egg.getValue() == null) continue;

            GuiButtonInterface eggButton = new GuiButton(Material.BOOKSHELF)
                    .setName("&6" + egg.getValue().name());

            if (egg.getValue().textures() == null || egg.getValue().textures().isEmpty()) {
                eggButton.setLore(" ", "&cToto vajíčko nemá žádné textury");
            } else {
                eggButton.setLore(" ", "&eKlikni pro zobrazení")
                    .withListener((e) -> {
                        eu.xap3y.egghunt.EggHunt.getVirtualGuiRegistry().invoke(GuiType.EGG_VIEW, (org.bukkit.entity.Player) e.getWhoClicked(), egg.getValue(), EggDto.class);
                    });
            }

            gui.setSlot(freeSlots[0], eggButton);
            freeSlots[0]++;
        }

        gui.setSlot(36, StaticGuiItems.GO_BACK.getButton().clone().withListener((e) -> {
            eu.xap3y.egghunt.EggHunt.getVirtualGuiRegistry().invoke(GuiType.MAIN, (org.bukkit.entity.Player) e.getWhoClicked(), null, Boolean.class);
        }));

        return gui;
    }
}
