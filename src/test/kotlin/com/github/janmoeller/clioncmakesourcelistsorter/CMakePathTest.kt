package com.github.janmoeller.clioncmakesourcelistsorter

import com.github.janmoeller.clioncmakesourcelistsorter.formatting.CMakePath
import junit.framework.TestCase

class CMakePathTest : TestCase() {

    fun testCMakePathSplitting() {
        // simple relative paths
        assertEquals(listOf("a"), CMakePath("a").toList())
        assertEquals(listOf("foo"), CMakePath("foo").toList())
        assertEquals(listOf("a", "b"), CMakePath("a/b").toList())
        assertEquals(listOf("foo", "bar"), CMakePath("foo/bar").toList())
        assertEquals(listOf("foo", "bar", "foo"), CMakePath("foo/bar/foo").toList())
        assertEquals(listOf("foo", "bar", "baz.bat"), CMakePath("foo/bar/baz.bat").toList())

        // simple absolute paths
        assertEquals(listOf(""), CMakePath("/").toList())
        assertEquals(true, CMakePath("/").absolute)
        assertEquals(listOf("", "foo", "bar", "foo"), CMakePath("/foo/bar/foo").toList())
        assertEquals(true, CMakePath("/foo/bar/foo").absolute)

        // paths containing variable references
        assertEquals(listOf("\${}"), CMakePath("\${}").toList())
        assertEquals(listOf("\${a}"), CMakePath("\${a}").toList())
        assertEquals(listOf("\${a}", "bar"), CMakePath("\${a}/bar").toList())
        assertEquals(listOf("\${a/b}", "bar"), CMakePath("\${a/b}/bar").toList())
        assertEquals(listOf("\${a/\${foo}}", "bar"), CMakePath("\${a/\${foo}}/bar").toList())
        assertEquals(listOf("foo\${a}", "\${///}"), CMakePath("foo\${a}/\${///}").toList())
        assertEquals(listOf("foo\$ENV{a}bar"), CMakePath("foo\$ENV{a}bar").toList())
        assertEquals(listOf("foo\$CACHE{a}bar"), CMakePath("foo\$CACHE{a}bar").toList())

        // paths that look like variable references but aren't
        assertEquals(listOf("$"), CMakePath("$").toList())
        assertEquals(listOf("\$FOO"), CMakePath("\$FOO").toList())
        assertEquals(listOf("\$ENF{foo", "bar}"), CMakePath("\$ENF{foo/bar}").toList())
    }
}
