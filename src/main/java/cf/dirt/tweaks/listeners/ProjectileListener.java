package cf.dirt.tweaks.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.EnumMap;
import java.util.Map;

public final class ProjectileListener implements Listener {

    private static final double SCALING = 0.6000000238418579d;
    private static final double KNOCK_BACK = 1.0d;
    private static final double DAMAGE = 0.5d;

    private static final Map<BlockFace, BlockData> fireFaces = new EnumMap<>(BlockFace.class);

    static {
        fireFaces.put(BlockFace.UP, Bukkit.createBlockData(
                "minecraft:fire[age=0,east=false,north=false,south=false,up=false,west=false]"
        ));
        fireFaces.put(BlockFace.DOWN, Bukkit.createBlockData(
                "minecraft:fire[age=0,east=false,north=false,south=false,up=true,west=false]"
        ));
        fireFaces.put(BlockFace.EAST, Bukkit.createBlockData(
                "minecraft:fire[age=0,east=false,north=false,south=false,up=false,west=true]"
        ));
        fireFaces.put(BlockFace.WEST, Bukkit.createBlockData(
                "minecraft:fire[age=0,east=true,north=false,south=false,up=false,west=false]"
        ));
        fireFaces.put(BlockFace.NORTH, Bukkit.createBlockData(
                "minecraft:fire[age=0,east=false,north=false,south=true,up=false,west=false]"
        ));
        fireFaces.put(BlockFace.SOUTH, Bukkit.createBlockData(
                "minecraft:fire[age=0,east=false,north=true,south=false,up=false,west=false]"
        ));
    }

    private static void applyKnockBack(Projectile projectile, Player player) {
        Vector projectileVelocity = projectile.getVelocity();
        Vector playerVelocity = player.getVelocity();

        final double projectileSpeed = Math.sqrt(
                Math.pow(projectileVelocity.getX(), 2) +
                        Math.pow(projectileVelocity.getZ(), 2)
        );

        playerVelocity.add(new Vector(
                projectileVelocity.getX() * KNOCK_BACK * SCALING / projectileSpeed, 0.1d,
                projectileVelocity.getZ() * KNOCK_BACK * SCALING / projectileSpeed
        ));

        player.setVelocity(playerVelocity);
    }

    private static void applyDamage(Projectile projectile, Player player) {
        final double health = player.getHealth();
        player.damage(DAMAGE, projectile);
        player.setHealth(health);
    }

    private static void applyFire(Block block, BlockFace face) {
        Block relative = block.getRelative(face);
        relative.setBlockData(fireFaces.get(face));
    }

    private static double roundToHalf(double value) {
        return Math.round(value * 2) / 2d;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (projectile instanceof Egg || projectile instanceof Snowball) {
            Entity entity = event.getHitEntity();

            if (entity instanceof Player) {
                Player player = (Player) entity;
                applyKnockBack(projectile, player);
                applyDamage(projectile, player);
            }
        } else {
            if (projectile instanceof Arrow) {
                if (projectile.getFireTicks() > 0) {
                    Block block = event.getHitBlock();

                    if (block != null && block.getType() != Material.TNT) {
                        BlockFace face = event.getHitBlockFace();
                        applyFire(block, face);
                    }
                }

                Arrow arrow = (Arrow) projectile;

                final double damage = arrow.getDamage();
                final int ticks = arrow.getTicksLived();
                final double speed = arrow.getVelocity().length();

                final double scaledDamage = roundToHalf(damage * (ticks / 10d) * (speed / 2d));

                arrow.setDamage(scaledDamage);
            }
        }
    }
}
