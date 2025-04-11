package eu.xap3y.egghunt.command;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.TreasureDto;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.gui.GeoCaching;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.util.ConfigDb;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class RootCommand {

    @Command("egghunt")
    private void helpCommand(
            CommandSender p0
    ) {
        EggHunt.getTexter().response(p0, "&fRunning GeoCaching v" + EggHunt.getVersion());
    }

    @Command("egghunt gui")
    @Permission(value = {"egghunt.*", "egghunt.gui"}, mode = Permission.Mode.ANY_OF)
    private void openTestGui(
            CommandSender p0
    ) {

        if (!(p0 instanceof Player player)) {
            EggHunt.getTexter().response(p0, ConfigDb.getOnlyPlayers());
            return;
        }

        EggHunt.getVirtualGuiRegistry().invoke(GuiType.EGG_HUNT, player, null, Boolean.class);
    }

    @Command("egghunt reload")
    @Permission(value = {"egghunt.*", "egghunt.reload"}, mode = Permission.Mode.ANY_OF)
    private void reloadPlugin(
            CommandSender p0
    ) {
        EggHunt.getTexter().response(p0, "&fZnovunačítám plugin...");
        ConfigManager.reloadConfig();
        ConfigManager.reloadStorage();
        EggHunt.getTexter().response(p0, "&aPlugin byl reloadnut");
    }

    @Command("geocaching")
    @Permission(value = {"geocaching.*", "geocaching.help"}, mode = Permission.Mode.ANY_OF)
    private void openGeoCaching(
            CommandSender p0
    ) {

        if (!(p0 instanceof Player player)) {
            EggHunt.getTexter().response(p0, ConfigDb.getOnlyPlayers());
            return;
        }

        new GeoCaching().build(false).open(player);
    }

    @Command("geocaching create [name]")
    @Permission(value = {"geocaching.*", "geocaching.create"}, mode = Permission.Mode.ANY_OF)
    private void createGeoCaching(
            @Argument("name") String name,
            CommandSender p0
    ) {
        if (!(p0 instanceof Player player)) {
            EggHunt.getTexter().response(p0, ConfigDb.getOnlyPlayers());
            return;
        }

        if (name == null) {
            EggHunt.getTexter().response(p0, "&cšpatné použití! &7/geocaching create <name>");
            return;
        }

        if (ConfigManager.getTreasureDtoList().stream().anyMatch(treasureDto -> Objects.equals(treasureDto.name(), name))) {
            EggHunt.getTexter().response(p0, "&cTreasure s tímto názvem již existuje");
            return;
        }

        ConfigManager.saveTreasureLocation(new TreasureDto(player.getLocation(), name));
        EggHunt.getTexter().response(p0, "&aTreasure byl přidán");
    }

    @Command("geocaching delete [name]")
    @Permission(value = {"geocaching.*", "geocaching.delete"}, mode = Permission.Mode.ANY_OF)
    private void deleteGeoCaching(
            @Argument("name") String name,
            CommandSender p0
    ) {
        if (name == null) {
            EggHunt.getTexter().response(p0, "&cšpatné použití! &7/geocaching delete <name>");
            return;
        }

        Optional<TreasureDto> treasureDto = ConfigManager.getTreasureDtoList().stream().filter(tr -> Objects.equals(tr.name(), name)).findFirst();

        if (treasureDto.isEmpty()) {
            EggHunt.getTexter().response(p0, "&cTreasure s tímto názvem neexistuje");
            return;
        }

        ConfigManager.removeTreasureLocation(treasureDto.get());
        EggHunt.getTexter().response(p0, "&aTreasure byl smazám");
    }
}
