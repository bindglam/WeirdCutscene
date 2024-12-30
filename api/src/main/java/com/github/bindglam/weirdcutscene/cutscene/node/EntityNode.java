package com.github.bindglam.weirdcutscene.cutscene.node;

import com.alibaba.fastjson2.JSONObject;
import com.github.bindglam.weirdcutscene.WeirdCutscene;
import com.github.bindglam.weirdcutscene.cutscene.animation.Animation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Objects;

public class EntityNode extends Node {
    private final EntityType entityType;

    private Entity entity;

    public EntityNode(Animation animation, JSONObject jsonObject) {
        super(animation, jsonObject);
        entityType = EntityType.valueOf(jsonObject.getString("entityType"));
    }

    @Override
    public void play() {
        update(false);
    }

    @Override
    public boolean update(boolean updateTime) {
        Vector position = animationProperty.getPositionFrame("entity");
        EulerAngle rotation = animationProperty.getRotationFrame("entity");
        World world = animationProperty.getWorldFrame("entity");

        if(world != null) {
            Location location = new Location(world, position.getX(), position.getY(), position.getZ(), (float) Math.toDegrees(rotation.getY()), (float) Math.toDegrees(rotation.getX()));

            if(entity == null) {
                entity = world.spawnEntity(location, entityType);

                for(Player other : Bukkit.getOnlinePlayers()){
                    if(Objects.equals(other.getUniqueId().toString(), player.getUniqueId().toString())) continue;
                    other.hideEntity(WeirdCutscene.inst(), entity);
                }
            }
            entity.teleport(location);
        } else if(entity != null){
            entity.remove();
            entity = null;
        }

        if(updateTime)
            return animationProperty.updateTime();
        return true;
    }

    @Override
    public void dispose() {
        if(entity != null) {
            entity.remove();
            entity = null;
        }
    }
}
