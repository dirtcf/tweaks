package cf.dirt.tweaks.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public final class EnderDragonListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            ItemStack stack = new ItemStack(Material.ELYTRA, 1);
            Collection<ItemStack> drops = event.getDrops();
            drops.add(stack);
        }
    }
}
