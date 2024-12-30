package com.github.bindglam.weirdcutscene.cutscene.animation;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Animation {
    private final Map<String, Timeline> timelines = new HashMap<>();

    @Getter @Setter private double length;

    public Animation() {
    }

    public Timeline getOrCreateTimeline(String bone) {
        if(timelines.containsKey(bone))
            return timelines.get(bone);
        Timeline timeline = new Timeline();
        timelines.put(bone, timeline);
        return timeline;
    }

    public @Nullable World getWorld(String bone, double time) {
        if(!timelines.containsKey(bone))
            return null;
        return timelines.get(bone).getWorldFrame(time);
    }

    public Vector getPosition(String bone, double time) {
        if(!timelines.containsKey(bone))
            return new Vector();
        return timelines.get(bone).getPositionFrame(time);
    }

    public EulerAngle getRotation(String bone, double time) {
        if(!timelines.containsKey(bone))
            return EulerAngle.ZERO;
        return timelines.get(bone).getRotationFrame(time);
    }
}
