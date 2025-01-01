package com.github.bindglam.weirdcutscene;

import com.github.bindglam.weirdcutscene.cutscene.Cutscene;
import com.github.bindglam.weirdcutscene.cutscene.node.NodeManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public abstract class WeirdCutscene extends JavaPlugin {
    private static WeirdCutscene instance;

    public abstract NodeManager getNodeManager();

    public abstract void reload();

    public abstract Cutscene loadCutscene(@NotNull String name);

    public static @NotNull WeirdCutscene inst(){
        return instance;
    }

    @ApiStatus.Internal
    public static void inst(@NotNull WeirdCutscene plugin){
        if(WeirdCutscene.instance != null) throw new IllegalStateException("Already initialized!");
        WeirdCutscene.instance = plugin;
    }
}
