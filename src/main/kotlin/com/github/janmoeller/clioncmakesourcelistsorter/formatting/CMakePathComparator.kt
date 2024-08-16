package com.github.janmoeller.clioncmakesourcelistsorter.formatting

import java.text.Collator
import java.util.*
import kotlin.Comparator

class CMakePathComparator(
    val increasing: Boolean = true,
    val absoluteGroup: Int = 0,
    val variableGroup: Int = 0,
    val cacheVariableGroup: Int = 0,
    val envVariableGroup: Int = 0,
    val generatorExprGroup: Int = 0,
    val anythingElseGroup: Int = 0
) : Comparator<CMakePath> {
    private val collator = Collator.getInstance(Locale.ROOT)
    private val comparator = Comparator<CMakePath> { path1, path2 ->
        val group1 = group(path1)
        val group2 = group(path2)

        // If the two paths are from different groups, just use those for ordering
        if (group1 != group2)
            return@Comparator group1.compareTo(group2)

        // If they are within the same group, compare the paths lexicographically
        var parts1 = path1.toList()
        var parts2 = path2.toList()

        // swap paths if we are supposed to sort in reverse order
        if (!increasing)
            parts1 = parts2.also { parts2 = parts1 }

        // Compare each part of the path
        for (i in 0 until minOf(parts1.size, parts2.size)) {
            val comparison = collator.compare(parts1[i], parts2[i])
            if (comparison != 0) {
                return@Comparator comparison
            }
        }

        // If all compared parts are equal, the shorter path comes first
        parts1.size.compareTo(parts2.size)
    }

    private fun group(path: CMakePath): Int {
        if (path.absolute)
            return absoluteGroup
        if (path.startsWithRegularVariable)
            return variableGroup
        if (path.startsWithCacheVariable)
            return cacheVariableGroup
        if (path.startsWithEnvVariable)
            return envVariableGroup
        if (path.startsWithGeneratorExpression)
            return generatorExprGroup
        return anythingElseGroup
    }

    override fun compare(o1: CMakePath?, o2: CMakePath?): Int {
        return comparator.compare(o1, o2)
    }
}