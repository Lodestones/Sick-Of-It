package gg.lode.sickofit;

import gg.lode.bookshelfapi.BookshelfAPI;
import gg.lode.bookshelfapi.IBookshelfAPI;
import gg.lode.bookshelfapi.api.Configuration;
import gg.lode.sickofit.item.SickDisc;
import gg.lode.sickofit.item.SickDiscVillager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SickOfIt extends JavaPlugin implements Listener {

    private Configuration config;

    @Override
    public void onLoad() {
        this.config = new Configuration(this, "config.yml");
        this.config.initialize();
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (config().getBoolean("should_prompt")) {
            player.setResourcePack("https://lode.gg/files/resourcepacks/sickofitpack.zip", "86e3b2930baef2103bfcf5e9893bf2e426ff3c77", config.getBoolean("should_force"));
        }
    }

    public IBookshelfAPI bookshelf() {
        return BookshelfAPI.getApi();
    }

    public Configuration config() {
        return config;
    }

    @Override
    public void onEnable() {
        BookshelfAPI.init(this, BookshelfAPI.Builder.createDisabled()
                .useItemManager(true));

        bookshelf().getItemManager().register(new SickDisc(this));
        bookshelf().getItemManager().register(new SickDiscVillager(this));

        ShapelessRecipe sickDiscRecipe = new ShapelessRecipe(new NamespacedKey(this, "sick_disc"), Objects.requireNonNull(bookshelf().getItemManager().getItemStackByClass(SickDisc.class)));
        ShapelessRecipe sickDiscRecipeVillager = new ShapelessRecipe(new NamespacedKey(this, "sick_disc_villager"), Objects.requireNonNull(bookshelf().getItemManager().getItemStackByClass(SickDiscVillager.class)));

        sickDiscRecipe.addIngredient(1, Material.DIAMOND);
        sickDiscRecipe.addIngredient(1, Material.PAINTING);
        sickDiscRecipe.addIngredient(1, Material.GOLDEN_HELMET);
        sickDiscRecipe.addIngredient(1, Material.WRITABLE_BOOK);

        sickDiscRecipeVillager.addIngredient(1, Material.DIAMOND);
        sickDiscRecipeVillager.addIngredient(1, Material.PAINTING);
        sickDiscRecipeVillager.addIngredient(1, Material.GOLDEN_HELMET);
        sickDiscRecipeVillager.addIngredient(1, Material.WRITABLE_BOOK);
        sickDiscRecipeVillager.addIngredient(1, Material.EMERALD);

        getServer().addRecipe(sickDiscRecipe);
        getServer().addRecipe(sickDiscRecipeVillager);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }
}
