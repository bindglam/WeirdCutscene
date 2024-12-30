package io.github.bindglam.weirdcutscene

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.github.bindglam.weirdcutscene.WeirdCutscene
import com.github.bindglam.weirdcutscene.cutscene.Cutscene
import com.github.bindglam.weirdcutscene.cutscene.node.CameraNode
import com.github.bindglam.weirdcutscene.cutscene.node.NodeManager
import com.github.retrooper.packetevents.PacketEvents
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandExecutor
import io.github.bindglam.weirdcutscene.cutscene.node.NodeManagerImpl
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class WeirdCutsceneImpl : WeirdCutscene() {
    private val cutscenesFolder = File("plugins/WeirdCutscene/cutscenes")

    private val nodeManager = NodeManagerImpl()

    private val loadedCutscenes = HashMap<String, JSONObject>()

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this))

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()

        registerCommands()
    }

    override fun onEnable() {
        CommandAPI.onEnable()

        PacketEvents.getAPI().init()

        inst(this)

        if(!cutscenesFolder.exists())
            cutscenesFolder.mkdirs()

        nodeManager.register(CameraNode::class.java)

        reload()
    }

    override fun onDisable() {
        CommandAPI.onDisable()

        PacketEvents.getAPI().terminate()
    }

    private fun registerCommands() {
        CommandAPICommand("weirdcutscene")
            .withPermission(CommandPermission.OP)
            .withAliases("wc", "cutscene")
            .withSubcommands(
                CommandAPICommand("play")
                    .withArguments(TextArgument("name"), PlayerArgument("player"))
                    .executes(CommandExecutor { sender, args ->
                        val name = args["name"] as String
                        val player = args["player"] as Player

                        if(!loadedCutscenes.containsKey(name)){
                            sender.sendMessage(Component.text("Failed to load cutscene!").color(NamedTextColor.RED))
                            return@CommandExecutor
                        }

                        val cutscene = Cutscene.loadCutscene(loadedCutscenes[name])
                        cutscene.play(player)
                    }),
                CommandAPICommand("reload")
                    .executes(CommandExecutor { sender, _ ->
                        sender.sendMessage(Component.text("Reloading..."))
                        reload()
                        sender.sendMessage(Component.text("Reloaded!"))
                    })
            )
            .register()
    }

    override fun getNodeManager(): NodeManager {
        return nodeManager
    }

    override fun reload() {
        loadedCutscenes.clear()

        for(cutsceneFile in cutscenesFolder.listFiles()!!){
            if(!cutsceneFile.name.endsWith(".cutscene")) continue
            loadedCutscenes[cutsceneFile.nameWithoutExtension] = JSON.parseObject(BufferedReader(FileReader(cutsceneFile)))
        }
    }
}