package com.github.bindglam.weirdcutscene.cutscene.node;

import com.github.bindglam.weirdcutscene.cutscene.animation.Animation;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class CameraNode extends Node {
    private ItemDisplay cameraEntity;

    public CameraNode(Animation animation) {
        super(animation);
    }

    @Override
    public void play() {
        update(false);
    }

    @Override
    public boolean update(boolean updateTime) {
        Vector position = animationProperty.getPositionFrame("camera");
        EulerAngle rotation = animationProperty.getRotationFrame("camera");
        World world = animationProperty.getWorldFrame("camera");

        if(world != null) {
            Location location = new Location(world, position.getX(), position.getY(), position.getZ(), (float) Math.toDegrees(rotation.getY()), (float) Math.toDegrees(rotation.getX()));

            if(cameraEntity == null) {
                cameraEntity = world.spawn(location, ItemDisplay.class);
                cameraEntity.setItemStack(new ItemStack(Material.CARVED_PUMPKIN));
                cameraEntity.setTeleportDuration(1);
            }
            cameraEntity.teleport(location);
        } else if(cameraEntity != null){
            cameraEntity.remove();
            cameraEntity = null;
        }

        // set camera
        if(cameraEntity != null && player != null){
            WrapperPlayServerCamera camera = new WrapperPlayServerCamera(cameraEntity.getEntityId());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, camera);

            WrapperPlayServerChangeGameState gameMode = new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE, 3f);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, gameMode);
        } else if(player != null){
            WrapperPlayServerCamera camera = new WrapperPlayServerCamera(player.getEntityId());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, camera);

            WrapperPlayServerChangeGameState gameMode = new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE, player.getGameMode().getValue());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, gameMode);
        }

        if(updateTime)
            return animationProperty.updateTime();
        return true;
    }

    @Override
    public void dispose() {
        if(cameraEntity != null){
            cameraEntity.remove();
            cameraEntity = null;
        }
        if(player != null){
            WrapperPlayServerCamera camera = new WrapperPlayServerCamera(player.getEntityId());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, camera);

            WrapperPlayServerChangeGameState gameMode = new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.CHANGE_GAME_MODE, player.getGameMode().getValue());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, gameMode);
        }
    }
}
