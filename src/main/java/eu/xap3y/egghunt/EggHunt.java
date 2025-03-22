package eu.xap3y.egghunt;

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import eu.xap3y.egghunt.api.enums.GuiType;
import eu.xap3y.egghunt.api.enums.StaticGuiItems;
import eu.xap3y.egghunt.api.gui.MainGui;
import eu.xap3y.egghunt.command.DevCommand;
import eu.xap3y.egghunt.command.RootCommand;
import eu.xap3y.egghunt.listener.BlockListener;
import eu.xap3y.egghunt.listener.PlayerListener;
import eu.xap3y.egghunt.manager.CommandManager;
import eu.xap3y.egghunt.manager.ConfigManager;
import eu.xap3y.egghunt.service.ParticleService;
import eu.xap3y.egghunt.service.Texter;
import eu.xap3y.xagui.GuiRegistry;
import eu.xap3y.xagui.XaGui;
import eu.xap3y.xagui.models.GuiButton;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EggHunt extends JavaPlugin {

    @Getter
    private static String version = "@VERSION@";

    @Getter
    private static EggHunt instance;

    @Getter
    @Setter
    private static Texter texter;

    @Getter
    private static XaGui xagui;

    @Getter
    private static ParticleNativeAPI parApi;

    @Getter
    private static final GuiRegistry<GuiType> virtualGuiRegistry = new GuiRegistry<GuiType>();

    @Getter
    private static final ParticleService particleService = new ParticleService();

    @Override
    public void onEnable() {

        instance = this;

        xagui = new XaGui(this);

        xagui.setCloseButton(StaticGuiItems.CLOSE.getButton());

        xagui.setPreviousPageButton(new GuiButton(Material.ARROW)
                .setName("&6Předchozí strana").getItem());

        xagui.setNextPageButton(new GuiButton(Material.ARROW)
                .setName("&6Další strana").getItem());

        parApi = ParticleNativeCore.loadAPI(this);

        virtualGuiRegistry.register(GuiType.MAIN, new MainGui(), Boolean.class);
        virtualGuiRegistry.register(GuiType.EGG_HUNT, new eu.xap3y.egghunt.api.gui.EggHunt(), Boolean.class);
        /*virtualGuiRegistry.register(GuiType.EGG_VIEW, new eu.xap3y.egghunt.api.gui.EggView(), EggDto.class);*/

        ConfigManager.reloadConfig();
        ConfigManager.reloadStorage();

        CommandManager cmdManager = new CommandManager(this);
        cmdManager.parse(new RootCommand());

        if (this.getConfig().getBoolean("debug", false)) {
            texter.console("&cDebug mode is enabled.");
            cmdManager.parse(new DevCommand());
        }

        //   Registering listeners  \\

        PluginManager manager = getServer().getPluginManager();
        registerListeners(manager);

        //  Registering PlaceholderAPI  \\
        //registerPapi();

        // Get current mc version as x.x.x
        particleService.init();
    }

    private static void registerListeners(PluginManager manager) {
        //  Registering listeners  \\
        Listener[] listeners = new Listener[]{
            new BlockListener(),
            new PlayerListener()
        };

        for (Listener listener : listeners) {
            manager.registerEvents(listener, EggHunt.getInstance());
        }
    }

    /*private void registerPapi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            *//*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             *//*
            //Bukkit.getPluginManager().registerEvents(new MyListener(), this);
        } else {
            *//*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             *//*
            //Bukkit.getPluginManager().disablePlugin(this);
        }
    }*/
}
