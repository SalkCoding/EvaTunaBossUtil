package net.evatunabossutil.salkcoding.scoreboard;

import net.evatunabossutil.salkcoding.main.Main;
import net.evatunabossutil.salkcoding.rank.MobDamageRank;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ScoreBoardTimer implements Runnable {

    private MobDamageRank rank;
    private BukkitTask task;
    private MobTimerInfo timer;

    private boolean mobDeath = false;

    public ScoreBoardTimer(MobDamageRank info, int minute, int second) {
        //info.getBoss().getDisplayName().setCustomName(ChatColor.translateAlternateColorCodes('&', info.getBoss().getDisplayName()));
        this.rank = info;
        this.timer = new MobTimerInfo(rank, minute, second);
        timer.setTask(Bukkit.getScheduler().runTaskTimer(Main.getInstance(), timer, 0, 20));
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public void safeDisable() {
        task.cancel();
    }

    public void setMobDeath(boolean death) {
        this.mobDeath = death;
    }

    @Override
    public void run() {
        MobDamageScoreBoard.printDamageOnScoreBoard(rank, timer);
        if (rank.getMobEntity().isDead() || mobDeath) {
            ScoreBoardRemover scoreBoardRemover = new ScoreBoardRemover(rank);
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), scoreBoardRemover, 100);
            task.cancel();
        }
    }

}
