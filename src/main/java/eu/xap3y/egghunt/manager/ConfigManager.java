package eu.xap3y.egghunt.manager;

import eu.xap3y.egghunt.EggHunt;

public class ConfigManager {

    public static void reloadConfig() {
        if (!EggHunt.getInstance().getDataFolder().exists()) {
            EggHunt.getInstance().getDataFolder().mkdir();
        }

        EggHunt.getInstance().saveDefaultConfig();
        EggHunt.getInstance().reloadConfig();
    }

}
