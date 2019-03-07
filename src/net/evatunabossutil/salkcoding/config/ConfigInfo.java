package net.evatunabossutil.salkcoding.config;

public class ConfigInfo {

    private int condition;
    private int minute;
    private int second;
    private boolean phase;
    private String previousName;
    private String nextPhase;

    ConfigInfo(int condition, int minute, int second, boolean phase,String previousName, String nextPhase) {
        this.condition = condition;
        this.minute = minute;
        this.second = second;
        this.phase = phase;
        this.previousName = previousName;
        this.nextPhase = nextPhase;
    }

    public int getCondition() {
        return condition;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public boolean isPhase() {
        return phase;
    }

    public String getPreviousName() {
        return previousName;
    }

    public String getNextPhase() {
        return nextPhase;
    }
}
