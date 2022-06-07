package com.trashbyte.glue.recipes;

import com.google.gson.JsonObject;
import com.trashbyte.glue.init.RecipeInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;


public class TransmutatorRecipe extends SingleItemRecipe {
    public TransmutatorRecipe(ResourceLocation resLoc, String s, Ingredient ingredient, ItemStack stack) {
        super(RecipeInit.TRANSMUTATOR_RECIPE_TYPE, RecipeInit.TRANSMUTATOR_RECIPE_SERIALIZER.get(), resLoc, s, ingredient, stack);
    }

    public boolean matches(Container c, Level level) {
        return this.ingredient.test(c.getItem(0));
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.STONECUTTER);
    }


    public static class Serializer<T extends SingleItemRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
        final SingleItemMaker<T> factory;

        public Serializer(SingleItemMaker<T> p_44435_) {
            this.factory = p_44435_;
        }

        public T fromJson(ResourceLocation p_44449_, JsonObject p_44450_) {
            String s = GsonHelper.getAsString(p_44450_, "group", "");
            Ingredient ingredient;
            if (GsonHelper.isArrayNode(p_44450_, "ingredient")) {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(p_44450_, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_44450_, "ingredient"));
            }

            String s1 = GsonHelper.getAsString(p_44450_, "result");
            int i = GsonHelper.getAsInt(p_44450_, "count");
            ItemStack itemstack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s1)), i);
            return this.factory.create(p_44449_, s, ingredient, itemstack);
        }

        public T fromNetwork(ResourceLocation resLoc, FriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack itemstack = buffer.readItem();
            return this.factory.create(resLoc, s, ingredient, itemstack);
        }

        public void toNetwork(FriendlyByteBuf buffer, T t) {
            buffer.writeUtf(t.getGroup());
            t.getIngredients().get(0).toNetwork(buffer);
            buffer.writeItem(t.getResultItem());
        }

        public interface SingleItemMaker<T extends SingleItemRecipe> {
            T create(ResourceLocation loc, String s, Ingredient ing, ItemStack stack);
        }
    }
}