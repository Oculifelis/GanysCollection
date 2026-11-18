package dev.oculifelis.ganys.client;

import net.neoforged.bus.api.IEventBus;

public final class GanysClient {
    public static void init(IEventBus modBus) {
        modBus.addListener(ConnectedGlassModel::register);
    }

    private GanysClient() {
    }
}
