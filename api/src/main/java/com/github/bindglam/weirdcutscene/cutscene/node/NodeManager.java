package com.github.bindglam.weirdcutscene.cutscene.node;

import com.github.bindglam.weirdcutscene.cutscene.animation.Animation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NodeManager {
    void register(@NotNull Class<? extends Node> nodeClass);

    @Nullable Node createNode(String className, Animation animation);
}
