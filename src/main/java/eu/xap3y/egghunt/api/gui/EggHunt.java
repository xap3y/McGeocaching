package eu.xap3y.egghunt.api.gui;

import eu.xap3y.egghunt.api.dto.EggDto;
import eu.xap3y.egghunt.api.dto.EggStorageDto;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.HeadType;
import eu.xap3y.egghunt.api.enums.StaticGuiItems;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiButtonInterface;
import eu.xap3y.xagui.interfaces.GuiInterface;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EggHunt extends VirtualMenu<Boolean> {

    public EggHunt() {
        super("&6&lEgg Hunt", 5, eu.xap3y.egghunt.EggHunt.getXagui());
    }

    @Override
    public @Nullable GuiInterface build(Boolean b) {
        GuiInterface gui = getGui();

        gui.fillBorder();
        gui.addCloseButton();

        Integer[] freeSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

        EggStorageDto eggStorageDto = ConfigManager.getEggStorageDto();

        gui.setSlot(4, new GuiButton(HeadType.EASTER_EGG.getHead()).setName("&6Egg Hunt").withClickSound(Sound.ENTITY_EVOKER_PREPARE_WOLOLO));

        int i = 0;
        for (Map.Entry<String, EggDto> egg : eggStorageDto.getEggs().entrySet()) {
            if (egg.getValue() == null) continue;

            GuiButtonInterface eggButton = new GuiButton(Material.BOOKSHELF)
                    .setName("&6" + egg.getValue().name());

            if (egg.getValue().textures() == null || egg.getValue().textures().isEmpty()) {
                eggButton.setLore(" ", "&cToto vajíčko nemá žádné textury");
            } else {
                eggButton.setLore(" ", "&eKlikni pro zobrazení")
                    .withListener((e) -> {
                        Player player = (Player) e.getWhoClicked();
                        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, .8f, 1.0f);
                        //eu.xap3y.egghunt.EggHunt.getVirtualGuiRegistry().invoke(GuiType.EGG_VIEW, player, egg.getValue(), EggDto.class);
                        new EggView().build(egg.getValue()).open(player);
                    });
            }

            gui.setSlot(freeSlots[i], eggButton);
            i++;
        }

        gui.setSlot(36, StaticGuiItems.GO_BACK.getButton().clone().withListener((e) -> {
            Player player = (Player) e.getWhoClicked();
            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
            eu.xap3y.egghunt.EggHunt.getVirtualGuiRegistry().invoke(GuiType.MAIN, player, null, Boolean.class);
        }));

        GuiButton catalog = new GuiButton(Material.PAPER).setName("&3&lPoložená vajíčka")
                .setLore(" ", " &3➥ &fPočet vajíček na nalezení: &e" + ConfigManager.getEggStorageDto().getLocations().size(), "", "&eKlikni pro zobrazení")
                .withListener((e) -> {
                    Player player = (Player) e.getWhoClicked();
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, .8f, 1.0f);
                    //eu.xap3y.egghunt.EggHunt.getVirtualGuiRegistry().invoke(GuiType.EGG_HUNT, player, true, Boolean.class);
                    new PlacedEggsView().build(null).open(player);
                });

        gui.setSlot(43, catalog);

        gui.setSlot(44, StaticGuiItems.RELOAD.getButton());

        boolean isEnabled = ConfigManager.getEggHuntConfig().getEnabled();

        String text = isEnabled ? "&aZapnutý" : "&cVypnutý";

        GuiButton toggleButton = new GuiButton((isEnabled ? HeadType.TURNED_ON : HeadType.TURNED_OFF).getHead())
                .setName("&6EggHunt &fje " + text)
                .setLore(" ", "&eKlikni pro přepnutí")
                .withListener((e) -> {
                    Player player = (Player) e.getWhoClicked();
                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, .8f, 1.0f);
                    eu.xap3y.egghunt.EggHunt.getInstance().getConfig().set("egghunt.enabled", !isEnabled);
                    eu.xap3y.egghunt.EggHunt.getInstance().saveConfig();

                    ConfigManager.getEggHuntConfig().setEnabled(!isEnabled);
                    eu.xap3y.egghunt.EggHunt.getParticleService().restart();

                    gui.close(player);
                    Bukkit.getScheduler().runTaskLater(eu.xap3y.egghunt.EggHunt.getInstance(), () -> {
                        build(null).open(player);
                    }, 3L);
                });

        gui.setSlot(42, toggleButton);

        return gui;
    }
}
