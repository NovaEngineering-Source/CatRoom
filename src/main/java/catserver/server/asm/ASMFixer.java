package catserver.server.asm;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

public class ASMFixer {
    public interface IEntityASMFixer {
        CraftEntity getBukkitEntity();

        default void setFire(int seconds) {
            ((Entity) this).setFire(seconds);
        }
    }

    public interface IEntityPlayerASMFixer {
        CraftHumanEntity getBukkitEntity();
    }

    public interface IEntityPlayerMPASMFixer {
        CraftPlayer getBukkitEntity();

        default NetHandlerPlayServer getConnection() {
            return ((EntityPlayerMP) this).connection;
        }
    }
}
