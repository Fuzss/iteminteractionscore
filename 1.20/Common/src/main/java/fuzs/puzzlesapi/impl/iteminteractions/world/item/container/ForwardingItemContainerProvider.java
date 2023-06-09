package fuzs.puzzlesapi.impl.iteminteractions.world.item.container;

import com.google.gson.JsonObject;
import fuzs.puzzlesapi.api.iteminteractions.v1.provider.ItemContainerProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record ForwardingItemContainerProvider(ItemContainerProvider provider) implements ItemContainerProvider {
    
    @Override
    public SimpleContainer getItemContainer(ItemStack containerStack, Player player, boolean allowSaving) {
        SimpleContainer container = this.provider.getItemContainer(containerStack, player, allowSaving);
        Objects.requireNonNull(container, "container is null");
        return container;
    }

    @Override
    public boolean hasItemContainerData(ItemStack containerStack) {
        return this.provider.hasItemContainerData(containerStack);
    }

    @Override
    public @Nullable CompoundTag getItemContainerData(ItemStack containerStack) {
        return this.provider.getItemContainerData(containerStack);
    }

    @Override
    public void setItemContainerData(ItemStack containerStack, ListTag itemsTag, String nbtKey) {
        this.provider.setItemContainerData(containerStack, itemsTag, nbtKey);
    }

    @Override
    public boolean canAddItem(ItemStack containerStack, ItemStack stackToAdd, Player player) {
        if (this.canAcceptItem(containerStack, stackToAdd, player)) {
            return this.provider.canAddItem(containerStack, stackToAdd, player);
        }
        return false;
    }

    @Override
    public boolean allowsPlayerInteractions(ItemStack containerStack, Player player) {
        return this.provider.allowsPlayerInteractions(containerStack, player);
    }

    @Override
    public void broadcastContainerChanges(Player player) {
        this.provider.broadcastContainerChanges(player);
    }

    @Override
    public boolean isItemAllowedInContainer(ItemStack containerStack, ItemStack stackToAdd) {
        return this.provider.isItemAllowedInContainer(containerStack, stackToAdd);
    }

    @Override
    public boolean hasAnyOf(ItemStack containerStack, ItemStack stackToAdd, Player player) {
        if (this.canAcceptItem(containerStack, stackToAdd, player)) {
            return this.provider.hasAnyOf(containerStack, stackToAdd, player);
        }
        return false;
    }

    @Override
    public int getAcceptableItemCount(ItemStack containerStack, ItemStack stackToAdd, Player player) {
        if (this.canAcceptItem(containerStack, stackToAdd, player)) {
            return this.provider.getAcceptableItemCount(containerStack, stackToAdd, player);
        }
        return 0;
    }

    private boolean canAcceptItem(ItemStack containerStack, ItemStack stackToAdd, Player player) {
        return !stackToAdd.isEmpty() && this.allowsPlayerInteractions(containerStack, player) && this.isItemAllowedInContainer(containerStack, stackToAdd);
    }

    @Override
    public boolean canProvideTooltipImage(ItemStack containerStack, Player player) {
        return this.provider.canProvideTooltipImage(containerStack, player);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack containerStack, Player player) {
        Optional<TooltipComponent> tooltipImage = this.provider.getTooltipImage(containerStack, player);
        Objects.requireNonNull(tooltipImage, "tooltip image is null");
        return tooltipImage;
    }

    @Override
    public void toJson(JsonObject jsonObject) {
        this.provider.toJson(jsonObject);
    }
}
