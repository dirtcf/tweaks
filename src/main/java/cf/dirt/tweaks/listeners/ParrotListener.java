package cf.dirt.tweaks.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

public final class ParrotListener implements Listener {

    private static final Set<DamageCause> ignoredCauses = EnumSet.of(
            BLOCK_EXPLOSION, DROWNING, FALLING_BLOCK, FIRE, FIRE_TICK,
            LAVA, LIGHTNING, MAGIC, POISON, SUFFOCATION, VOID, WITHER
    );

    private static Parrot spawnParrot(Parrot parrot, Location location) {
        World world = location.getWorld();
        return world.spawn(location, Parrot.class, (newParrot) ->  {

            newParrot.setCustomName(parrot.getCustomName());
            newParrot.setCustomNameVisible(parrot.isCustomNameVisible());
            newParrot.setBreed(parrot.canBreed());
            newParrot.setOwner(parrot.getOwner());
            newParrot.setTamed(parrot.isTamed());
            newParrot.setVariant(parrot.getVariant());
            newParrot.setAge(parrot.getAge());
            newParrot.setAI(parrot.hasAI());

            if (parrot.isAdult()) {
                newParrot.setAdult();
            } else {
                newParrot.setBaby();
            }
        });
    }

    private static void releaseParrot(Player player) {
        Location location = player.getLocation().add(new Vector(0, 2, 0));

        Entity left = player.getShoulderEntityLeft();
        if (left != null) {
            if (left instanceof Parrot) {
                spawnParrot((Parrot) left, location);
                player.setShoulderEntityLeft(null);
            }
        }

        Entity right = player.getShoulderEntityRight();
        if (right != null) {
            if (right instanceof Parrot) {
                spawnParrot((Parrot) right, location);
                player.setShoulderEntityRight(null);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SHOULDER_ENTITY) {
            if (event.getEntity() instanceof Parrot) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerSwim(EntityToggleSwimEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.isSwimming()) {
                Player player = (Player) event.getEntity();
                releaseParrot(player);
            }
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            Player player = event.getPlayer();
            releaseParrot(player);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (!ignoredCauses.contains(event.getCause())) {
                return;
            }

            final double damage = event.getDamage();

            Entity left = player.getShoulderEntityLeft();
            if (left instanceof Parrot) {
                Parrot leftParrot = (Parrot) left;

                if (damage >= leftParrot.getHealth()) {
                    Parrot newLeftParrot = spawnParrot(leftParrot, player.getLocation());
                    player.setShoulderEntityLeft(null);
                    newLeftParrot.damage(damage);
                } else {
                    leftParrot.damage(damage);
                    player.setShoulderEntityLeft(leftParrot);
                }
            }

            Entity right = player.getShoulderEntityRight();
            if (right instanceof Parrot) {
                Parrot rightParrot = (Parrot) right;

                if (damage >= rightParrot.getHealth()) {
                    Parrot newRightParrot = spawnParrot(rightParrot, player.getLocation());
                    player.setShoulderEntityRight(null);
                    newRightParrot.damage(damage);
                } else {
                    rightParrot.damage(damage);
                    player.setShoulderEntityRight(rightParrot);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        releaseParrot(player);
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player player = event.getPlayer();
        releaseParrot(player);
    }
}
