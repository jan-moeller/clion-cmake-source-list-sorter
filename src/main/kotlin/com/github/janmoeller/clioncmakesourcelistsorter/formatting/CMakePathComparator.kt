package com.github.janmoeller.clioncmakesourcelistsorter.formatting

import java.text.Collator
import java.util.*
import kotlin.Comparator

class CMakePathComparator(val increasing: Boolean = true, val absoluteFirst: Boolean = true) : Comparator<CMakePath> {
    // Static data
    private object d {
        val collator = Collator.getInstance(Locale.ROOT)
        val absoluteFirstComp = Comparator<CMakePath> { path1, path2 ->
            if (path1.absolute && !path2.absolute)
                return@Comparator -1
            if (!path1.absolute && path2.absolute)
                return@Comparator 1
            return@Comparator 0
        }
        val pathComp = Comparator<CMakePath> { path1, path2 ->
            val parts1 = path1.toList()
            val parts2 = path2.toList()

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
    }

    private val comparator = makeComparator()

    private fun wrapComparator(inner: Comparator<CMakePath>, outer: Comparator<CMakePath>): Comparator<CMakePath> {
        return Comparator<CMakePath> { path1, path2 ->
            val o = outer.compare(path1, path2)
            if (o != 0)
                return@Comparator o
            return@Comparator inner.compare(path1, path2)
        }
    }

    private fun makeComparator(): Comparator<CMakePath> {
        val curAbsoluteComparator = if (absoluteFirst) d.absoluteFirstComp else d.absoluteFirstComp.reversed()
        val curPathComparator = if (increasing) d.pathComp else d.pathComp.reversed()

        return wrapComparator(curPathComparator, curAbsoluteComparator)
    }

    override fun compare(o1: CMakePath?, o2: CMakePath?): Int {
        return comparator.compare(o1, o2)
    }
}