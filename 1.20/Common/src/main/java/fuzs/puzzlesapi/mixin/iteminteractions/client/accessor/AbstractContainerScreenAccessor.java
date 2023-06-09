package fuzs.puzzlesapi.mixin.iteminteractions.client.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

    @Invoker("findSlot")
    Slot easyshulkerboxes$callFindSlot(double mouseX, double mouseY);

    @Invoker("slotClicked")
    void easyshulkerboxes$callSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type);

    @Accessor("doubleclick")
    void easyshulkerboxes$setDoubleclick(boolean doubleclick);
}
