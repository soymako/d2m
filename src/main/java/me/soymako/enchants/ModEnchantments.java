package me.soymako.enchants;

import me.soymako.D2m;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments{
  
  public static final ResourceKey<Enchantment> BOUNCE =
    ResourceKey.create(
        Registries.ENCHANTMENT, D2m.id("bounce"));

  public static void init(){


  }


}
