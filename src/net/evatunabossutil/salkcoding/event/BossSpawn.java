package net.evatunabossutil.salkcoding.event;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.meowj.langutils.lang.LanguageHelper;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitItemStack;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobLootDropEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.LootBag;
import io.lumine.xikage.mythicmobs.drops.droppables.ItemDrop;
import net.ess3.api.Economy;
import net.evatunabossutil.salkcoding.Constants;
import net.evatunabossutil.salkcoding.config.Config;
import net.evatunabossutil.salkcoding.config.ConfigInfo;
import net.evatunabossutil.salkcoding.main.Main;
import net.evatunabossutil.salkcoding.rank.DamageRank;
import net.evatunabossutil.salkcoding.rank.MobDamageRank;
import net.evatunabossutil.salkcoding.scoreboard.ScoreBoardTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BossSpawn implements Listener {

    private static HashMap<Integer, MobDamageRank> bossMap = new HashMap<>();
    private static HashMap<Integer, ScoreBoardTimer> scoreBoardTimerHashMap = new HashMap<>();

    public static HashMap<Integer, MobDamageRank> getMobMap() {
        return bossMap;
    }

    public static HashMap<Integer, ScoreBoardTimer> getTimerMap() {
        return scoreBoardTimerHashMap;
    }

    @EventHandler
    public void onSpawn(MythicMobSpawnEvent event) {//We have to test about Map sync!
        int entityId = event.getEntity().getEntityId();
        String internalName = event.getMobType().getInternalName();
        if (Config.isContainInSet(internalName)) {
            if (!bossMap.containsKey(entityId)) {
                MobDamageRank rank = new MobDamageRank(event.getMobType(), event.getEntity());
                ConfigInfo info = Config.getConfigSetting(internalName);//Already verify on first if sentence
                if (info.getNextPhase().equals(internalName)) {
                    int id = getMythicMobWithName(info.getPreviousName());
                    if (id > -1) {
                        rank.synchroniizing(bossMap.get(id));
                        bossMap.remove(id);
                    }
                }
                bossMap.put(entityId, rank);

            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        if (bossMap.containsKey(target.getEntityId())) {
            MobDamageRank rank = bossMap.get(target.getEntityId());
            Entity damager = event.getDamager();
            if (damager instanceof Player) {
                rank.addPlayerDamage((Player) damager, event.getDamage());
            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player)
                    rank.addPlayerDamage((Player) shooter, event.getDamage());
            }

            if (damager instanceof Player || damager instanceof Projectile) {
                if (!scoreBoardTimerHashMap.containsKey(rank.getMobEntity().getEntityId())) {
                    ConfigInfo info = Config.getConfigSetting(rank.getBoss().getInternalName());//Already verify with previous if code
                    ScoreBoardTimer timer = new ScoreBoardTimer(rank, info.getMinute(), info.getSecond());
                    timer.setTask(Bukkit.getScheduler().runTaskTimer(Main.getInstance(), timer, 0, 20));
                    scoreBoardTimerHashMap.put(rank.getMobEntity().getEntityId(), timer);
                }
            }
            //MobTimer.addTimer(rank);
        }
    }

    private static HashMap<Integer, LootBag> dropMap = new HashMap<>();

    @EventHandler
    public void onDrop(MythicMobLootDropEvent event) {
        if (bossMap.containsKey(event.getEntity().getEntityId()))
            dropMap.put(event.getEntity().getEntityId(), event.getDrops());
    }

    @EventHandler
    public void onDeathMob(MythicMobDeathEvent event) throws UserDoesNotExistException, NoLoanPermittedException {
        int entityId = event.getEntity().getEntityId();
        if (bossMap.containsKey(entityId)) {
            MobDamageRank rank = bossMap.get(entityId);
            if (rank.getMobEntity().getEntityId() == event.getEntity().getEntityId()) {

                ConfigInfo info = Config.getConfigSetting(rank.getBoss().getInternalName());
                if (info.isPhase()) {
                    event.setCurrency(0);
                    event.setExp(0);
                    event.getDrops().clear();

                    ScoreBoardTimer timer = scoreBoardTimerHashMap.get(entityId);
                    timer.setMobDeath(true);
                    scoreBoardTimerHashMap.remove(entityId);
                    dropMap.remove(entityId);
                    return;
                }

                int condition = info.getCondition();
                LootBag lootBag = dropMap.get(entityId);
                double amount = event.getCurrency();
                Random random = new Random(System.currentTimeMillis());

                for (DamageRank damageRank : rank.getDamagers()) {
                    Player player = damageRank.getPlayer();
                    player.setExp(player.getExp() + event.getExp());
                    amount = random.nextDouble() * amount;
                    Economy.add(player.getName(), new BigDecimal(amount));

                    player.sendMessage(Constants.Info_Format + rank.getBoss().getDisplayName()
                            + ChatColor.GRAY + ChatColor.ITALIC + "을/를 잡아 " + ChatColor.GOLD + amount + ChatColor.GRAY + ChatColor.ITALIC + "캔과 "
                            + ChatColor.GOLD + event.getExp() + ChatColor.GRAY + ChatColor.ITALIC + "만큼의 경험치를 얻으셨습니다.");
                    if (damageRank.getDamage() >= condition) {
                        StringBuilder builder = new StringBuilder(Constants.Info_Format + rank.getBoss().getDisplayName() +
                                ChatColor.GRAY + ChatColor.ITALIC + "에게 " + ChatColor.GOLD + condition + ChatColor.GRAY + ChatColor.ITALIC + "이상의 피해를 입히셔서 보상으로 ");
                        int i = 1;
                        for (Drop drop : lootBag.getDrops()) {
                            ItemDrop itemDrop = (ItemDrop) drop;
                            if (!itemDrop.rollChance()) continue;
                            itemDrop.rollAmount();
                            BukkitItemStack itemStack = (BukkitItemStack) itemDrop.getDrop(lootBag.getMetadata());
                            ItemStack item = itemStack.build();
                            player.getInventory().addItem(item);
                            if (i == lootBag.size())
                                builder.append(LanguageHelper.getItemDisplayName(item, player)).append(" ").append(item.getAmount()).append("개 ");
                            else
                                builder.append(LanguageHelper.getItemDisplayName(item, player)).append(" ").append(item.getAmount()).append("개, ");
                            i++;
                        }
                        builder.append("을/를 획득하셨습니다.");
                        player.sendMessage(builder.toString());
                    }
                }

                remove(event, entityId);
            }
        }
    }

    private void remove(MythicMobDeathEvent event, int entityId) {
        event.setCurrency(0);
        event.setExp(0);
        event.getDrops().clear();

        ScoreBoardTimer timer = scoreBoardTimerHashMap.get(entityId);
        timer.setMobDeath(true);
        bossMap.remove(entityId);
        scoreBoardTimerHashMap.remove(entityId);
        dropMap.remove(entityId);
    }

    private int getMythicMobWithName(String name) {
        for (Map.Entry<Integer, MobDamageRank> element : bossMap.entrySet()) {
            MobDamageRank rank = element.getValue();
            if (rank.getBoss().getInternalName().equals(name))
                return element.getKey();
        }
        return -1;
    }

}
