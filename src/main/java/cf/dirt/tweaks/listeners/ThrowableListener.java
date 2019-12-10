package cf.dirt.tweaks.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public final class ThrowableListener implements Listener {

    private static final double SCALING = 0.6000000238418579d;
    private static final double KNOCK_BACK = 1.0d;
    private static final double DAMAGE = 0.5d;

    private static final float VOLUME = 0.5f;
    private static final float PITCH = 2.0f;

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (projectile instanceof Egg || projectile instanceof Snowball) {
            Entity entity = event.getHitEntity();

            if (entity instanceof Player) {
                Player player = (Player) entity;

                if (!player.isBlocking()) {
                    applyKnockBack(projectile, player);
                    applyDamage(projectile, player);
                } else {
                    player.playSound(
                            player.getLocation(),
                            Sound.ITEM_SHIELD_BLOCK,
                            VOLUME, PITCH
                    );
                }
            }
        }
    }
}
