package me.soymako.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.soymako.D2m;
import me.soymako.attachments.ModAttachments;
import me.soymako.enchants.ModEnchantments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ProjectileWeaponItem.class)
public class ProjectileWeaponItemMixin {



    @ModifyExpressionValue(
        method = "shoot",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ProjectileWeaponItem;createProjectile(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/projectile/Projectile;"
        )
    )
    private Projectile d2m$onProjectileCreated(
        Projectile projectile,
        ServerLevel level,
        LivingEntity shooter,
        InteractionHand hand,
        ItemStack weapon,
        List<ItemStack> ammunition,
        float speed,
        float inaccuracy,
        boolean critical,
        LivingEntity target
    )
    {
        if (projectile instanceof AbstractArrow arrow){
            D2m.LOGGER.info(
                "Flecha creada por {} usando {}",
                shooter.getName().getString(),
                weapon.getItem()
            );
            var bounce = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(ModEnchantments.BOUNCE);

            ItemEnchantments enchants = weapon.getEnchantments();
            int bounceLevel = enchants.getLevel(bounce);
            float odds = 1f;
            if (bounceLevel > 0){
                projectile.setAttached(ModAttachments.BOUNCE_CHANCE, odds);
                D2m.LOGGER.info(
                    "bounce level: {}",
                    bounceLevel
                );
            }
        }
        return projectile;
    }



}
