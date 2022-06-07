package com.trashbyte.glue.init;

import com.trashbyte.glue.Glue;
import com.trashbyte.glue.ui.TransmutatorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class UiInit {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Glue.MOD_ID);

    public static final RegistryObject<MenuType<TransmutatorMenu>> TRANSMUTATOR_MENU = MENU_TYPES.register(
            "transmutator_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new TransmutatorMenu(windowId, inv))
    );
}
