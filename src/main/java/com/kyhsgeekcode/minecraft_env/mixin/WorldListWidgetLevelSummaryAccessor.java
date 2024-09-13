package com.kyhsgeekcode.minecraft_env.mixin;

import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.gui.screen.world.WorldListWidget.Entry.class)
public interface WorldListWidgetLevelSummaryAccessor {
    @Accessor("level")
    LevelSummary getLevel();
}
