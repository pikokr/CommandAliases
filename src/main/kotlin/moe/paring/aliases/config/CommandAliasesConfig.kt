package moe.paring.aliases.config

data class Alias(
    val name: List<String>,
    val executes: String?,
    val children: List<Alias>?,
)
