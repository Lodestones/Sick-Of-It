package gg.lode.sickofit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SuicideInstance extends BukkitRunnable {

    private final Mob livingEntity;
    private final Location baseLocation;
    private final List<Block> lavaSources = new ArrayList<>();

    public SuicideInstance(Mob livingEntity, Location baseLocation) {
        this.livingEntity = livingEntity;
        this.baseLocation = baseLocation;

        for (int x = baseLocation.getBlockX() - 32; x < baseLocation.getBlockX() + 32; x++) {
            for (int y = baseLocation.getBlockY() - 32; y < baseLocation.getBlockY() + 32; y++) {
                for (int z = baseLocation.getBlockZ() - 32; z < baseLocation.getBlockZ() + 32; z++) {
                    Block block = baseLocation.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.LAVA) {
                        lavaSources.add(block);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        if (livingEntity.isDead() || !livingEntity.isValid() || livingEntity.getLocation().distance(baseLocation) > 32) {
            cancel();
            return;
        }

        livingEntity.setTarget(null);

        // sort for the nearest lava source
        Block lavaSource = lavaSources.stream().filter(block -> block.getType() == Material.LAVA).min((o1, o2) -> {
            double distance1 = o1.getLocation().distance(livingEntity.getLocation());
            double distance2 = o2.getLocation().distance(livingEntity.getLocation());
            return Double.compare(distance1, distance2);
        }).orElse(null);

        if (lavaSources.isEmpty() || lavaSource == null) {
            cancel();
            return;
        }

        if (!livingEntity.getPathfinder().moveTo(lavaSource.getLocation())) {
            if (livingEntity.getLocation().distance(lavaSource.getLocation().toCenterLocation()) <= 2.5) {
                livingEntity.teleport(lavaSource.getLocation().toCenterLocation().clone().add(0, -0.5, 0));
            }
        }
    }
}
