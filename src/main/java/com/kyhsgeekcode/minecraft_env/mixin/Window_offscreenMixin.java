package com.kyhsgeekcode.minecraft_env.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;
import java.util.Optional;

@Mixin(value = Window.class)
public abstract class Window_offscreenMixin implements AutoCloseable {

    @Mutable
    @Shadow
    @Final
    private WindowEventHandler eventHandler;
    @Mutable
    @Shadow
    @Final
    private MonitorTracker monitorTracker;
    @Mutable
    @Shadow
    @Final
    private long handle;

    @Shadow
    private int windowedX;

    @Shadow
    private int windowedY;

    @Shadow
    private int windowedWidth;
    @Shadow
    private int windowedHeight;
    @Shadow
    private Optional<VideoMode> videoMode;
    @Shadow
    private boolean fullscreen;
    @Shadow
    private boolean currentFullscreen;
    @Shadow
    private int x;
    @Shadow
    private int y;
    @Shadow
    private int width;
    @Shadow
    private int height;

    @Shadow
    public abstract void setPhase(String phase);

    @Shadow
    protected abstract void updateWindowRegion();

    @Shadow
    protected abstract void updateFramebufferSize();

    @Shadow
    protected abstract void onFramebufferSizeChanged(long window, int width, int height);

    @Shadow
    protected abstract void onWindowFocusChanged(long window, boolean focused);

    @Shadow
    protected abstract void onWindowPosChanged(long window, int x, int y);

    @Shadow
    protected abstract void onWindowSizeChanged(long window, int width, int height);

    @Shadow
    protected abstract void onCursorEnterChanged(long window, boolean entered);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init_window(WindowEventHandler eventHandler, MonitorTracker monitorTracker, WindowSettings settings, String videoMode, String title, CallbackInfo ci) {
        RenderSystem.assertInInitPhase();
        this.monitorTracker = monitorTracker;
        this.setPhase("Pre startup");
        this.eventHandler = eventHandler;
        Optional<VideoMode> optional = VideoMode.fromString(videoMode);
        this.videoMode = optional.isPresent() ? optional : (settings.fullscreenWidth.isPresent() && settings.fullscreenHeight.isPresent() ? Optional.of(new VideoMode(settings.fullscreenWidth.getAsInt(), settings.fullscreenHeight.getAsInt(), 8, 8, 8, 60)) : Optional.empty());
        this.currentFullscreen = this.fullscreen = settings.fullscreen;
        Monitor monitor = monitorTracker.getMonitor(GLFW.glfwGetPrimaryMonitor());
        this.width = settings.width > 0 ? settings.width : 1;
        this.windowedWidth = this.width;
        this.height = settings.height > 0 ? settings.height : 1;
        this.windowedHeight = this.height;
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_CREATION_API, GLFW.GLFW_NATIVE_CONTEXT_API);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, 1);
        this.handle = GLFW.glfwCreateWindow(this.width, this.height, title, this.fullscreen && monitor != null ? monitor.getHandle() : 0L, 0L);
        if (monitor != null) {
            VideoMode videoMode2 = monitor.findClosestVideoMode(this.fullscreen ? this.videoMode : Optional.empty());
            this.windowedX = this.x = monitor.getViewportX() + videoMode2.getWidth() / 2 - this.width / 2;
            this.windowedY = this.y = monitor.getViewportY() + videoMode2.getHeight() / 2 - this.height / 2;
        } else {
            int[] is = new int[1];
            int[] js = new int[1];
            GLFW.glfwGetWindowPos(this.handle, is, js);
            this.windowedX = this.x = is[0];
            this.windowedY = this.y = js[0];
        }
        GLFW.glfwMakeContextCurrent(this.handle);
        Locale locale = Locale.getDefault(Locale.Category.FORMAT);
        Locale.setDefault(Locale.Category.FORMAT, Locale.ROOT);
        GL.createCapabilities();
        Locale.setDefault(Locale.Category.FORMAT, locale);
        this.updateWindowRegion();
        this.updateFramebufferSize();
        GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeChanged);
        GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPosChanged);
        GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChanged);
        GLFW.glfwSetWindowFocusCallback(this.handle, this::onWindowFocusChanged);
        GLFW.glfwSetCursorEnterCallback(this.handle, this::onCursorEnterChanged);
    }
}
