package com.github.bindglam.weirdcutscene.cutscene.node;

import com.alibaba.fastjson2.JSONObject;
import com.github.bindglam.weirdcutscene.cutscene.animation.Animation;
import com.github.bindglam.weirdcutscene.cutscene.animation.AnimationProperty;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public abstract class Node {
    @Getter protected AnimationProperty animationProperty;
    @Setter protected Player player;

    protected final JSONObject jsonObject;

    public Node(Animation animation, JSONObject jsonObject){
        this.animationProperty = new AnimationProperty(animation);
        this.jsonObject = jsonObject;
    }

    public abstract void play();
    public abstract boolean update(boolean updateTime);
    public abstract void dispose();
}
