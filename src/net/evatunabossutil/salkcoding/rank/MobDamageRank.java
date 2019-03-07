package net.evatunabossutil.salkcoding.rank;

import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class MobDamageRank {

    private MythicMob boss;
    private Entity mobEntity;
    private HashMap<UUID, DamageRank> damageMap = new HashMap<>();
    private PriorityQueue<DamageRank> damageRanking = new PriorityQueue<>();

    public MobDamageRank(MythicMob boss, Entity mobEntity) {
        this.boss = boss;
        this.mobEntity = mobEntity;
    }

    public void addPlayerDamage(Player player, double damage) {
        UUID uuid = player.getUniqueId();
        if (damageMap.containsKey(uuid)) {
            DamageRank rank = damageMap.get(uuid);
            rank.setDamage(rank.getDamage() + damage);
            damageRanking.remove(rank);
            damageRanking.add(rank);
            //May be sync with object in PriorityQueue :P
        } else {
            DamageRank rank = new DamageRank(uuid, damage);
            rank.setDamage(rank.getDamage() + damage);
            damageMap.put(uuid, rank);
            damageRanking.add(rank);
        }
    }

    public List<DamageRank> getDamagers() {
        PriorityQueue<DamageRank> queue = new PriorityQueue<>(damageRanking);
        List<DamageRank> ranking = new ArrayList<>();
        for (int i = 0; i < 10; i++) {//1~10
            if (queue.isEmpty())
                break;
            ranking.add(queue.poll());
        }
        return ranking;
    }

    private PriorityQueue<DamageRank> getQueueDirectly(){
        return damageRanking;
    }

    private HashMap<UUID, DamageRank> getMapDirectly(){
        return damageMap;
    }

    /*public double getDamage(UUID playerUUID) {
        if (damageMap.containsKey(playerUUID))
            return damageMap.get(playerUUID).getDamage();
        return 0;
    }*/

    public MythicMob getBoss() {
        return boss;
    }

    public Entity getMobEntity() {
        return mobEntity;
    }

    public void synchroniizing(MobDamageRank target){
        this.damageRanking = target.getQueueDirectly();
        this.damageMap = target.getMapDirectly();
    }

}
