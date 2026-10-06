package me.soymako.attachments;

import com.mojang.serialization.Codec;

import me.soymako.D2m;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class ModAttachments{


  public static final AttachmentType<Float> BOUNCE_CHANCE =
    AttachmentRegistry.createPersistent(
      D2m.id("bounce_chance"),
      Codec.FLOAT
    );

  public static void init(){

  }


}
