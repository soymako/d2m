package me.soymako.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class MessageItem extends Item{

  public MessageItem(Item.Properties properties){
    super(properties);
  }

  @Override
  public InteractionResult use(Level level, Player player, InteractionHand hand){
    if (level.isClientSide()){
      player.sendSystemMessage(
        Component.literal("Hola uwu")
          .withStyle(ChatFormatting.GREEN)
      );
    }
    return InteractionResult.SUCCESS;
  }


}
