package net.evatunabossutil.salkcoding.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.evatunabossutil.salkcoding.Constants;
import net.evatunabossutil.salkcoding.main.Main;

import java.io.*;
import java.util.HashMap;

public class Config {

    private static boolean isInit = false;
    private static HashMap<String, ConfigInfo> configMap = new HashMap<>();

    public static boolean isContainInSet(String name) {
        if (!isInit) throw new IllegalStateException("It is not initialized yet!");
        return configMap.containsKey(name);
    }

    public static ConfigInfo getConfigSetting(String name) {
        if (!isInit) throw new IllegalStateException("It is not initialized yet!");
        return configMap.get(name);
    }

    public static void load() {
        File dir = Main.getInstance().getDataFolder();
        File config = new File(dir, "config.json");
        try {
            if (!dir.exists())
                dir.mkdirs();
            if (!config.exists()) {
                if (config.createNewFile()) {
                    FileWriter writer = new FileWriter(config);
                    writer.write("[]");
                    writer.flush();
                    writer.close();
                }
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(config));
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) builder.append(str);
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(builder.toString()).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject json = element.getAsJsonObject();
                String name = json.get("name").getAsString();
                if (json.get("isPhase").getAsBoolean())
                    configMap.put(name, new ConfigInfo(json.get("condition").getAsInt(), json.get("minute").getAsInt(), json.get("second").getAsInt(), true, name, json.get("nextPhase").getAsString()));
                else
                    configMap.put(name, new ConfigInfo(json.get("condition").getAsInt(), json.get("minute").getAsInt(), json.get("second").getAsInt(), false, null, null));
            }
        } catch (IOException e) {
            System.out.println(Constants.Console + "Config format error.");
            System.out.println(Constants.Console + "Please rewrite config file correctly");
            e.printStackTrace();
        } finally {
            isInit = true;
        }
    }

}
