package me.soymako;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.soymako.item.ModItems;

public class D2m implements ModInitializer {
  public static final String MOD_ID = "d2m";

  public static D2m instance;

  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


  @Override
  public void onInitialize() {
    instance = this;
    ModItems.initialize();
    LOGGER.info("Hello Fabric world!");
  }

  public D2m getInstance(){
    return instance;
  }

  public static Identifier id(String path) {
    return Identifier.fromNamespaceAndPath(MOD_ID, path);
  }
}
