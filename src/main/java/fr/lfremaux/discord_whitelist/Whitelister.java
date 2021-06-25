package fr.lfremaux.discord_whitelist;

import fr.lfremaux.dataManager.DataManager;
import fr.lfremaux.dataManager.interfaces.Plugin;
import fr.lfremaux.dataManager.mysql.MysqlManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Whitelister extends JavaPlugin implements Plugin {

    private DataManager dataManager;

    private static Whitelister __instance;

    @Override
    public void onEnable() {
        __instance = this;

        try {
            dataManager = new DataManager(this)
                    .with(MysqlManager.class);

        } catch (IOException e) {
            log(Level.SEVERE, "Unable to connect to server database " + e.getMessage());
            e.printStackTrace();
        }

        Bukkit.setWhitelist(true);

        new WhitelistRunnable(this);

        getLogger().info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled");
    }

    public static Whitelister getInstance() {
        return __instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public void log(Level level, String s) {
        getLogger().log(level, s);
    }

    @Override
    public File getConfigFolder() {
        return new File(getDataFolder().getPath() + " /config");
    }
}
