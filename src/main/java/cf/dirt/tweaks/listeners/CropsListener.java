package cf.dirt.tweaks.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public final class CropsListener implements Listener {

    private static final Set<Material> seedTypes = EnumSet.of(
            Material.BEETROOT_SEEDS,
            Material.WHEAT_SEEDS,
            Material.CARROT,
            Material.POTATO
    );

    private static final Set<Material> cropTypes = EnumSet.of(
            Material.BEETROOTS,
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES
    );

    private static final Set<Material> toolTypes = EnumSet.of(
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE
    );

    private static void releaseDrops(Collection<ItemStack> drops, Block block) {
        World world = block.getWorld();
        Location location = block.getLocation();

        for (ItemStack stack : drops) {
            if (stack.getAmount() > 0) {
                world.dropItemNaturally(location, stack);
            }
        }
    }

    private static void replantCrops(ItemStack stack, Block block, Ageable data) {
        final int amount = stack.getAmount();
        stack.setAmount(amount - 1);

        data.setAge(0);
        block.setBlockData(data);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (!toolTypes.contains(hand.getType())) {
            return;
        }

        Block block = event.getBlock();

        if (block.getBlockData() instanceof Ageable) {
            Block relative = block.getRelative(BlockFace.DOWN);

            if (relative.getBlockData() instanceof Farmland) {
                Ageable data = (Ageable) block.getBlockData();

                if (!cropTypes.contains(block.getType())) {
                    return;
                }

                if (data.getAge() >= data.getMaximumAge()) {
                    Collection<ItemStack> drops = block.getDrops();

                    for (ItemStack stack : drops) {
                        if (seedTypes.contains(stack.getType())) {
                            replantCrops(stack, block, data);

                            if (player.getGameMode() == GameMode.SURVIVAL) {
                                releaseDrops(drops, block);
                            }

                            break;
                        }
                    }
                }

                event.setCancelled(true);
            }
        }
    }
}
