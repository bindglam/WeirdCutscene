package io.github.bindglam.weirdcutscene.cutscene.node

import com.github.bindglam.weirdcutscene.WeirdCutscene
import com.github.bindglam.weirdcutscene.cutscene.animation.Animation
import com.github.bindglam.weirdcutscene.cutscene.node.Node
import com.github.bindglam.weirdcutscene.cutscene.node.NodeManager
import java.lang.reflect.InvocationTargetException

class NodeManagerImpl : NodeManager {
    private val nodeClasses = HashMap<String, Class<out Node>>()

    override fun register(nodeClass: Class<out Node>) {
        nodeClasses[nodeClass.simpleName] = nodeClass

        WeirdCutscene.inst().logger.info("${nodeClass.simpleName} is registered")
    }

    override fun createNode(className: String, animation: Animation): Node? {
        if (!nodeClasses.containsKey(className)) return null
        val clazz = nodeClasses[className]!!
        return try {
            clazz.getDeclaredConstructor(Animation::class.java).newInstance(animation)
        } catch (e: InstantiationException) {
            null
        } catch (e: IllegalAccessException) {
            null
        } catch (e: InvocationTargetException) {
            null
        } catch (e: NoSuchMethodException) {
            null
        }
    }
}