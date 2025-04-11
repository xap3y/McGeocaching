package eu.xap3y.egghunt.api.gui;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.TreasureDto;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.StaticGuiItems;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiInterface;
import eu.xap3y.xagui.models.GuiButton;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TreasuresView  extends VirtualMenu<Boolean> {

    public TreasuresView() {
        super("&9Knihovna pokladů", 6, 1, eu.xap3y.egghunt.EggHunt.getXagui());
    }

    @Override
    public @Nullable GuiInterface build(Boolean nullos) {

        Integer[] freeSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int currentPageIndex = 0;

        List<TreasureDto> treasureLocationDtoList = ConfigManager.getTreasureDtoList();
        if (treasureLocationDtoList == null || treasureLocationDtoList.isEmpty()) {
            return null;
        }

        int totalPages = (int) Math.ceil(treasureLocationDtoList.size() / 28.0);

        GuiInterface gui = getGui();


        gui.setTotalPages(totalPages > 0 ? totalPages : 1);

        gui.fillBorder();
        gui.addCloseButtonAllPages();
        gui.addPaginator();
        gui.setPageSwitchSound(Sound.ITEM_BOOK_PAGE_TURN);

        int i = 0;
        int j = 1;
        int k = 1;

        for (TreasureDto treasureDto : treasureLocationDtoList) {
            GuiButton guiButton = new GuiButton(Material.CHEST)
                    .setAmount(j)
                    .setName("&6" + treasureDto.name() + " &7(&e#" + k + "&7)")
                    .setLore(
                            " ",
                            " &3➥ &fWorld: &e" + treasureDto.location().getWorld().getName(),
                            " &3➥ &fX: &e" + treasureDto.location().getBlockX(),
                            " &3➥ &fY: &e" + treasureDto.location().getBlockY(),
                            " &3➥ &fZ: &e" + treasureDto.location().getBlockZ(),
                            " ",
                            "&aLeft-Click pro teleportaci k pokladu",
                            "&cShift + Left-Click pro smazání"
                    )
                    .withListener((e) -> {
                        ClickType click = e.getClick();
                        Player player = (Player) e.getWhoClicked();

                        if (click == ClickType.LEFT || click == ClickType.RIGHT) {
                            Location tpLoc = treasureDto.location().toBlockLocation();
                            tpLoc.setPitch(90f);
                            tpLoc.add(0.5, 0, 0.5);
                            player.teleport(tpLoc);
                            player.playSound(player, Sound.ENTITY_SHULKER_TELEPORT, .8f, 1f);
                        } else if (click == ClickType.SHIFT_LEFT) {
                            ConfigManager.removeTreasureLocation(treasureDto);
                            treasureDto.location().getBlock().setType(Material.AIR);
                            player.playSound(player, Sound.BLOCK_BONE_BLOCK_BREAK, .5f, 1f);
                            gui.clearAllSlots();
                            Bukkit.getScheduler().runTaskLater(EggHunt.getInstance(), () -> {
                                GuiInterface build = build(null);
                                if (build != null) {
                                    build.open(player);
                                } else {
                                    player.playSound(player, Sound.ENTITY_CAT_HISS, .5f, 1f);
                                    EggHunt.getVirtualGuiRegistry().invoke(GuiType.GEOCACHE, player, null, Boolean.class);
                                }
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
            EggHunt.getVirtualGuiRegistry().invoke(GuiType.GEOCACHE, player, null, Boolean.class);
        });
        gui.setAllPageSlot(45, goBack);

        return gui;
    }
}
