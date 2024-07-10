package com.github.janmoeller.clioncmakesourcelistsorter.formatting

import com.github.janmoeller.clioncmakesourcelistsorter.config.ConfigManagerService
import com.intellij.lang.ASTNode
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor
import com.jetbrains.cmake.CMakeLanguage
import com.jetbrains.cmake.psi.CMakeCommand
import com.jetbrains.cmake.psi.CMakeCommandArguments
import com.jetbrains.cmake.psi.CMakeElementFactory
import java.text.Collator
import java.util.*
import kotlin.Comparator

class MyPreFormatProcessor : PreFormatProcessor {
    private val commands = setOf("add_executable", "add_library", "target_sources")

    // Function to split a path into its components
    // Note: In CMake, paths are separated by '/', but the native path separator is also supported. This sorting only
    // takes '/' into account.
    private fun splitPath(path: String): List<String> {
        return path.split('/').filter { it.isNotEmpty() }
    }

    // case-insensitive collator
    private val caseInsensitiveCollator = Collator.getInstance(Locale.ROOT).apply {
        strength = Collator.PRIMARY
    }

    // Comparator that compares paths hierarchically
    private fun makeComparator(config: ConfigManagerService): Comparator<String> {
        val collator = if (config.state.case_sensitive) Collator.getInstance(Locale.ROOT) else caseInsensitiveCollator
        return Comparator<String> { path1, path2 ->
            val parts1 = splitPath(path1)
            val parts2 = splitPath(path2)

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


    // Sorts the argument list within the given index range.
    private fun sortArguments(args: CMakeCommandArguments?, range: IntRange) {
        // If there are no arguments, do nothing
        if (args == null)
            return

        val project = args.project

        // Sort the argument list
        val argumentList = args.children.map { it.text }.toMutableList()
        val sortedSubset = argumentList.subList(range.first, range.last)
            .sortedWith(makeComparator(project.service<ConfigManagerService>()))
        for ((index, value) in sortedSubset.withIndex())
            argumentList[range.first + index] = value

        // Create new sorted argument elements
        val sortedArguments = argumentList.map { CMakeElementFactory.createArgument(project, it) }

        // Update the tree with the sorted arguments
        WriteCommandAction.runWriteCommandAction(project) {
            for ((index, pair) in sortedArguments.zip(args.children).withIndex()) {
                if (!range.contains(index))
                    continue
                val (a, b) = pair
                args.node.replaceChild(b.node, a.node)
            }
        }
    }

    // Finds the first index matching a predicate after a certain index. Returns size if none.
    private fun <T> findIndexOfFirstAfterIndex(list: List<T>, startIndex: Int, predicate: (T) -> Boolean): Int {
        for (index in startIndex until list.size) {
            if (predicate(list[index])) {
                return index
            }
        }
        return list.size
    }

    // Computes the index ranges of an argument list that need to be sorted.
    private fun findSourceFileArgumentRanges(cmd: String, args: CMakeCommandArguments?): List<IntRange> {
        if (args == null) return listOf()

        val result = mutableListOf<IntRange>()
        val argStrings = args.children.map { it.text }

        // The first argument is always the target -> skip that one
        var startIdx = 1
        while (startIdx < argStrings.size) {
            when (argStrings[startIdx]) {
                // Don't touch imported or alias targets
                "IMPORTED", "ALIAS" -> {
                    return listOf()
                }

                // These are all indicators without associated arguments -> just skip them
                "WIN32", "MACOSX_BUNDLE", "EXCLUDE_FROM_ALL", "STATIC", "SHARED", "MODULE", "OBJECT", "INTERFACE", "UNKNOWN", "PUBLIC", "PRIVATE" -> {
                    startIdx++
                }

                // These are special arguments with a single following argument -> skip both of them
                "FILE_SET", "TYPE" -> {
                    startIdx += 2
                }

                // There can be an arbitrarily long list after these. Find the start of the next possible argument
                // after, and sort that range independently.
                "BASE_DIRS", "FILES" -> {
                    val stopIdx = findIndexOfFirstAfterIndex(argStrings, startIdx + 1) {
                        it in listOf(
                            "INTERFACE",
                            "PUBLIC",
                            "PRIVATE",
                            "FILES"
                        )
                    }
                    result.add(startIdx + 1..stopIdx)
                    startIdx = stopIdx
                }

                // If this isn't one of the special arguments handled above, then this must be the first sortable
                // element. Except for target_sources, this is guaranteed to be the tail of the argument list.
                else -> {
                    val stopIdx = when (cmd) {
                        "target_sources" -> {
                            findIndexOfFirstAfterIndex(argStrings, startIdx) {
                                it in listOf(
                                    "INTERFACE",
                                    "PUBLIC",
                                    "PRIVATE"
                                )
                            }
                        }

                        else -> {
                            argStrings.size
                        }
                    }
                    result.add(startIdx..stopIdx)
                    startIdx = stopIdx
                }
            }
        }

        return result
    }

    override fun process(element: ASTNode, range: TextRange): TextRange {
        // Ignore non-CMake files
        if (element.elementType.language != CMakeLanguage.INSTANCE)
            return range

        // Find relevant nodes that carry source lists
        element.psi.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                val cmd = element as? CMakeCommand
                if (cmd != null && cmd.name in commands && cmd.cMakeCommandArguments != null && element.textRange.intersects(
                        range
                    )
                ) {
                    // Each of the handled commands can have several sortable sub sets. Sort those independently of one
                    // another.
                    val ranges = findSourceFileArgumentRanges(cmd.name, cmd.cMakeCommandArguments)
                    for (r in ranges)
                        sortArguments(cmd.cMakeCommandArguments, r)
                }
                super.visitElement(element)
            }
        })

        return range
    }
}