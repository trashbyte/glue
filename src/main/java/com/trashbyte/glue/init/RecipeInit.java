package com.trashbyte.glue.init;

import com.trashbyte.glue.Glue;
import com.trashbyte.glue.recipes.TransmutatorRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeInit {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Glue.MOD_ID);

    public static final RegistryObject<RecipeSerializer<TransmutatorRecipe>> TRANSMUTATOR_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register("transmutator",
                () -> new TransmutatorRecipe.Serializer<>(TransmutatorRecipe::new));

    public static RecipeType<TransmutatorRecipe> TRANSMUTATOR_RECIPE_TYPE = null;
}
