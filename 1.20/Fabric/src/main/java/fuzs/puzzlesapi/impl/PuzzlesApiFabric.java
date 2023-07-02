package fuzs.puzzlesapi.impl;

import fuzs.puzzlesapi.impl.iteminteractions.ItemInteractions;
import fuzs.puzzlesapi.impl.slotcycling.SlotCycling;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class PuzzlesApiFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(PuzzlesApi.MOD_ID, ItemInteractions::new);
        ModConstructor.construct(PuzzlesApi.MOD_ID, SlotCycling::new);
    }
}
