package eu.xap3y.egghunt.command;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.util.ConfigDb;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

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

        EggHunt.getVirtualGuiRegistry().invoke(GuiType.MAIN, player, null, Boolean.class);
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
}
