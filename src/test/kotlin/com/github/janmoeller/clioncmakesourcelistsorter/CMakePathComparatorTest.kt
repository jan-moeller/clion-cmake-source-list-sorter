package com.github.janmoeller.clioncmakesourcelistsorter

import com.github.janmoeller.clioncmakesourcelistsorter.formatting.CMakePath
import com.github.janmoeller.clioncmakesourcelistsorter.formatting.CMakePathComparator
import junit.framework.TestCase

class CMakePathComparatorTest : TestCase() {

    fun testCMakePathComparator() {
        assertEquals(0, CMakePathComparator().compare(CMakePath("a"), CMakePath("a")))
        assertEquals(0, CMakePathComparator().compare(CMakePath("a/b"), CMakePath("a/b")))
        assertEquals(-1, CMakePathComparator().compare(CMakePath("/a/b"), CMakePath("a/b")))
    }

    fun testCMakePathComparatorLists() {
        assertEquals(
            listOf(
                CMakePath("/a/a"),
                CMakePath("/a/a/a"),
                CMakePath("/a/b"),
                CMakePath("a/a"),
                CMakePath("a/a/a"),
                CMakePath("a/b"),
            ),
            listOf(
                CMakePath("a/a/a"),
                CMakePath("/a/a/a"),
                CMakePath("/a/a"),
                CMakePath("a/b"),
                CMakePath("/a/b"),
                CMakePath("a/a"),
            ).sortedWith(CMakePathComparator())
        )

        assertEquals(
            listOf(
                CMakePath("a/a"),
                CMakePath("a/a/a"),
                CMakePath("a/b"),
                CMakePath("/a/a"),
                CMakePath("/a/a/a"),
                CMakePath("/a/b"),
            ),
            listOf(
                CMakePath("a/a/a"),
                CMakePath("/a/a/a"),
                CMakePath("/a/a"),
                CMakePath("a/b"),
                CMakePath("/a/b"),
                CMakePath("a/a"),
            ).sortedWith(CMakePathComparator(absoluteGroup = 1))
        )

        assertEquals(
            listOf(
                CMakePath("/a/a"),
                CMakePath("/a/a/a"),
                CMakePath("/a/b"),
                CMakePath("a/a"),
                CMakePath("a/a/a"),
                CMakePath("a/b"),
            ),
            listOf(
                CMakePath("a/a/a"),
                CMakePath("/a/a/a"),
                CMakePath("/a/a"),
                CMakePath("a/b"),
                CMakePath("/a/b"),
                CMakePath("a/a"),
            ).sortedWith(CMakePathComparator(absoluteGroup = -1))
        )

        println()
        assertEquals(
            listOf(
                CMakePath("/a/b"),
                CMakePath("/a/a/a"),
                CMakePath("/a/a"),
                CMakePath("a/b"),
                CMakePath("a/a/a"),
                CMakePath("a/a"),
            ),
            listOf(
                CMakePath("a/a/a"),
                CMakePath("/a/a/a"),
                CMakePath("/a/a"),
                CMakePath("a/b"),
                CMakePath("/a/b"),
                CMakePath("a/a"),
            ).sortedWith(CMakePathComparator(absoluteGroup = -1, increasing = false))
        )
    }
}