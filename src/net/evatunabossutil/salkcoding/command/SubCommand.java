package net.evatunabossutil.salkcoding.command;

import net.evatunabossutil.salkcoding.Constants;
import net.evatunabossutil.salkcoding.config.Config;
import net.evatunabossutil.salkcoding.config.TimeConfig;
import net.evatunabossutil.salkcoding.config.TimeInfo;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Map;

public class SubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!sender.isOp())
            sender.sendMessage(Constants.Error_Format + "You don't have a permission for use this command");
        //ebu spawn [...] label name DAY HOUR MINUTE
        if (strings[0].equalsIgnoreCase("spawn")) {
            if (strings.length == 7) {
                if (strings[1].equalsIgnoreCase("create")) {
                    if (sender instanceof Player) {
                        if (TimeConfig.isExist(strings[2])) {
                            sender.sendMessage(Constants.Error_Format + "Same label is already existed");
                            return true;
                        }
                        Location location = ((Player) sender).getLocation();
                        String label = strings[2];
                        String name = strings[3];
                        int day = Integer.parseInt(strings[4]);
                        int hour = Integer.parseInt(strings[5]);
                        int minute = Integer.parseInt(strings[6]);
                        TimeConfig.addTime(label, name, location, day, hour, minute);
                        sender.sendMessage(Constants.Info_Format + "Label is successfully created on your location");
                    }
                } else sender.sendMessage(Constants.Warn_Format + "Player can only use this command");
            } else if (strings.length == 3) {
                if (strings[1].equalsIgnoreCase("remove")) {
                    TimeConfig.removeTime(strings[2]);
                    sender.sendMessage(Constants.Info_Format + "Remove that label");
                }else sendCommandList(sender);
            } else if (strings.length == 2) {
                if (strings[1].equalsIgnoreCase("list")) {
                    sender.sendMessage(Constants.Info_Format + "Spawn label list");
                    sender.sendMessage(Constants.Info_Format  + "Current Time : " + Calendar.getInstance().getTimeInMillis());
                    for (Map.Entry<String, TimeInfo> element : TimeConfig.getList()) {
                        TimeInfo info = element.getValue();
                        sender.sendMessage("Label : " + element.getKey());
                        sender.sendMessage("    Mob name : " + info.getInternalName());
                        sender.sendMessage("    Next spawn : " + info.getTime().getTimeInMillis());
                        sender.sendMessage("    Spawn location : (world : " + info.getSpawnLocation().getWorld().getName()
                                + " x : " + info.getSpawnLocation().getBlockX()
                                + " y : " + info.getSpawnLocation().getY()
                                + " z : " + info.getSpawnLocation().getZ());
                    }
                }
            } else sendCommandList(sender);
        } else if (strings[0].equalsIgnoreCase("reload") && strings[1].equalsIgnoreCase("confirm")) {
            if (strings.length == 3) {
                Config.load();
            } else sendCommandList(sender);
        } else if (strings[0].equalsIgnoreCase("reload")) {
            if (strings.length == 1) {
                sender.sendMessage(Constants.Warn_Format + "If you want yo reload the config file, enter the command /ebu reload confirm.\r\nIt will be reload the config and remove entities which is registered in scoreboard ");
            } else sendCommandList(sender);
        } else sendCommandList(sender);
        return true;
    }

    private void sendCommandList(CommandSender sender) {
        sender.sendMessage(Constants.Error_Format + "Wrong command usage");
        sender.sendMessage(Constants.Error_Format + "/Ebu Spawn Create Label Name DAY HOUR MINUTE : Create an auto mob spawn timer");
        sender.sendMessage(Constants.Error_Format + "/Ebu Spawn Remove : Remove an auto mob spawn timer");
        sender.sendMessage(Constants.Error_Format + "/Ebu Spawn List : Show timer list");
        sender.sendMessage(Constants.Error_Format + "/Ebu Reload : Reload plugin config");
    }

}
