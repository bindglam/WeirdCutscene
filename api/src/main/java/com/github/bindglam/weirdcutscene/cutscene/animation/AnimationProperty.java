package com.github.bindglam.weirdcutscene.cutscene.animation;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

@Getter
public class AnimationProperty {
    private final Animation animation;
    private double time = 0;
    @Setter private double speed;

    public AnimationProperty(Animation animation) {
        this(animation, 1);
    }

    public AnimationProperty(Animation animation, double speed) {
        this.animation = animation;
        this.speed = speed;
    }

    public boolean updateTime() {
        if(time < animation.getLength()) {
            time = Math.min(time + (speed / 20), animation.getLength());
            return true;
        }
        return false;
    }

    public @Nullable World getWorldFrame(String bone) {
        return animation.getWorld(bone, time);
    }

    public Vector getPositionFrame(String bone) {
        return animation.getPosition(bone, time);
    }

    public EulerAngle getRotationFrame(String bone) {
        return animation.getRotation(bone, time);
    }
}
