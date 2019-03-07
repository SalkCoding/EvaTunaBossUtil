package net.evatunabossutil.salkcoding.scoreboard;

import net.evatunabossutil.salkcoding.event.BossSpawn;
import net.evatunabossutil.salkcoding.rank.DamageRank;
import net.evatunabossutil.salkcoding.rank.MobDamageRank;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

public class MobTimerInfo implements Runnable {

    private int min;//시간 설정
    private int sec;
    private MobDamageRank rank;
    private BukkitTask task;

    void setTask(BukkitTask task) {
        this.task = task;
    }

    private void mobDeath() {
        task.cancel();
    }

    MobTimerInfo(MobDamageRank rank, int minute, int second) {
        this.rank = rank;
        this.min = minute;
        this.sec = second;
    }

    int getMinutes() {
        return min;
    }

    int getSeconds() {
        return sec;
    }

    @Override
    public void run() {
        if (rank.getMobEntity().isDead()) {
            task.cancel();
        } else {
            if (sec <= 0 && min <= 0) {//fail
                //Bukkit.broadcastMessage(Constants.Format + ChatColor.RED + "시간 초과로 보스 레이드에 실패하였습니다.");
                for (DamageRank player : rank.getDamagers()) {
                    //player.getPlayer().teleport(Main.getSpawn());
                    player.getPlayer().sendTitle(ChatColor.RED + "보스 레이드", ChatColor.RED + "시간 초과로 보스 레이드에 실패하였습니다.", 30, 60, 30);
                }
                ScoreBoardTimer timer = BossSpawn.getTimerMap().get(rank.getMobEntity().getEntityId());
                timer.setMobDeath(true);
                BossSpawn.getTimerMap().remove(rank.getMobEntity().getEntityId());
                rank.getMobEntity().remove();
                mobDeath();
                return;
            } else if (sec <= 0) {
                sec = 59;
                min--;
            }
            sec--;
        }
    }
}
