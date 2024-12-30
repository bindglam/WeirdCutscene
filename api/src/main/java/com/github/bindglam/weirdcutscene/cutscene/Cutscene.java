package com.github.bindglam.weirdcutscene.cutscene;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.bindglam.weirdcutscene.WeirdCutscene;
import com.github.bindglam.weirdcutscene.cutscene.animation.Animation;
import com.github.bindglam.weirdcutscene.cutscene.animation.Timeline;
import com.github.bindglam.weirdcutscene.cutscene.animation.keyframe.KeyframeType;
import com.github.bindglam.weirdcutscene.cutscene.animation.keyframe.WorldKeyframe;
import com.github.bindglam.weirdcutscene.cutscene.node.Node;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Cutscene {
    private final String name;
    private final List<Node> nodes = new ArrayList<>();

    private double length;

    private Player viewer;
    private int taskID;

    public Cutscene(String name) {
        this.name = name;
    }

    public void play(Player viewer){
        this.viewer = viewer;

        for(Node node : nodes){
            node.setPlayer(viewer);
            node.play();

            if(length > node.getAnimationProperty().getAnimation().getLength()){
                length = node.getAnimationProperty().getAnimation().getLength();
            }
        }

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(WeirdCutscene.inst(), () -> {
            for(int i = nodes.size()-1; i >= 0; i--){
                Node node = nodes.get(i);
                if(!node.update(true)){
                    node.dispose();
                    nodes.remove(i);
                }
            }
        }, 0L, 1L);
    }

    // TODO : Optimize
    public static Cutscene loadCutscene(JSONObject cutsceneObj) {
        Cutscene cutscene = new Cutscene(cutsceneObj.getString("name"));

        for(JSONObject nodeObj : cutsceneObj.getList("nodes", JSONObject.class)){
            JSONObject animationObj = nodeObj.getJSONObject("animation");
            Animation animation = new Animation();
            animation.setLength(animationObj.getDouble("length"));

            for(JSONObject timelineObj : animationObj.getList("timelines", JSONObject.class)){
                Timeline timeline = animation.getOrCreateTimeline(timelineObj.getString("name"));

                JSONObject keyframesObj = timelineObj.getJSONObject("keyframes");

                //world keyframes
                for(JSONObject frameObj : keyframesObj.getList("world", JSONObject.class)){
                    timeline.addWorldFrame(frameObj.getDouble("time"), Bukkit.getWorld(frameObj.getString("value")));
                }
                //position keyframes
                for(JSONObject frameObj : keyframesObj.getList("position", JSONObject.class)){
                    JSONObject valueObj = frameObj.getJSONObject("value");
                    timeline.addPositionFrame(frameObj.getDouble("time"), new Vector(valueObj.getDouble("x"), valueObj.getDouble("y"), valueObj.getDouble("z")),
                            KeyframeType.valueOf(frameObj.getString("type")));
                }
                //rotation keyframes
                for(JSONObject frameObj : keyframesObj.getList("rotation", JSONObject.class)){
                    JSONObject valueObj = frameObj.getJSONObject("value");
                    timeline.addRotationFrame(frameObj.getDouble("time"), new EulerAngle(valueObj.getDouble("x"), valueObj.getDouble("y"), valueObj.getDouble("z")),
                            KeyframeType.valueOf(frameObj.getString("type")));
                }
            }

            Node node = WeirdCutscene.inst().getNodeManager().createNode(nodeObj.getString("type"), animation);
            if(node == null){
                WeirdCutscene.inst().getLogger().severe(nodeObj.getString("type") + " is not exist!");
                continue;
            }
            cutscene.nodes.add(node);
        }
        return cutscene;
    }
}
