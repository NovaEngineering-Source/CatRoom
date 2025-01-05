package catserver.api.bukkit.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * A Bukkit side wrapper for Forge events.
 * This wrapper event is <b>optionally</b> cancellable.
 */
@SuppressWarnings("unused")
public class ForgeEvent extends org.bukkit.event.Event {
    private static final HandlerList handlers = new HandlerList();
    private final Event forgeEvent;

    public ForgeEvent(Event forgeEvent) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.forgeEvent = forgeEvent;
    }

    @Nonnull
    public Event getForgeEvent() {
        return this.forgeEvent;
    }

    /**
     * Try to set the cancelled state of the wrapped Forge event.
     * @param cancelled The cancelled state to set.
     * @return Whether the cancelled state was successfully set.
     */
    public boolean trySetCancelled(boolean cancelled) {
        if (this.forgeEvent.isCancelable()) {
            this.forgeEvent.setCanceled(cancelled);
            return true;
        }
        return false;
    }

    /**
     * Get the cancelled state of the wrapped Forge event.
     * @return Whether the wrapped Forge event is cancelled.
     * If the wrapped Forge event is not cancellable, this will always return false.
     */
    public boolean isCancelled() {
        return this.forgeEvent.isCanceled();
    }

    /**
     * Get whether the wrapped Forge event is cancellable.
     * @return Whether the wrapped Forge event is cancellable.
     */
    public boolean isCancellable() {
        return this.forgeEvent.isCancelable();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
