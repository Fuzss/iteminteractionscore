package fuzs.puzzlesapi.impl.iteminteractions.client.core;

import net.minecraft.client.KeyMapping;

public interface KeyMappingProvider {

    boolean keyPressed(int keyCode, int scanCode);

    KeyMapping getKeyMapping();
}
