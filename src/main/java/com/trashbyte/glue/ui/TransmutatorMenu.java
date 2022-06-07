package com.trashbyte.glue.ui;

import com.google.common.collect.Lists;
import java.util.List;

import com.trashbyte.glue.init.BlockInit;
import com.trashbyte.glue.init.RecipeInit;
import com.trashbyte.glue.init.UiInit;
import com.trashbyte.glue.recipes.TransmutatorRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class TransmutatorMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int USE_ROW_SLOT_START = 29;
    private static final int USE_ROW_SLOT_END = 38;
    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private List<TransmutatorRecipe> recipes = Lists.newArrayList();
    private ItemStack input = ItemStack.EMPTY;
    final Slot inputSlot;
    final Slot resultSlot;
    Runnable slotUpdateListener = () -> {};

    public final Container container = new SimpleContainer(1) {
        public void setChanged() {
            super.setChanged();
            TransmutatorMenu.this.slotsChanged(this);
            TransmutatorMenu.this.slotUpdateListener.run();
        }
    };
    final ResultContainer resultContainer = new ResultContainer();

    public TransmutatorMenu(int capacity, Inventory inv) {
        this(capacity, inv, ContainerLevelAccess.NULL);
    }

    public TransmutatorMenu(int capacity, Inventory inv, final ContainerLevelAccess access) {
        super(UiInit.TRANSMUTATOR_MENU.get(), capacity);
        this.access = access;
        this.level = inv.player.level;
        this.inputSlot = this.addSlot(new Slot(this.container, INPUT_SLOT, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, RESULT_SLOT, 143, 33) {
            public boolean mayPlace(ItemStack p_40362_) {
                return false;
            }

            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player.level, player, stack.getCount());
                TransmutatorMenu.this.resultContainer.awardUsedRecipes(player);
                ItemStack itemstack = TransmutatorMenu.this.inputSlot.remove(1);
                if (!itemstack.isEmpty()) {
                    TransmutatorMenu.this.setupResultSlot();
                }

                super.onTake(player, stack);
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
        }

        this.addDataSlot(this.selectedRecipeIndex);
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<TransmutatorRecipe> getRecipes() {
        return this.recipes;
    }

    public int getNumRecipes() {
        return this.recipes.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, BlockInit.TRANSMUTATOR_BLOCK.get());
    }

    public boolean clickMenuButton(Player player, int idx) {
        if (this.isValidRecipeIndex(idx)) {
            this.selectedRecipeIndex.set(idx);
            this.setupResultSlot();
        }

        return true;
    }

    private boolean isValidRecipeIndex(int idx) {
        return idx >= 0 && idx < this.recipes.size();
    }

    public void slotsChanged(Container container) {
        ItemStack itemstack = this.inputSlot.getItem();
        if (!itemstack.is(this.input.getItem())) {
            this.input = itemstack.copy();
            this.setupRecipeList(container, itemstack);
        }

    }

    private void setupRecipeList(Container container, ItemStack stack) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(RecipeInit.TRANSMUTATOR_RECIPE_TYPE, container, this.level);
        }
    }

    void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            TransmutatorRecipe r = this.recipes.get(this.selectedRecipeIndex.get());
            this.resultContainer.setRecipeUsed(r);
            this.resultSlot.set(r.assemble(this.container));
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    public MenuType<?> getType() {
        return UiInit.TRANSMUTATOR_MENU.get();
    }

    public void registerUpdateListener(Runnable p_40324_) {
        this.slotUpdateListener = p_40324_;
    }

    public boolean canTakeItemForPickAll(ItemStack p_40321_, Slot p_40322_) {
        return p_40322_.container != this.resultContainer && super.canTakeItemForPickAll(p_40321_, p_40322_);
    }

    public ItemStack quickMoveStack(Player p_40328_, int p_40329_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_40329_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (p_40329_ == 1) {
                item.onCraftedBy(itemstack1, p_40328_.level, p_40328_);
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (p_40329_ == 0) {
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.level.getRecipeManager().getRecipeFor(RecipeType.STONECUTTING, new SimpleContainer(itemstack1), this.level).isPresent()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_40329_ >= 2 && p_40329_ < 29) {
                if (!this.moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_40329_ >= USE_ROW_SLOT_START && p_40329_ < USE_ROW_SLOT_END && !this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_START, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(p_40328_, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((p_40313_, p_40314_) -> {
            this.clearContainer(player, this.container);
        });
    }
}