package me.robertlit.attackdogs;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftWolf;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public final class AttackDogs extends JavaPlugin implements Listener {

    private boolean attackPlayers;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        attackPlayers = getConfig().getBoolean("attack-players", true);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Wolf) {
            addGoals((Wolf) entity);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof Wolf) {
                addGoals((Wolf) entity);
            }
        }
    }

    private void addGoals(@NotNull Wolf wolf) {
        EntityWolf handle = ((CraftWolf) wolf).getHandle();
        handle.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<>(handle, EntityHuman.class, 10, true, true, entityLiving -> {
            AnimalTamer owner = wolf.getOwner();
            if (!wolf.isTamed() || owner == null) {
                return false;
            }
            return shouldAttack(owner, entityLiving);
        }));
        Class<? extends EntityLiving>[] classes = new Class[] {EntityZombie.class, EntitySkeleton.class, EntitySpider.class, EntityDrowned.class, EntityZombieHusk.class, EntityCaveSpider.class, EntitySilverfish.class, EntitySkeletonWither.class, EntityPiglin.class, EntityHoglin.class, EntityPillager.class, EntityWitch.class, EntitySkeletonStray.class, EntityEndermite.class, EntityEvoker.class, EntityIllagerIllusioner.class, EntityVindicator.class, EntitySlime.class, EntityMagmaCube.class, EntityPiglinBrute.class};
        for (Class<? extends EntityLiving> clazz : classes) {
            handle.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<>(handle, clazz, 10, true, true, entityLiving -> wolf.isTamed()));
        }
    }

    private boolean shouldAttack(@NotNull AnimalTamer owner, @NotNull EntityLiving victim) {
        if (!attackPlayers || owner.getUniqueId().equals(victim.getBukkitEntity().getUniqueId())) {
            return false;
        }
        ScoreboardManager manager = getServer().getScoreboardManager();
        String name = owner.getName();
        if (manager == null || name == null) {
            return true;
        }
        Team team = manager.getMainScoreboard().getEntryTeam(name);
        if (team == null) {
            return true;
        }
        return !team.getEntries().contains(victim.getBukkitEntity().getName());
    }

//        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
//            for (Wolf wolf : Bukkit.getWorlds().stream().map(world -> world.getEntitiesByClass(Wolf.class)).flatMap(Collection::stream).collect(Collectors.toSet())) {
//                AnimalTamer owner = wolf.getOwner();
//                if (!wolf.isTamed() || owner == null) {
//                    continue;
//                }
//                if (wolf.getTarget() != null && !wolf.getTarget().isDead()) {
//                    continue;
//                }
//                for (Entity entity : wolf.getNearbyEntities(16, 16, 16)) {
//                    if ((entity instanceof LivingEntity && types.contains(entity.getType())) || (entity instanceof Player && shouldAttack(owner, (Player) entity))) {
//                        wolf.setTarget((LivingEntity) entity);
//                        break;
//                    }
//                }
//            }
//        }, 0, 1);

//    private boolean shouldAttack(AnimalTamer owner, Player victim) {
//        if (!attackPlayers || owner.getUniqueId().equals(victim.getUniqueId())) {
//            return false;
//        }
//        ScoreboardManager manager = getServer().getScoreboardManager();
//        String name = owner.getName();
//        if (manager == null || name == null) {
//            return true;
//        }
//        Team team = manager.getMainScoreboard().getEntryTeam(name);
//        if (team == null) {
//            return true;
//        }
//        return !team.getEntries().contains(victim.getName());
//    }
}
