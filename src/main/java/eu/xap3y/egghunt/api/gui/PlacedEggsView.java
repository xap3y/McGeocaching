package eu.xap3y.egghunt.api.gui;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggLocationDto;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.StaticGuiItems;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.service.Util;
import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiInterface;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlacedEggsView extends VirtualMenu<String> {

    public PlacedEggsView() {
        super("Placed eggs", 6, 1, eu.xap3y.egghunt.EggHunt.getXagui());
    }

    @Override
    public @Nullable GuiInterface build(String nullos) {

        Integer[] freeSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int currentPageIndex = 0;

        GuiInterface gui = getGui();

        List<EggLocationDto> eggLocationDtoList = ConfigManager.getEggStorageDto().getLocations();
        int totalPages = (int) Math.ceil(eggLocationDtoList.size() / 28.0);
        gui.setTotalPages(totalPages > 0 ? totalPages : 1);

        gui.fillBorder();
        gui.addCloseButtonAllPages();
        gui.addPaginator();
        gui.setPageSwitchSound(Sound.ITEM_BOOK_PAGE_TURN);

        int i = 0;
        int j = 1;
        int k = 1;
        for (EggLocationDto eggLoc : eggLocationDtoList) {
            GuiButton guiButton = new GuiButton(Util.getTexturedSkull(ConfigManager.getEggStorageDto().getTextures().get(eggLoc.texture())))
                    .setAmount(j)
                    .setName("&6" + eggLoc.eggType() + " &7(&e" + k + "&7)")
                    .setLore(
                            " ",
                            " &3➥ &fWorld: &e" + eggLoc.loc().getWorld().getName(),
                            " &3➥ &fX: &e" + eggLoc.loc().getBlockX(),
                            " &3➥ &fY: &e" + eggLoc.loc().getBlockY(),
                            " &3➥ &fZ: &e" + eggLoc.loc().getBlockZ(),
                            " ",
                            "&aLeft-Click pro teleportaci k vajíčku",
                            "&cShift + Left-Click pro smazání"
                    )
                    .withListener((e) -> {
                        ClickType click = e.getClick();
                        Player player = (Player) e.getWhoClicked();

                        if (click == ClickType.LEFT || click == ClickType.RIGHT) {
                            Location tpLoc = eggLoc.loc().toBlockLocation();
                            tpLoc.setPitch(90f);
                            tpLoc.add(0.5, 0, 0.5);
                            player.teleport(tpLoc);
                            player.playSound(player, Sound.ENTITY_SHULKER_TELEPORT, .8f, 1f);
                        } else if (click == ClickType.SHIFT_LEFT) {
                            BlockFace blockFace = eggLoc.loc().getBlock().getFace(player.getLocation().getBlock());
                            eggLocationDtoList.remove(eggLoc);
                            ConfigManager.removeEggLocation(ConfigManager.compileEggLocation(eggLoc));
                            eggLoc.loc().getBlock().setType(Material.AIR);
                            Util.eggDeletionMessage(player, eggLoc, blockFace);
                            player.playSound(player, Sound.BLOCK_BONE_BLOCK_BREAK, .5f, 1f);
                            gui.clearAllSlots();
                            Bukkit.getScheduler().runTaskLater(EggHunt.getInstance(), () -> {
                                build(null).open(player);
                            }, 2L);

                        }
                    });

            gui.setSlot(currentPageIndex, freeSlots[i], guiButton);
            i++;
            j++;
            k++;
            if (i > 27) {
                currentPageIndex++;
                i = 0;
            }
            if (j > 64) {
                j=1;
            }
        }

        GuiButton goBack = StaticGuiItems.GO_BACK.getButton().clone().withListener((e) -> {
            Player player = (Player) e.getWhoClicked();
            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
            EggHunt.getVirtualGuiRegistry().invoke(GuiType.EGG_HUNT, player, null, Boolean.class);
        });
        gui.setAllPageSlot(45, goBack);
        /*

        gui.setSlot(0, 45, goBack);
        gui.setSlot(1, 45, goBack);
        gui.setSlot(2, 45, goBack);

        GuiButton nextPage = new GuiButton(Material.ARROW)
                .setName("&6Další strana")
                .setLore(" ", "&eKlikni pro zobrazení")
                .withListener((e) -> {
                    Player player = (Player) e.getWhoClicked();
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
                    openedPageIndex[0]++;
                    getGui().switchPage(openedPageIndex[0], player);
                });*/

        /*int finalCurrentPageIndex = currentPageIndex;
        gui.setOnPageSwitch((GuiPageSwitchModel e) -> {
            if (e.getPage() == 0) {
                gui.setSlot(46, XaGui.Companion.getBorderFiller());
            }

            if (e.getPage() > 0) {
                GuiButton previousPage = new GuiButton(Material.ARROW)
                        .setName("&6Předchozí strana")
                        .setLore(" ", "&eKlikni pro zobrazení")
                        .withListener((event) -> {
                            Player player = (Player) event.getWhoClicked();
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
                            openedPageIndex[0]--;
                            getGui().switchPage(openedPageIndex[0], player);
                        });
                gui.setSlot(e.getPage(), 46, previousPage);
            }

            if (e.getPage() < finalCurrentPageIndex) {
                gui.setSlot(e.getPage(), 53, nextPage);
            }
        });*/

        return gui;
    }

}
