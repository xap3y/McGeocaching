package eu.xap3y.egghunt.commands;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.GuiType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

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
}
