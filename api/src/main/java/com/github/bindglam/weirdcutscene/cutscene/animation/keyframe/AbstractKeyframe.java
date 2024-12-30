package com.github.bindglam.weirdcutscene.cutscene.animation.keyframe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractKeyframe<T> {
    private T value;
    private KeyframeType type;

    public AbstractKeyframe(T value) {
        this(value, KeyframeType.LINEAR);
    }

    public AbstractKeyframe(T value, KeyframeType type) {
        this.value = value;
        this.type = type;
    }
}
