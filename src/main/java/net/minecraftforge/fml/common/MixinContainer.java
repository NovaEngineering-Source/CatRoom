package net.minecraftforge.fml.common;

import com.google.common.eventbus.EventBus;

public final class MixinContainer extends DummyModContainer{
    private static final String version = System.getProperty("cleanroom.mixinbooter.version", "10.1");
    public MixinContainer() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "mixinbooter";
        meta.name = "MixinBooter";
        meta.description = "A Mixin library and loader.";
        meta.version = version;
        meta.authorList.add("Rongmario");
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}
