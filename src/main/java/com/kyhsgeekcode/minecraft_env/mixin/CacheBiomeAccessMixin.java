package com.kyhsgeekcode.minecraft_env.mixin;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kyhsgeekcode.minecraft_env.Point3D;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(net.minecraft.world.biome.source.BiomeAccess.class)
public class CacheBiomeAccessMixin {
    // Thread safe LRU cache
    @Unique
    private final Cache<Point3D, Point3D> coordsCache = Caffeine.newBuilder()
            .maximumSize(4096)  // Max cache size
            .build();

    @Final
    @Shadow
    private BiomeAccess.Storage storage;

    @Inject(
            method = "getBiome",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void getBiomeHead(BlockPos pos, CallbackInfoReturnable<RegistryEntry<Biome>> cir) {
        // BlockPos is mutable
        var blockPos = new Point3D(pos.getX(), pos.getY(), pos.getZ());
        var pwx = coordsCache.getIfPresent(blockPos);
        if (pwx != null) {
            cir.setReturnValue(storage.getBiomeForNoiseGen(pwx.x(), pwx.y(), pwx.z()));
            cir.cancel();
        }
    }

    @Inject(
            method = "getBiome",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/source/BiomeAccess$Storage;getBiomeForNoiseGen(III)Lnet/minecraft/registry/entry/RegistryEntry;"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void getBiome(
            BlockPos pos,
            CallbackInfoReturnable<RegistryEntry<Biome>> cir,
            int i,
            int j,
            int k,
            int l,
            int m,
            int n,
            double d,
            double e,
            double f,
            int o,
            double g,
            int p,
            int w,
            int x
    ) {
        coordsCache.put(new Point3D(pos.getX(), pos.getY(), pos.getZ()), new Point3D(p, w, x));
    }
}
