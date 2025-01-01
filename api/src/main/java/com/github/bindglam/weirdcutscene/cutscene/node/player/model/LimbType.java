package com.github.bindglam.weirdcutscene.cutscene.node.player.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.Locale;

@Getter
@AllArgsConstructor
public enum LimbType {
    ROOT(-1, -1, new Vector(), new Vector3f(), EulerAngle.ZERO),

    HEAD(1, 1, new Vector(0f, 0.703125f, 0f), new Vector3f(-0.35F, 0F, -0.35F), EulerAngle.ZERO),

    RIGHT_ARM(2, 7, new Vector(-0.3125f, 0.671875f, 0f), new Vector3f(-0.35F, -1024F, -0.35F), EulerAngle.ZERO),
    LEFT_ARM(3, 8, new Vector(0.3125f, 0.671875f, 0f), new Vector3f(-0.35F, -2048F, -0.35F), EulerAngle.ZERO),

    TORSO(4, 4, new Vector(0f, 0.703125f, 0f), new Vector3f(-0.35F, -5119.9F, -0.35F), EulerAngle.ZERO),

    RIGHT_LEG(5, 5, new Vector(-0.1171875f, 0.703125f, 0f), new Vector3f(-0.35F, -6144F, -0.35F), EulerAngle.ZERO),
    LEFT_LEG(6, 6, new Vector(0.1171875f, 0.703125f, 0f), new Vector3f(-0.35F, -7168F, -0.35F), EulerAngle.ZERO),

    RIGHT_ITEM(-1, -1, new Vector(-0.375f, 0.8125f, 0.125f), new Vector3f(-0.26F, 0F, -0.25F), EulerAngle.ZERO),
    LEFT_ITEM(-1, -1, new Vector(0.375f, 0.8125f, 0.125f), new Vector3f(-0.26F, 0F, -0.25F), EulerAngle.ZERO);

    public static final Vector base = new Vector(0.313, -1.85204000149011612, 0);

    private final int modelId;
    private final int slimId;
    private final Vector origin;
    private final Vector3f initialTranslation;
    private final EulerAngle rotation;

    public static LimbType get(String limb) {
        try {
            return valueOf(limb.toUpperCase(Locale.ENGLISH));
        }catch (IllegalArgumentException ignored) {}

        return null;
    }
}