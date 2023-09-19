package moe.paring.aliases

import io.github.monun.kommand.kommand
import io.github.monun.kommand.node.KommandNode
import moe.paring.aliases.config.Alias
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class CommandAliasesPlugin : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()

        val aliases = mutableListOf<Alias>()

        fun parseAlias(map: Map<*, *>): Alias {
            fun parseChildren(items: List<*>?): List<Alias> {
                if (items == null) return listOf()

                val result = mutableListOf<Alias>()

                items.forEach { item ->
                    if (item !is Map<*, *>) throw IllegalArgumentException("children must be a dictionary")
                    result.add(parseAlias(item))
                }

                return result
            }

            val names = mutableListOf<String>()
            if (map["name"] is String) {
                names.add(map["name"] as String)
            } else {
                (map["names"] as List<*>).forEach {
                    if (it is String) names.add(it)
                    else throw IllegalArgumentException("Value $it is not a string")
                }
            }
            val executes = map["executes"] as String?
            val asOp = map["asOp"] as Boolean? ?: false
            val children = map["children"] as List<*>?

            return Alias(names, executes, parseChildren(children))
        }

        config.getMapList("commands").forEach {
            aliases.add(parseAlias(it))
        }

        fun KommandNode.runner(command: String) {
            executes {
                player.performCommand(command)
            }
        }

        fun KommandNode.addChildren(children: List<Alias>) {
            children.forEach { item ->
                item.name.forEach { name ->
                    then(name) {
                        item.executes?.let { runner(it) }
                        item.children?.let { addChildren(it) }
                    }
                }
            }
        }

        kommand {
            aliases.forEach { alias ->
                alias.name.forEach { name ->
                    register(name) {
                        alias.executes?.let { runner(it) }
                        alias.children?.let { addChildren(it) }
                    }
                }
            }
        }
    }
}
