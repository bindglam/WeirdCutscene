package com.github.bindglam.weirdcutscene.cutscene.node.player;

import com.alibaba.fastjson2.JSONObject;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.bindglam.weirdcutscene.cutscene.animation.Animation;
import com.github.bindglam.weirdcutscene.cutscene.node.Node;
import com.github.bindglam.weirdcutscene.cutscene.node.player.model.LimbType;
import com.github.bindglam.weirdcutscene.cutscene.node.player.model.PlayerBone;
import com.github.bindglam.weirdcutscene.cutscene.node.player.texture.TextureWrapper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerNode extends Node {
    private final Map<LimbType, PlayerBone> bones = new HashMap<>();

    @Getter private final TextureWrapper texture;

    @Getter private Vector baseVector;
    @Getter private World baseLevel;
    @Getter private float baseYaw;

    public PlayerNode(Animation animation, JSONObject jsonObject) {
        super(animation, jsonObject);

        if(jsonObject.getJSONObject("skin").containsKey("url")){
            texture = new TextureWrapper(jsonObject.getJSONObject("skin").getString("url"), jsonObject.getJSONObject("skin").getBoolean("isSlim"));
        } else {
            if(jsonObject.getJSONObject("skin").containsKey("playerName")) {
                texture = TextureWrapper.fromBase64(getTexture(Bukkit.getOfflinePlayer(jsonObject.getJSONObject("skin").getString("playerName"))));
            } else {
                texture = TextureWrapper.fromBase64(getTexture(Bukkit.getOfflinePlayer(UUID.fromString(jsonObject.getJSONObject("skin").getString("playerUUID")))));
            }
        }
    }

    @Override
    public void play() {
        update(false);
    }

    @Override
    public boolean update(boolean updateTime) {
        baseVector = animationProperty.getPositionFrame("entity");
        EulerAngle rotation = animationProperty.getRotationFrame("entity");
        baseLevel = animationProperty.getWorldFrame("entity");

        baseYaw = (float) Math.toDegrees(rotation.getY());

        if(baseLevel != null){
            if(bones.isEmpty()){
                PlayerBone rootBone = new PlayerBone(this, LimbType.ROOT);
                bones.put(LimbType.ROOT, rootBone);

                PlayerBone bodyBone = new PlayerBone(this, LimbType.TORSO);
                bodyBone.spawn();
                bones.put(LimbType.TORSO, bodyBone);
                rootBone.addChild(bodyBone);

                PlayerBone headBone = new PlayerBone(this, LimbType.HEAD);
                headBone.spawn();
                bones.put(LimbType.HEAD, headBone);
                bodyBone.addChild(headBone);

                PlayerBone rightArmBone = new PlayerBone(this, LimbType.RIGHT_ARM);
                rightArmBone.spawn();
                bones.put(LimbType.RIGHT_ARM, rightArmBone);
                bodyBone.addChild(rightArmBone);
                PlayerBone leftArmBone = new PlayerBone(this, LimbType.LEFT_ARM);
                leftArmBone.spawn();
                bones.put(LimbType.LEFT_ARM, leftArmBone);
                bodyBone.addChild(leftArmBone);

                PlayerBone rightLegBone = new PlayerBone(this, LimbType.RIGHT_LEG);
                rightLegBone.spawn();
                bones.put(LimbType.RIGHT_LEG, rightLegBone);
                rootBone.addChild(rightLegBone);
                PlayerBone leftLegBone = new PlayerBone(this, LimbType.LEFT_LEG);
                leftLegBone.spawn();
                bones.put(LimbType.LEFT_LEG, leftLegBone);
                rootBone.addChild(leftLegBone);
            }

            for(PlayerBone bone : bones.values()){
                bone.update();
            }
        } else if(!bones.isEmpty()){
            for(PlayerBone bone : bones.values()){
                bone.dispose();
            }
            bones.clear();
        }

        if(updateTime)
            return animationProperty.updateTime();
        return true;
    }

    @Override
    public void dispose() {
        for(PlayerBone bone : bones.values()){
            bone.dispose();
        }
        bones.clear();
    }

    private String getTexture(OfflinePlayer player){
        String raw = "";
        for(ProfileProperty property : player.getPlayerProfile().getProperties()){
            if(property.getName().equals("textures")){
                raw = property.getValue();
                break;
            }
        }
        return raw;
    }
}

