package com.kyhsgeekcode.minecraft_env.mixin;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.gui.screen.world.MoreOptionsDialog.class)
public interface MoreOptionsDialogSeedTextFieldAccessor {
    @Accessor("seedTextField")
    TextFieldWidget getSeedTextField();

    @Accessor("mapTypeButton")
    ButtonWidget getMapTypeButton();
}
