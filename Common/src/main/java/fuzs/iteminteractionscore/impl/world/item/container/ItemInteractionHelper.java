package fuzs.iteminteractionscore.impl.world.item.container;

import fuzs.iteminteractionscore.impl.world.inventory.ContainerSlotHelper;
import fuzs.iteminteractionscore.impl.world.inventory.ItemMoveHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class ItemInteractionHelper {
    public static final String TAG_ITEMS = "Items";

    public static boolean overrideStackedOnOther(Supplier<SimpleContainer> containerSupplier, Slot slot, ClickAction clickAction, Player player, ToIntFunction<ItemStack> acceptableItemCount) {
        ItemStack stackBelowMe = slot.getItem();
        boolean extractSingleItemOnly = ContainerSlotHelper.extractSingleItemOnly(player);
        if (clickAction == ClickAction.SECONDARY && (stackBelowMe.isEmpty() || extractSingleItemOnly)) {
            BiConsumer<ItemStack, Integer> addToSlot = (stackToAdd, index) -> {
                addStack(containerSupplier, player, slot.safeInsert(stackToAdd), acceptableItemCount, index);
            };
            handleRemoveItem(containerSupplier, stackBelowMe, player, extractSingleItemOnly, addToSlot);
            return true;
        } else if (clickAction == ClickAction.SECONDARY || extractSingleItemOnly) {
            ItemStack stackInSlot = slot.safeTake(stackBelowMe.getCount(), stackBelowMe.getCount(), player);
            handleAddItem(containerSupplier, clickAction, player, acceptableItemCount, stackInSlot);
            slot.safeInsert(stackInSlot);
            return true;
        }
        return false;
    }

    public static boolean overrideOtherStackedOnMe(Supplier<SimpleContainer> containerSupplier, ItemStack stackOnMe, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess, ToIntFunction<ItemStack> acceptableItemCount) {
        if (!slot.allowModification(player)) return false;
        boolean extractSingleItemOnly = ContainerSlotHelper.extractSingleItemOnly(player);
        if (clickAction == ClickAction.SECONDARY && (stackOnMe.isEmpty() || extractSingleItemOnly)) {
            BiConsumer<ItemStack, Integer> addToSlot = (stackToAdd, index) -> {
                ItemStack stackInSlot = slotAccess.get();
                if (stackInSlot.isEmpty()) {
                    slotAccess.set(stackToAdd);
                } else {
                    stackInSlot.grow(stackToAdd.getCount());
                    slotAccess.set(stackInSlot);
                }
            };
            handleRemoveItem(containerSupplier, stackOnMe, player, extractSingleItemOnly, addToSlot);
            return true;
        } else if (clickAction == ClickAction.SECONDARY || extractSingleItemOnly) {
            handleAddItem(containerSupplier, clickAction, player, acceptableItemCount, stackOnMe);
            return true;
        }
        return false;
    }

    private static void handleRemoveItem(Supplier<SimpleContainer> containerSupplier, ItemStack stackOnMe, Player player, boolean extractSingleItemOnly, BiConsumer<ItemStack, Integer> addToSlot) {
        ToIntFunction<ItemStack> amountToRemove = stack -> extractSingleItemOnly ? 1 : stack.getCount();
        Predicate<ItemStack> itemFilter = stackInSlot -> {
            return stackOnMe.isEmpty() || (ItemStack.isSameItemSameTags(stackOnMe, stackInSlot) && stackOnMe.getCount() < stackOnMe.getMaxStackSize());
        };
        Pair<ItemStack, Integer> result = removeLastStack(containerSupplier, player, itemFilter, amountToRemove);
        ItemStack stackToAdd = result.getLeft();
        if (!stackToAdd.isEmpty()) {
            addToSlot.accept(stackToAdd, result.getRight());
            player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.4F);
        }
    }

    private static void handleAddItem(Supplier<SimpleContainer> containerSupplier, ClickAction clickAction, Player player, ToIntFunction<ItemStack> acceptableItemCount, ItemStack stackInSlot) {
        int transferredCount;
        if (clickAction == ClickAction.PRIMARY) {
            transferredCount = addStack(containerSupplier, player, stackInSlot, stack -> Math.min(1, acceptableItemCount.applyAsInt(stack)));
        } else {
            transferredCount = addStack(containerSupplier, player, stackInSlot, acceptableItemCount);
        }
        stackInSlot.shrink(transferredCount);
        if (transferredCount > 0) {
            player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.4F);
        }
    }

    private static int addStack(Supplier<SimpleContainer> containerSupplier, Player player, ItemStack newStack, ToIntFunction<ItemStack> acceptableItemCount) {
        return addStack(containerSupplier, player, newStack, acceptableItemCount, ContainerSlotHelper.getCurrentContainerSlot(player));
    }

    private static int addStack(Supplier<SimpleContainer> containerSupplier, Player player, ItemStack newStack, ToIntFunction<ItemStack> acceptableItemCount, int prioritizedSlot) {
        if (newStack.isEmpty()) return 0;
        SimpleContainer container = containerSupplier.get();
        ItemStack stackToAdd = newStack.copy();
        stackToAdd.setCount(Math.min(acceptableItemCount.applyAsInt(newStack), newStack.getCount()));
        if (stackToAdd.isEmpty()) return 0;
        Pair<ItemStack, Integer> result = ItemMoveHelper.addItem(container, stackToAdd, prioritizedSlot);
        ContainerSlotHelper.setCurrentContainerSlot(player, result.getRight());
        return stackToAdd.getCount() - result.getLeft().getCount();
    }

    private static Pair<ItemStack, Integer> removeLastStack(Supplier<SimpleContainer> containerSupplier, Player player, Predicate<ItemStack> itemFilter, ToIntFunction<ItemStack> amountToRemove) {
        SimpleContainer container = containerSupplier.get();
        OptionalInt slotWithContent = findSlotWithContent(container, player, itemFilter, amountToRemove);
        if (slotWithContent.isPresent()) {
            int index = slotWithContent.getAsInt();
            int amount = amountToRemove.applyAsInt(container.getItem(index));
            return Pair.of(container.removeItem(index, amount), index);
        }
        return Pair.of(ItemStack.EMPTY, -1);
    }

    private static OptionalInt findSlotWithContent(SimpleContainer container, Player player, Predicate<ItemStack> itemFilter, ToIntFunction<ItemStack> amountToRemove) {
        int currentContainerSlot = ContainerSlotHelper.getCurrentContainerSlot(player);
        if (currentContainerSlot >= 0 && currentContainerSlot < container.getContainerSize()) {
            ItemStack stackInSlot = container.getItem(currentContainerSlot);
            if (!stackInSlot.isEmpty() && itemFilter.test(stackInSlot)) {
                // did we empty the slot, so cycle to a different one
                if (stackInSlot.getCount() <= amountToRemove.applyAsInt(stackInSlot)) {
                    ContainerSlotHelper.cycleCurrentSlotBackwards(player, container);
                }
                return OptionalInt.of(currentContainerSlot);
            }
        }
        for (int i = container.getContainerSize() - 1; i >= 0; i--) {
            ItemStack stackInSlot = container.getItem(i);
            if (!stackInSlot.isEmpty() && itemFilter.test(stackInSlot)) {
                // did we empty the slot, so cycle to a different one
                if (stackInSlot.getCount() <= amountToRemove.applyAsInt(stackInSlot)) {
                    ContainerSlotHelper.resetCurrentContainerSlot(player);
                } else {
                    // otherwise if not empty make sure this is the new current slot
                    ContainerSlotHelper.setCurrentContainerSlot(player, i);
                }
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }
}
