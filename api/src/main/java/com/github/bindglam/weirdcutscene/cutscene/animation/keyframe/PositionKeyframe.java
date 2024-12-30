package com.github.bindglam.weirdcutscene.cutscene.animation.keyframe;

import org.bukkit.util.Vector;

public class PositionKeyframe extends AbstractKeyframe<Vector> {
    public PositionKeyframe(Vector value) {
        super(value);
    }

    public PositionKeyframe(Vector value, KeyframeType type) {
        super(value, type);
    }
}

