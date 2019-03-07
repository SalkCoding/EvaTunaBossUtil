package net.evatunabossutil.salkcoding.scoreboard;

import net.evatunabossutil.salkcoding.rank.DamageRank;
import net.evatunabossutil.salkcoding.rank.MobDamageRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class ScoreBoardRemover implements Runnable {

    //private BukkitTask task;
    private MobDamageRank rank;

    ScoreBoardRemover(MobDamageRank rank) {
        this.rank = rank;
    }

    /*void setTask(BukkitTask task) {
        this.task = task;
    }*/

    @Override
    public void run() {
        //Don't need to store that list
        List<DamageRank> ranking = rank.getDamagers();
        for (Player player : Bukkit.getOnlinePlayers()) {
            /*if (!player.isOp())
                player.teleport(Main.getSpawn());*/
            MobDamageScoreBoard.removePlayerScoreBoard(player);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "=======================");
            player.sendMessage(rank.getBoss().getDisplayName() + ChatColor.GRAY + "에게 입힌 " + ChatColor.GOLD + "데미지 " + ChatColor.GRAY + "순위");
            int i = 1;
            for (DamageRank info : ranking) {
                player.sendMessage(i + "위 : " + ChatColor.GOLD + info.getName() + ChatColor.GRAY + String.format(" %.2f", info.getDamage()));
                i++;
            }
            player.sendMessage(ChatColor.LIGHT_PURPLE + "=======================");
        }
        //task.cancel();
    }
}
