package com.github.janmoeller.clioncmakesourcelistsorter.formatting

class CMakePath(path: String) : Iterable<String> {
    val original = path
    val absolute = path.startsWith("/")
    val startsWithRegularVariable: Boolean
        get() = parts.isNotEmpty() && parts.first().startsWith("\${")
    val startsWithEnvVariable: Boolean
        get() = parts.isNotEmpty() && parts.first().startsWith("\$ENV{")
    val startsWithCacheVariable: Boolean
        get() = parts.isNotEmpty() && parts.first().startsWith("\$CACHE{")
    val startsWithGeneratorExpression: Boolean
        get() = parts.isNotEmpty() && parts.first().startsWith("\$<")
    private val parts: List<String> = splitPath(path)

    // Function to split a path into its components
    // Note: In CMake, paths are separated by '/', but the same character is also used in variable names. This code
    // parses variable references correctly even in the presence of '/'.
    // Variables can be nested and follow the syntax described here: https://cmake.org/cmake/help/latest/manual/cmake-language.7.html#variable-references
    private fun splitPath(path: String): List<String> {
        var parts = mutableListOf<String>()

        var remaining = path
        while (remaining.isNotEmpty()) {
            val (match, new_remaining) = parsePathSegment(remaining)
            parts.add(match)
            remaining = new_remaining
        }
        return parts
    }

    data class ParseResult(var match: String, var remaining: String)

    private fun parsePathSegment(path: String): ParseResult {

        val result = ParseResult("", path)
        while (result.remaining.isNotEmpty()) {
            val c = result.remaining.first()
            if (c == '$') {
                val variableRef = parseExpression(result.remaining)
                if (variableRef.match.isNotEmpty()) {
                    result.match += variableRef.match
                    result.remaining = variableRef.remaining
                    continue
                }
            } else if (c == '/') {
                result.remaining = result.remaining.drop(1)
                break
            }
            result.match += c
            result.remaining = result.remaining.drop(1)
        }
        return result
    }

    // This function parses variable references and generator expressions
    private fun parseExpression(s: String): ParseResult {
        val result = ParseResult("", s)
        var endChar = '}'
        if (result.remaining.startsWith("\${")) {
            result.match += "\${"
            result.remaining = result.remaining.drop(2);
        } else if (result.remaining.startsWith("\$ENV{")) {
            result.match += "\$ENV{"
            result.remaining = result.remaining.drop(5);
        } else if (result.remaining.startsWith("\$CACHE{")) {
            result.match += "\$CACHE{"
            result.remaining = result.remaining.drop(7);
        } else if (result.remaining.startsWith("$<")) {
            result.match += "$<"
            result.remaining = result.remaining.drop(2);
            endChar = '>'
        } else
            return result

        while (result.remaining.isNotEmpty()) {
            val c = result.remaining.first()
            // Parse nested expression
            if (c == '$') {
                val inner = parseExpression(result.remaining)
                // If the $ did not start an expression, the result is empty.
                if (inner.match.isNotEmpty()) {
                    result.match += inner.match
                    result.remaining = inner.remaining
                    continue
                }
            }
            result.match += c
            result.remaining = result.remaining.drop(1)
            if (c == endChar)
                break
        }

        return result
    }

    override fun iterator(): Iterator<String> {
        return parts.iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CMakePath

        if (absolute != other.absolute) return false
        if (parts != other.parts) return false

        return true
    }

    override fun hashCode(): Int {
        var result = absolute.hashCode()
        result = 31 * result + parts.hashCode()
        return result
    }

    override fun toString(): String {
        var path = parts.joinToString(separator = "/")
        return "CMakePath(${path})"
    }


}