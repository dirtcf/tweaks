package cf.dirt.tweaks.listeners;

import cf.dirt.tweaks.Plugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public final class RespawnListener implements Listener {

    private final Plugin plugin;
    private final Location location;

    public RespawnListener(Plugin plugin, World world) {
        this.plugin = plugin;
        this.location = world.getSpawnLocation().add(
                new Vector(0.5, 0, 0.5)
        );
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            event.setSpawnLocation(location);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (player.getBedSpawnLocation() == null) {
            event.setRespawnLocation(location);
        }

        final Location deathLocation = player.getLocation();

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setCompassTarget(deathLocation);
            }
        }.runTaskLater(plugin, 1);
    }
}
