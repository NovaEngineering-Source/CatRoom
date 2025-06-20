package catserver.server.utils;

import catserver.server.CatServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Loader;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

public class ModFixUtils {
    public static void doBlockCollisions() { }

    /**
     * Logic: NetherAPI transformer locates constant 7 in EntityAIMate#spawnBaby,
     * and replace it with its own Hooks.spawnParticles call, which always returns 0.
     * CraftBukkit inserted another constant 7 in Random instance for calculating experience drops,
     * NetherAPI accidentally replaced it, leading to IllegalArgumentException.
     *
     * @see <a href="https://github.com/jbredwards/Nether-API/blob/1.12.2/src/main/java/git/jbredwards/nether_api/mod/asm/transformers/vanilla/Transformer_MC_10369.java#L230-L264">NetherAPI</a>
     * @see net.minecraft.entity.ai.EntityAIMate#spawnBaby
     */
    public static int fixNetherAPI() {
        return 7;
    }

    public static void fixNetherex() {
        if (Loader.instance().getIndexedModList().containsKey("netherex")) {
            World netherWorld = DimensionManager.getWorld(-1);
            if (netherWorld != null) {
                try {
                    netherWorld.getServer().unloadWorld(netherWorld.getWorld(), true);
                    if (!CatServer.getConfig().autoUnloadDimensions.contains(-1)) DimensionManager.initDimension(-1);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings({"unused", "rawtypes"})
    public static void hookFirstAidHealthUpdate(EntityPlayer player, DataParameter key, Object value) {
        if (key.equals(EntityPlayer.HEALTH)) {
            float health = (float)value;
            if (player instanceof EntityPlayerMP) {
                final CraftPlayer cbPlayer = ((EntityPlayerMP)player).getBukkitEntity();
                if (health < 0.0f) {
                    cbPlayer.setRealHealth(0.0);
                }
                else if (health > cbPlayer.getMaxHealth()) {
                    cbPlayer.setRealHealth(cbPlayer.getMaxHealth());
                }
                else {
                    cbPlayer.setRealHealth(health);
                }
            }
        }
    }
}