package eu.xap3y.egghunt.api.gui;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggDto;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.StaticGuiItems;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.service.Util;
import eu.xap3y.xagui.VirtualMenu;
import eu.xap3y.xagui.interfaces.GuiButtonInterface;
import eu.xap3y.xagui.interfaces.GuiInterface;
import eu.xap3y.xagui.models.GuiButton;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EggView extends VirtualMenu<EggDto> {

    public EggView() {
        super("&e&lEgg View", 6, 1, eu.xap3y.egghunt.EggHunt.getXagui());
    }

    @Override
    public @Nullable GuiInterface build(EggDto eggDto) {

        List<String> textures2 = eggDto.textures();

        if (textures2 == null) {
            return null;
        }

        Set<String> textures = new HashSet<String>(textures2);

        GuiInterface gui = getGui();
        //gui.clearAllSlots();

        Integer[] freeSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int currentPageIndex = 0;

        Set<String> textures2Copy;
        textures2Copy = new HashSet<>(textures);

        List<String> regexTextures = textures2Copy.stream().filter(s -> s.startsWith("R:") || s.startsWith("!R:")).toList();

        for (String texture : textures2Copy) {
            if (!texture.startsWith("R:") && !texture.startsWith("!R:")) {
                textures.add(texture);
            }
        }

        for (String regex : regexTextures) {
            boolean isNegative = regex.startsWith("!R:");

            regex = regex.substring(isNegative ? 3 : 2);
            if (regex.isEmpty()) continue;

            Pattern pattern = Pattern.compile(regex);

            Set<String> filteredTextures = ConfigManager.getEggStorageDto().getTextures().keySet().stream()
                    .filter(s -> {
                        var matcher = pattern.matcher(s);
                        return matcher.matches();
                    })
                    .collect(Collectors.toSet());

            if (!isNegative) textures.addAll(filteredTextures);
            else textures.removeAll(filteredTextures);
        }

        int totalPages = (int) Math.ceil(textures.size() / 28.0);
        gui.setTotalPages(totalPages > 0 ? totalPages : 1);
        gui.fillBorder();
        gui.addCloseButtonAllPages();

        List<String> animationPool = eggDto.animationPool();

        GuiButton mainIcon = new GuiButton(Material.BOOKSHELF)
                .setName("&6" + eggDto.name())
                .setLore(
                        " ",
                        " &3➥ &fPočet variant: &e" + textures.size(),
                        " &3➥ &fPočet odměn: &e" + eggDto.rewards().size(),
                        (animationPool != null && !animationPool.isEmpty()) ?
                                " &3➥ &fAnimace: &ePOOL(&f" + String.join(", ", animationPool) + "&e)"
                                :
                                " &3➥ &fAnimace: &e" + eggDto.animation(),
                        " &3➥ &fNáhodná odměna: &e" + (eggDto.randomReward() ? "&aZapnuto" : "&cVypnuto")
                );

        gui.setAllPageSlot(4, mainIcon);

        int i = 0;
        for (String texture : textures) {
            //EggHunt.getTexter().console("Texture: " + texture);
            if (texture == null) continue;

            String base64 = ConfigManager.getEggStorageDto().getTextures().get(texture);

            if (base64 == null) continue;

            ItemStack skull = Util.getTexturedSkull(base64);
            GuiButtonInterface eggButton = new GuiButton(skull)
            .setName("&6" + texture)
            .setLore(" ", "&eKlikni pro získáni")
            .withClickSound(Sound.BLOCK_NOTE_BLOCK_HARP)
            .withListener((e) -> {
                Player player = (Player) e.getWhoClicked();

                ItemStack skullItem = skull.clone();
                skullItem.setAmount(1);
                ItemMeta meta = skullItem.getItemMeta();
                meta.displayName(Component.text("§6" + texture));

                Component c1 = Component.text(" §3➥ §f" + eggDto.name());
                Component c2 = Component.text("§e" + "Polož vejce na zem.");
                Component finalComponent = Component.empty().append(Component.text(" ")).append(c1).append(Component.text(" ")).append(c2);

                meta.lore(finalComponent.children());

                meta.getPersistentDataContainer().set(Util.getNamespacedKey(), PersistentDataType.STRING, eggDto.name());
                meta.getPersistentDataContainer().set(Util.getNamespacedKeyTexture(), PersistentDataType.STRING, texture);

                skullItem.setItemMeta(meta);

                /*player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1f);*/
                player.getInventory().addItem(skullItem);
            });

            gui.setSlot(currentPageIndex, freeSlots[i], eggButton);
            i++;
            if (i > 27) {
                currentPageIndex++;
                i = 0;
            }
        }

        gui.addPaginator();
        gui.setPageSwitchSound(Sound.ITEM_BOOK_PAGE_TURN);

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
                });

        if (currentPageIndex > 0) {
            gui.setSlot(53, nextPage);
        }

        int finalCurrentPageIndex = currentPageIndex;
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
