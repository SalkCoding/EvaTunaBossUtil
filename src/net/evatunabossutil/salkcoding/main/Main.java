package net.evatunabossutil.salkcoding.main;

import net.evatunabossutil.salkcoding.Constants;
import net.evatunabossutil.salkcoding.command.SubCommand;
import net.evatunabossutil.salkcoding.config.Config;
import net.evatunabossutil.salkcoding.config.TimeConfig;
import net.evatunabossutil.salkcoding.event.BossSpawn;
import net.evatunabossutil.salkcoding.rank.MobDamageRank;
import net.evatunabossutil.salkcoding.scoreboard.ScoreBoardTimer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;

public class Main extends JavaPlugin {

    private static Main instance = null;
    /*private static Location spawn = null;

    public static Location getSpawn() {
        return spawn;
    }*/

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (instance == null)
            instance = this;
        /*getConfig().options().copyDefaults(true);
        spawn = (Location) getConfig().get("spawn");
        //getConfig().set("spawn", new Location(Bukkit.getWorld("world"), 0, 0, 0));
        saveConfig();*/
        Config.load();
        try {
            TimeConfig.loadTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new BossSpawn(), this);
        getCommand("EvaTunaBossUtil").setExecutor(new SubCommand());
        Bukkit.getLogger().info(Constants.Console + "Plugin is now enable");
    }

    @Override
    public void onDisable() {
        for (Map.Entry<Integer, MobDamageRank> rank : BossSpawn.getMobMap().entrySet()) {
            rank.getValue().getMobEntity().remove();
        }
        for (Map.Entry<Integer, ScoreBoardTimer> rank : BossSpawn.getTimerMap().entrySet()) {
            rank.getValue().safeDisable();
        }
        /*for (Map.Entry<Integer, MobTimerInfo> info : MobTimer.getList().entrySet()) {
            info.getValue().mobDeath();
        }*/
        try {
            TimeConfig.saveTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BossSpawn.getMobMap().clear();
        Bukkit.getLogger().info(Constants.Console + "Plugin is now disable");
    }

}
