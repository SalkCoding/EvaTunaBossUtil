package net.evatunabossutil.salkcoding.scoreboard;

import net.evatunabossutil.salkcoding.rank.DamageRank;
import net.evatunabossutil.salkcoding.rank.MobDamageRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

class MobDamageScoreBoard {

    private static final int showRanking = 6;

    static void printDamageOnScoreBoard(MobDamageRank info, MobTimerInfo timer) {
        List<DamageRank> ranking = info.getDamagers();
        for (DamageRank rank : ranking) {
            Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();
            String name = info.getBoss().getDisplayName();
            Objective damageRank = sc.registerNewObjective("damageRanking", info.getBoss().getInternalName(), name);
            if (damageRank == null) return;
            damageRank.setDisplayName(name);
            damageRank.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score timerScore = damageRank.getScore(ChatColor.WHITE + "남은 시간 : " + timer.getMinutes() + ":" + timer.getSeconds());
            timerScore.setScore(0);
            int i = 0;
            for (DamageRank damager : ranking) {
                if (!damager.getPlayer().isOnline())
                    continue;
                if (i < showRanking) {
                    double damage = damager.getDamage();
                    if (damage != 0) {
                        Score score = damageRank.getScore(ChatColor.WHITE + "" + (i + 1) + "위 : " + ChatColor.GOLD + ranking.get(i).getName() + " " + ChatColor.GRAY + "" + String.format("%.2f", ranking.get(i).getDamage()));
                        score.setScore(-1 * (i + 1));
                    }
                }
                if (rank.getPlayer().getEntityId() == damager.getPlayer().getEntityId()) {
                    Score score = damageRank.getScore(ChatColor.LIGHT_PURPLE + "내가 입힌 데미지량 : " + ChatColor.GRAY + "" + String.format("%.2f", damager.getDamage()));
                    score.setScore(-1 * (showRanking + 1));
                }
                i++;
            }
            rank.getPlayer().setScoreboard(sc);
        }
    }

    static void removePlayerScoreBoard(Player player) {
        Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(sc);
    }

}
