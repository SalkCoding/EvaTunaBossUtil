package net.evatunabossutil.salkcoding.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitWorld;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import net.evatunabossutil.salkcoding.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;

public class TimeConfig {

    private static final File dir = new File(Main.getInstance().getDataFolder(), "SpawnTime");
    private static HashMap<String, TimeInfo> timeMap = new HashMap<>();

    private static BukkitTask task;

    private static boolean loaded = false;

    public static void saveTime() throws IOException {
        task.cancel();
        Gson gson = new Gson();
        if (!dir.exists())
            dir.mkdirs();
        for (Map.Entry<String, TimeInfo> element : timeMap.entrySet()) {
            File file = new File(dir, element.getKey() + ".json");
            if (!file.exists())
                file.createNewFile();
            TimeInfo info = timeMap.get(element.getKey());
            JsonObject json = new JsonObject();
            Location loc = info.getSpawnLocation();
            json.addProperty("Label", element.getKey());
            json.addProperty("Internal name", info.getInternalName());
            json.addProperty("Next spawn", info.getTime().getTimeInMillis());
            json.addProperty("AddMinuteAmount", info.getAddMinuteAmount());
            json.addProperty("world", loc.getWorld().getName());
            json.addProperty("x", loc.getX());
            json.addProperty("y", loc.getY());
            json.addProperty("z", loc.getZ());
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(json));
            writer.flush();
            writer.close();
        }
    }

    public static void loadTime() throws IOException {
        if (!dir.exists()) {
            dir.mkdirs();
            loaded = true;
            return;
        }
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) builder.append(str);
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(builder.toString()).getAsJsonObject();
            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(json.get("Next spawn").getAsLong());
            Location location = new Location(Bukkit.getWorld(json.get("world").getAsString()), json.get("x").getAsDouble(), json.get("y").getAsDouble(), json.get("z").getAsDouble());
            TimeInfo info = new TimeInfo(json.get("Internal name").getAsString(), location, json.get("AddMinuteAmount").getAsInt());
            timeMap.put(json.get("Label").getAsString(), info);
        }
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), TimeConfig::spawn, 0, 200);
        loaded = true;
    }

    private static void spawn() {
        Calendar now = Calendar.getInstance();
        BukkitAPIHelper helper = MythicMobs.inst().getAPIHelper();
        for (Map.Entry<String, TimeInfo> element : getList()) {
            if (now.after(element.getValue().getTime())) {
                Bukkit.getScheduler().runTask(Main.getInstance(),()->{
                    MythicMob mob = helper.getMythicMob(element.getValue().getInternalName());//API 개같은거 Async에서 작동안함
                    Location location = element.getValue().getSpawnLocation();
                    mob.spawn(new AbstractLocation(new BukkitWorld(location.getWorld()), location.getX(), location.getY(), location.getZ()), 0);
                });
                element.getValue().resetTime();
            }
        }
    }

    public static Set<Map.Entry<String, TimeInfo>> getList() {
        return timeMap.entrySet();
    }

    public static void addTime(String label, String name, Location location, int day, int hour, int minute) {
        if (!timeMap.containsKey(label)) {
            TimeInfo info = new TimeInfo(name, location, day, hour, minute);
            timeMap.put(label, info);
        }
    }

    public static boolean isExist(String label) {
        return timeMap.containsKey(label);
    }

    /*public static Calendar getTime(String label) {
        if (!loaded) throw new IllegalStateException("It is not loaded");
        return timeMap.containsKey(label) ? timeMap.get(label).getTime() : null;
    }*/

    public static void removeTime(String label) {
        if (!loaded) throw new IllegalStateException("It is not loaded");
        timeMap.remove(label);
        if (dir.exists())
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.getName().equals(label)) {
                    file.delete();
                    break;
                }
            }
    }

}
