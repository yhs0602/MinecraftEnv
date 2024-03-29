package com.kyhsgeekcode.minecraft_env.mixin;


public interface ManageFrameBufferInterface {
    List<EntityRenderListener> listeners = new ArrayList<>();

    default void addRenderListener(@NotNull EntityRenderListener listener) {
        listeners.add(listener);
    }

    default List<EntityRenderListener> getRenderListeners() {
        return listeners;
    }
}
