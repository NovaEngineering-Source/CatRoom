package catserver.server.utils;

import catserver.server.CatServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Loader;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

public class ModFixUtils {
    public static void doBlockCollisions() { }

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

    @SuppressWarnings("unused") // Used by ModCompatibleTransformer
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

    public static void fixCBRespawnLogic(EntityPlayerMP playerIn) {
        try {
            playerIn.getDataManager().lock.writeLock().lock();
            playerIn.getDataManager().entries.clear();
        } finally {
            playerIn.getDataManager().lock.writeLock().unlock();
        }
        playerIn.getDataManager().empty = true;
        playerIn.getDataManager().setClean();
        // [VanillaCopy] Entity data params from Entity#<init>
        playerIn.getDataManager().register(Entity.FLAGS, Byte.valueOf((byte)0));
        playerIn.getDataManager().register(Entity.AIR, Integer.valueOf(300));
        playerIn.getDataManager().register(Entity.CUSTOM_NAME_VISIBLE, Boolean.valueOf(false));
        playerIn.getDataManager().register(Entity.CUSTOM_NAME, "");
        playerIn.getDataManager().register(Entity.SILENT, Boolean.valueOf(false));
        playerIn.getDataManager().register(Entity.NO_GRAVITY, Boolean.valueOf(false));

        playerIn.entityInit();

        MinecraftForge.EVENT_BUS.post(new EntityEvent.EntityConstructing(playerIn));
        ((Entity) playerIn).capabilities = ForgeEventFactory.gatherCapabilities(playerIn);
    }
}