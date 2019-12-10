package cf.dirt.tweaks.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.EnumMap;
import java.util.Map;

public final class ArrowListener implements Listener {

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

    private static double roundToHalf(double value) {
        return Math.round(value * 2) / 2d;
    }

    private static void applyFire(Block block, BlockFace face) {
        Block relative = block.getRelative(face);
        relative.setBlockData(fireFaces.get(face));
    }

    private static void applyScaling(Arrow arrow) {
        final double damage = arrow.getDamage();
        final int ticks = arrow.getTicksLived();
        final double speed = arrow.getVelocity().length();

        final double scaledDamage = roundToHalf(damage * (ticks / 10d) * (speed / 2d));

        arrow.setDamage(scaledDamage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (projectile instanceof Arrow) {
            Arrow arrow = (Arrow) projectile;

            if (arrow.getFireTicks() > 0) {
                Block block = event.getHitBlock();

                if (block != null && block.getType() != Material.TNT) {
                    BlockFace face = event.getHitBlockFace();
                    applyFire(block, face);
                }
            }

            applyScaling(arrow);
        }
    }
}
