package me.soymako.mixin;


import me.soymako.D2m;
import me.soymako.attachments.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

//import net.minecraft.entity.projectile.ProjectileEntity;
//import net.minecraft.util.hit.BlockHitResult;
//import net.minecraft.util.hit.EntityHitResult;
//import net.minecraft.util.hit.HitResult;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractArrow.class)
public abstract class ProjectileHitMixin {

    @Shadow
    private double baseDamage;

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/Projectile;tick()V")
    )
    private void onTick(CallbackInfo ci){
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        Float bounce = arrow.getAttached(ModAttachments.BOUNCE_CHANCE);
        if (!(arrow.level() instanceof ServerLevel level)){
            return;
        }
        if (bounce == null) return;
        level.sendParticles(
            ParticleTypes.HAPPY_VILLAGER,
            arrow.getX(),
            arrow.getY(),
            arrow.getZ(),
            2, .1f, .1f, .1f,
            .01f
        );
    }

    @Inject(
        method = "onHitEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;doPostHurtEffects(Lnet/minecraft/world/entity/LivingEntity;)V",
            shift = At.Shift.AFTER
        )
    )
    private void onHitEntity(EntityHitResult hitResult, CallbackInfo info){
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        Level level = arrow.level();
        Float attachedChance = arrow.getAttached(ModAttachments.BOUNCE_CHANCE);
        if (attachedChance == null) return;
        float chance = attachedChance;
        if (level.isClientSide()){
            return;
        }
        Entity hitEntity = hitResult.getEntity();
        if (hitEntity instanceof LivingEntity e){


            if (level.getRandom().nextFloat() < chance){
                double rad = 25f;
                Vec3 center = e.position();
                AABB sb = new AABB(center, center).inflate(rad);

                List<LivingEntity> near = level.getEntitiesOfClass(
                    LivingEntity.class,
                    sb,
                    entity -> entity.isAlive()
                        && entity != hitEntity
                        && entity != arrow.getOwner()
                        && entity.distanceToSqr(center) <= rad * rad
                );
                LivingEntity target = near.stream()
                    .min(java.util.Comparator.comparingDouble(
                        entity -> entity.distanceToSqr(center)
                    ))
                    .filter(entity -> !entity.isInvulnerable())
                    .orElse(null);
//                D2m.LOGGER.info(
//                    "Rebote: víctima={}, candidatos={}, objetivo={}",
//                    e.getName().getString(),
//                    near.size(),
//                    target == null ? "none" : target.getName().getString()
//                );
                if (target == null)
                    return;
                Vec3 dir = target.getBoundingBox().getCenter()
                        .subtract(arrow.position())
                            .normalize();

                Vec3 origin = e.getBoundingBox().getCenter();
                Vec3 destination = target.getBoundingBox().getCenter();
                Vec3 difference = destination.subtract(origin);

                if (difference.lengthSqr() < 0.0001) {
                    return;
                }

                Vec3 direction = difference.normalize();

// Salir completamente de la caja de la víctima.
                AABB box = e.getBoundingBox();
                double clearance = new Vec3(
                    box.maxX - box.minX,
                    box.maxY - box.minY,
                    box.maxZ - box.minZ
                ).length() / 2.0 + 0.5;

                Vec3 start = origin.add(direction.scale(clearance));

                Arrow next = new Arrow(
                    level,
                    start.x,
                    start.y,
                    start.z,
                    arrow.getPickupItemStackOrigin().copy(),
                    arrow.getWeaponItem()
                );

                next.setAttached(ModAttachments.BOUNCE_CHANCE, attachedChance);

                double speedMultiplier = arrow.getKnownSpeed().length();

                next.setOwner(arrow.getOwner());
                next.setRemainingFireTicks(arrow.getRemainingFireTicks());
                next.setCritArrow(arrow.isCritArrow());
                next.setBaseDamage(this.baseDamage);
                next.pickup = AbstractArrow.Pickup.DISALLOWED;

                Vec3 aim = destination.subtract(start);
                next.shoot(aim.x, aim.y, aim.z, 2.5F, 0.0F);

                boolean spawned = level.addFreshEntity(next);
//                D2m.LOGGER.info("Flecha de rebote creada: {}", spawned);

                arrow.setDeltaMovement(dir.multiply(new Vec3(speedMultiplier, speedMultiplier, speedMultiplier)));

            }

//            e.level().explode(
//                projectile,
//                e.getX(),
//                e.getY(),
//                e.getZ(),
//                4F,
//                false,
//                Level.ExplosionInteraction.TNT
//            );
        }
    }



}
