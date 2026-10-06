package me.soymako.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

import me.soymako.D2m;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.*;
public class ModItems{

  private static final Item TEST_ITEM =
    register("test_item", Item::new, new Item.Properties());

  private static final Item MESSAGE_ITEM =
    register("message_item", MessageItem::new, new Item.Properties());


  public static void initialize(){
    D2m.LOGGER.info("ModItems initialized!");
  }


  public static Item register(
      String name,
      Function<Item.Properties, Item> factory,
      Item.Properties properties)
  {
    ResourceKey<Item> key = ResourceKey.create(
        Registries.ITEM,
        D2m.id(name)
    );
    Item item = factory.apply(properties.setId(key));
    return Registry.register(BuiltInRegistries.ITEM, key, item);
  }


}
