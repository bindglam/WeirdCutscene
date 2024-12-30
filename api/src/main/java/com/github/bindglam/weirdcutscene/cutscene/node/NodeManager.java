package com.github.bindglam.weirdcutscene.cutscene.node;

import com.alibaba.fastjson2.JSONObject;
import com.github.bindglam.weirdcutscene.WeirdCutscene;
import com.github.bindglam.weirdcutscene.cutscene.animation.Animation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public final class NodeManager {
    private final HashMap<String, Class<? extends Node>> nodeClasses = new HashMap<>();

    public void register(Class<? extends Node> nodeClass) {
        nodeClasses.put(nodeClass.getSimpleName(), nodeClass);

        WeirdCutscene.inst().getLogger().info(nodeClass.getSimpleName() + " is registered");
    }

    public @Nullable Node createNode(String className, Animation animation, JSONObject jsonObject) {
        if (!nodeClasses.containsKey(className)) return null;
        Class<? extends Node> clazz = nodeClasses.get(className);
        try {
            return clazz.getDeclaredConstructor(Animation.class, JSONObject.class).newInstance(animation, jsonObject);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }
}
