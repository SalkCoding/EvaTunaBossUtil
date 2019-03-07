package net.evatunabossutil.salkcoding.rank;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DamageRank implements Comparable<DamageRank> {

    private Player player;
    private double damage;

    DamageRank(UUID uuid, double damage) {
        this.damage = damage;
        player = Bukkit.getPlayer(uuid);
    }

    public String getName() {
        return player.getName();
    }

    public Player getPlayer() {
        return player;
    }

    void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }


    @Override
    public int compareTo(@NotNull DamageRank o) {
        return Double.compare(o.damage, damage);
    }
}
