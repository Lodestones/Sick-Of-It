package gg.lode.sickofit.item;

import gg.lode.bookshelfapi.api.Task;
import gg.lode.bookshelfapi.api.item.CustomItem;
import gg.lode.bookshelfapi.api.item.ItemBuilder;
import gg.lode.bookshelfapi.api.util.MiniMessageHelper;
import gg.lode.sickofit.SickInstance;
import gg.lode.sickofit.SickOfIt;
import gg.lode.sickofit.SuicideInstance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SickDisc extends CustomItem implements Listener {

    private final SickOfIt plugin;
    private final List<SickInstance> tasks = new ArrayList<>();

    public SickDisc(SickOfIt plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.JUKEBOX) return;

        tasks.removeIf(task -> {
            if (task.getBaseLocation().equals(block.getLocation())) {
                task.getTasks().values().forEach(SuicideInstance::cancel);
                task.cancel();
                block.getLocation().getNearbyEntitiesByType(Player.class, 40)
                        .forEach(closestPlayer -> closestPlayer.stopSound(SoundCategory.RECORDS));
                return true;
            } else
                return false;
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !event.getAction().isRightClick() || clickedBlock.getType() != Material.JUKEBOX) return;

        Jukebox jukebox = (Jukebox) clickedBlock.getState();
        ItemStack itemStack = jukebox.getRecord();
        if (plugin.bookshelf().getItemManager().isCustomItem(itemStack, this.id())) {
            tasks.removeIf(task -> {
                if (task.getBaseLocation().equals(clickedBlock.getLocation())) {
                    task.getTasks().values().forEach(SuicideInstance::cancel);
                    task.cancel();
                    clickedBlock.getLocation().getNearbyEntitiesByType(Player.class, 40)
                            .forEach(closestPlayer -> closestPlayer.stopSound(SoundCategory.RECORDS));
                    return true;
                } else
                    return false;
            });
        }
    }

    @Override
    public String id() {
        return "SICK_DISC";
    }


    @Override
    public void builder(ItemBuilder builder) {
        builder
                .type(Material.MUSIC_DISC_13)
                .modelData(1)
                .title("Sick of It Disc")
                .flags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                .lore("<gray>A disc that plays the song that makes",
                        "<gray>every mob want to kill itself.");
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item && plugin.bookshelf().getItemManager().isCustomItem(item.getItemStack(), id())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onRightInteract(Player player, PlayerInteractEvent event, ItemStack item) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        if (clickedBlock.getType() != Material.JUKEBOX) return;
        Location clickedLocation = clickedBlock.getLocation();

        Jukebox jukebox = (Jukebox) clickedBlock.getState();
        jukebox.setRecord(item.asQuantity(1));

        jukebox.stopPlaying();
        jukebox.update();

        item.setAmount(0);

        player.sendActionBar(MiniMessageHelper.deserialize("<aqua>Now playing: Sick of It"));

        clickedLocation.getNearbyEntitiesByType(Player.class, 40)
                .forEach(closestPlayer -> Task.later(plugin, () -> {
                    closestPlayer.stopSound(SoundCategory.RECORDS);
                    closestPlayer.playSound(closestPlayer.getLocation(), "minecraft:music_disc.sick_of_it", SoundCategory.RECORDS, 10, 1);
                }, 1L));

        this.tasks.add(new SickInstance(plugin, clickedLocation));
    }

}
