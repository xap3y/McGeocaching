package eu.xap3y.egghunt.command;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.dto.EggDto;
import eu.xap3y.egghunt.api.dto.EggStorageDto;
import eu.xap3y.egghunt.manager.ConfigManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

public class DevCommand {

    @Command("egghunt catalog")
    private void openCatalog(
            CommandSender p0
    ) {

        if (!(p0 instanceof Player player)) {
            EggHunt.getTexter().response(p0, "&cJenom hráči mohou používat tento příkaz.");
            return;
        }

        /*
        EggStorageDto storage = ConfigManager.getEggStorageDto();

        for (EggDto eggDto : storage.getEggs()) {
            EggHunt.getTexter().response(player, "&7- &e" + eggDto.name() + " &7(" + eggDto.animation() + ")");
            for (String texture : eggDto.textures()) {
                EggHunt.getTexter().response(player, "&7TEXTURE- &e" + texture);
            }
            for (String reward : eggDto.rewards()) {
                EggHunt.getTexter().response(player, "&7REWARD- &e" + reward);
            }
        }*/
    }
}
