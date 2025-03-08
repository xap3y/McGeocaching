package eu.xap3y.egghunt.command;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.manager.ConfigManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class RootCommand {

    @Command("egghunt opentest")
    private void openTestGui(
            CommandSender p0
    ) {

        if (!(p0 instanceof Player player)) {
            EggHunt.getTexter().response(p0, "&cJenom hráči mohou používat tento příkaz.");
            return;
        }

        EggHunt.getVirtualGuiRegistry().invoke(GuiType.MAIN, player, null, Boolean.class);
    }

    @Command("egghunt reload")
    @Permission("egghunt.reload")
    private void reloadPlugin(
            CommandSender p0
    ) {
        EggHunt.getTexter().response(p0, "&fZnovunačítám plugin...");
        ConfigManager.reloadConfig();
        ConfigManager.reloadStorage();
        EggHunt.getTexter().response(p0, "&aPlugin byl reloadnut");
    }
}
