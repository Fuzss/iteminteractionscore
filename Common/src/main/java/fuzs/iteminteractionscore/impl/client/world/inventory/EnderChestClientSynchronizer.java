package fuzs.iteminteractionscore.impl.client.world.inventory;

import fuzs.iteminteractionscore.impl.ItemInteractionsCore;
import fuzs.iteminteractionscore.impl.network.client.C2SEnderChestSetSlotMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;

public class EnderChestClientSynchronizer implements ContainerSynchronizer {

    @Override
    public void sendInitialData(AbstractContainerMenu abstractContainerMenu, NonNullList<ItemStack> nonNullList, ItemStack itemStack, int[] is) {

    }

    @Override
    public void sendSlotChange(AbstractContainerMenu abstractContainerMenu, int i, ItemStack itemStack) {
        // this is only required for the creative mode inventory, as it doesn't sync contents using default menu packets,
        // instead it uses custom packets which do not work for item interactions in a menu
        if (Minecraft.getInstance().gameMode.hasInfiniteItems() && Minecraft.getInstance().player.containerMenu instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            ItemInteractionsCore.NETWORK.sendToServer(new C2SEnderChestSetSlotMessage(abstractContainerMenu.incrementStateId(), i, itemStack));
        }
    }

    @Override
    public void sendCarriedChange(AbstractContainerMenu abstractContainerMenu, ItemStack itemStack) {

    }

    @Override
    public void sendDataChange(AbstractContainerMenu abstractContainerMenu, int i, int j) {

    }
}
