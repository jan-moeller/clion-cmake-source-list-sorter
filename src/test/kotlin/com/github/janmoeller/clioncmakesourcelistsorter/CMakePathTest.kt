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

    fun testCMakePathStartsWith() {
        assert(!CMakePath("a").startsWithRegularVariable)
        assert(!CMakePath("a").startsWithEnvVariable)
        assert(!CMakePath("a").startsWithCacheVariable)
        assert(!CMakePath("a").startsWithGeneratorExpression)

        assert(!CMakePath("a/b").startsWithRegularVariable)
        assert(!CMakePath("a/b").startsWithEnvVariable)
        assert(!CMakePath("a/b").startsWithCacheVariable)
        assert(!CMakePath("a/b").startsWithGeneratorExpression)

        assert(CMakePath("\${a}/b").startsWithRegularVariable)
        assert(!CMakePath("\${a}/b").startsWithEnvVariable)
        assert(!CMakePath("\${a}/b").startsWithCacheVariable)
        assert(!CMakePath("\${a}/b").startsWithGeneratorExpression)

        assert(!CMakePath("\$ENV{a}/b").startsWithRegularVariable)
        assert(CMakePath("\$ENV{a}/b").startsWithEnvVariable)
        assert(!CMakePath("\$ENV{a}/b").startsWithCacheVariable)
        assert(!CMakePath("\$ENV{a}/b").startsWithGeneratorExpression)

        assert(!CMakePath("\$CACHE{a}/b").startsWithRegularVariable)
        assert(!CMakePath("\$CACHE{a}/b").startsWithEnvVariable)
        assert(CMakePath("\$CACHE{a}/b").startsWithCacheVariable)
        assert(!CMakePath("\$CACHE{a}/b").startsWithGeneratorExpression)

        assert(!CMakePath("\$<a:b>/b").startsWithRegularVariable)
        assert(!CMakePath("\$<a:b>/b").startsWithEnvVariable)
        assert(!CMakePath("\$<a:b>/b").startsWithCacheVariable)
        assert(CMakePath("\$<a:b>/b").startsWithGeneratorExpression)

        assert(!CMakePath("foo\${a}/b").startsWithRegularVariable)
        assert(!CMakePath("foo\$ENV{a}/b").startsWithEnvVariable)
        assert(!CMakePath("foo\$CACHE{a}/b").startsWithCacheVariable)
        assert(!CMakePath("foo\$<a:b>/b").startsWithGeneratorExpression)

        assert(CMakePath("\${a}foo/b").startsWithRegularVariable)
        assert(CMakePath("\$ENV{a}foo/b").startsWithEnvVariable)
        assert(CMakePath("\$CACHE{a}foo/b").startsWithCacheVariable)
        assert(CMakePath("\$<a:b>foo/b").startsWithGeneratorExpression)

        assert(!CMakePath("a/\${b}").startsWithRegularVariable)
        assert(!CMakePath("a/\$ENV{b}").startsWithEnvVariable)
        assert(!CMakePath("a/\$CACHE{b}").startsWithCacheVariable)
        assert(!CMakePath("a/\$<a:b>").startsWithGeneratorExpression)
    }
}
