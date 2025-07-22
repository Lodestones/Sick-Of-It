package gg.lode.sickofit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class SickInstance extends BukkitRunnable {

    private final HashMap<UUID, SuicideInstance> tasks = new HashMap<>();
    private final SickOfIt plugin;
    private final Location baseLocation;
    private int ticks;

    public SickInstance(SickOfIt plugin, Location baseLocation) {
        this.plugin = plugin;
        this.baseLocation = baseLocation;

        this.runTaskTimer(plugin, 1, 1);
    }

    @Override
    public void run() {
        ticks++;

        if (ticks >= (20 * 60 * 2) + (44 * 20) || this.baseLocation.getBlock().getType() != Material.JUKEBOX) {
            this.tasks.values().forEach(SuicideInstance::cancel);
            this.baseLocation.getNearbyEntitiesByType(Player.class, 40)
                    .forEach(closestPlayer -> closestPlayer.stopSound(SoundCategory.RECORDS));
            cancel();
            return;
        }

        baseLocation.getNearbyEntities(32, 32, 32)
                .forEach(entity -> {
                    if (entity instanceof Mob livingEntity && !(livingEntity instanceof Player)) {
                        if (this.tasks.containsKey(livingEntity.getUniqueId())) return;

                        SuicideInstance instance = new SuicideInstance(livingEntity, baseLocation);
                        instance.runTaskTimer(plugin, 1, 1);
                        this.tasks.put(livingEntity.getUniqueId(), instance);
                    }
                });
    }

    public HashMap<UUID, SuicideInstance> getTasks() {
        return tasks;
    }

    public Location getBaseLocation() {
        return baseLocation;
    }

//    private void triggerLastResort(LivingEntity entity) {
//        if (entity instanceof Player) {
//            Location loc = entity.getLocation().clone();
//            loc.setPitch(90);
//            entity.teleport(loc, TeleportFlag.Relative.X, TeleportFlag.Relative.Y, TeleportFlag.Relative.Z, TeleportFlag.Relative.PITCH, TeleportFlag.Relative.YAW);
//        } else {
//            entity.teleport(entity.getLocation().setDirection(entity.getLocation().getDirection().setY(-90)));
//        }
//
//        entity.getLocation().getBlock().setType(Material.LAVA);
//    }

}
