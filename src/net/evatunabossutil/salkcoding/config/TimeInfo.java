package net.evatunabossutil.salkcoding.config;

import org.bukkit.Location;

import java.util.Calendar;

public class TimeInfo {

    private String internalName;
    private Location spawnLocation;
    private Calendar time;

    private int addMinuteAmount;

    TimeInfo(String internalName, Location spawnLocation, int addMinuteAmount) {
        this.internalName = internalName;
        this.spawnLocation = spawnLocation;
        this.time = Calendar.getInstance();
        this.addMinuteAmount = addMinuteAmount;
    }

    TimeInfo(String internalName, Location spawnLocation, int day, int hour, int minute) {
        this(internalName, spawnLocation, (day * 1440) + (hour * 24) + minute);
    }

    public String getInternalName() {
        return internalName;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Calendar getTime() {
        return time;
    }

    public int getAddMinuteAmount() {
        return addMinuteAmount;
    }

    public void resetTime() {
        time.add(Calendar.MINUTE, addMinuteAmount);
    }
}
