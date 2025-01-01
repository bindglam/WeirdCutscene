package com.github.bindglam.weirdcutscene.cutscene.node.player.model;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.bindglam.weirdcutscene.cutscene.node.player.PlayerNode;
import com.github.bindglam.weirdcutscene.utils.math.MathUtil;
import com.github.bindglam.weirdcutscene.utils.math.Offset;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerBone {

    protected final Map<String, PlayerBone> children = new HashMap<>();
    @Getter protected final PlayerNode model;
    @Getter protected final LimbType type;

    @Getter @Setter
    protected PlayerBone parent;

    // Properties
    protected Vector position = new Vector();
    @Getter protected EulerAngle rotation = EulerAngle.ZERO;

    private String boneName = "";

    private ItemDisplay itemDisplay;

    public PlayerBone(PlayerNode model) {
        this(model, null);
    }

    public PlayerBone(PlayerNode model, LimbType type) {
        this.model = model;
        this.type = type;

        if(type != null) {
            boneName = type.name().toLowerCase();
        }
    }

    public void spawn(){
        if(type != LimbType.ROOT) {
            itemDisplay = model.getBaseLevel().spawn(new Location(model.getBaseLevel(), position.getX(), position.getY(), position.getZ(), model.getBaseYaw(), 0f), ItemDisplay.class);
            itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND);
            itemDisplay.setViewRange(0.6f);
            itemDisplay.setInterpolationDelay(1);
            itemDisplay.setTeleportDuration(1);
            itemDisplay.setTransformation(new Transformation(
                    type.getInitialTranslation(),
                    toQuaternion(rotation.add(type.getRotation().getX(), type.getRotation().getY(), type.getRotation().getZ())),
                    new Vector3f(1f, 1f, 1f),
                    new Quaternionf(0.0, 0.0, 0.0, 1.0)
            ));
            if (type.getModelId() != -1) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();

                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                profile.setProperty(new ProfileProperty("textures", model.getTexture().toBase64()));
                meta.setPlayerProfile(profile);

                meta.setCustomModelData(model.getTexture().isSlim() ? type.getSlimId() : type.getModelId());
                skull.setItemMeta(meta);
                itemDisplay.setItemStack(skull);
            }
        }
    }

    public void update() {
        Vector fPosition = model.getAnimationProperty().getPositionFrame(boneName);
        EulerAngle fRotation = model.getAnimationProperty().getRotationFrame(boneName);

        Vector pPosition = parent == null ? getModel().getBaseVector() : parent.getPosition();
        EulerAngle pRotation = parent == null ? EulerAngle.ZERO : parent.getRotation();

        fPosition = Offset.getRelativeLocation(pRotation, fPosition.add(type.getOrigin()));
        fRotation = MathUtil.globalRotate(EulerAngle.ZERO, fRotation);

        if(parent == null) {
            fPosition.add(LimbType.base);
        }

        fPosition = Offset.rotateYaw(fPosition, Math.toRadians(getModel().getBaseYaw()));

        position = pPosition.add(fPosition);
        rotation = MathUtil.localRotate(pRotation, fRotation);

        if(itemDisplay != null) {
            itemDisplay.teleport(new Location(model.getBaseLevel(), position.getX(), position.getY(), position.getZ(), model.getBaseYaw(), 0f));
            itemDisplay.setTransformation(new Transformation(
                    type.getInitialTranslation(),
                    toQuaternion(rotation.add(type.getRotation().getX(), type.getRotation().getY(), type.getRotation().getZ())),
                    new Vector3f(1f, 1f, 1f),
                    new Quaternionf(0.0, 0.0, 0.0, 1.0)
            ));
        }

        for (PlayerBone bone : children.values())
            bone.update();
    }

    public void dispose(){
        if(itemDisplay != null){
            itemDisplay.remove();
        }
    }

    public void addChild(PlayerBone bone) {
        bone.setParent(this);
        children.put(bone.boneName, bone);
    }

    public Vector getPosition() {
        return position.clone();
    }

    public Quaternionf toQuaternion(EulerAngle angle) {
        var roll = angle.getX();
        var pitch = angle.getY();
        var yaw = angle.getZ();

        var cr = Math.cos(roll * 0.5);
        var sr = Math.sin(roll * 0.5);
        var cp = Math.cos(pitch * 0.5);
        var sp = Math.sin(pitch * 0.5);
        var cy = Math.cos(yaw * 0.5);
        var sy = Math.sin(yaw * 0.5);

        var q = new Quaternionf();
        q.w = (float) -(cr * cp * cy + sr * sp * sy);
        q.x = (float) -(sr * cp * cy - cr * sp * sy);
        q.y = (float) (cr * sp * cy + sr * cp * sy);
        q.z = (float) (cr * cp * sy - sr * sp * cy);

        return q;
    }
}
