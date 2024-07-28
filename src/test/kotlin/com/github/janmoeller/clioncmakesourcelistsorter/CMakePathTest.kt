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

        // paths containing generator expressions
        assertEquals(listOf("\$<>"), CMakePath("\$<>").toList())
        assertEquals(listOf("\$<PLATFORM_ID>"), CMakePath("\$<PLATFORM_ID>").toList())
        assertEquals(
            listOf("\$<CXX_COMPILER_VERSION:version>", "bar"),
            CMakePath("\$<CXX_COMPILER_VERSION:version>/bar").toList()
        )
        assertEquals(
            listOf("\$<PATH:RELATIVE_PATH,a/b/c,src/>"),
            CMakePath("\$<PATH:RELATIVE_PATH,a/b/c,src/>").toList()
        )
        assertEquals(
            listOf("\$<JOIN:$<TARGET_PROPERTY:tgt,INCLUDE_DIRECTORIES>,;foo>", "bar"),
            CMakePath("\$<JOIN:\$<TARGET_PROPERTY:tgt,INCLUDE_DIRECTORIES>,;foo>/bar").toList()
        )
        assertEquals(
            listOf("foo\$<PATH:GET_ROOT_NAME,\${SOME_DIR}>", "\$<PATH:REPLACE_EXTENSION,\${SOME_PATH},.c>bar"),
            CMakePath("foo\$<PATH:GET_ROOT_NAME,\${SOME_DIR}>/\$<PATH:REPLACE_EXTENSION,\${SOME_PATH},.c>bar").toList()
        )
    }
}
