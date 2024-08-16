package com.github.janmoeller.clioncmakesourcelistsorter.config

import com.intellij.ui.components.*
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel


/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class AppSettingsComponent {
    val panel: JPanel
    private val myVariableGroupPicker = run {
        val s = JBSlider(-5, 5, 0)
        s.paintTicks = true
        s.paintLabels = true
        s.labelTable = s.createStandardLabels(1)
        s.toolTipText = "Group of paths starting with a CMake variable (\${NAME})."
        s
    }
    private val myEnvVariableGroupPicker = run {
        val s = JBSlider(-5, 5, 0)
        s.paintTicks = true
        s.paintLabels = true
        s.labelTable = s.createStandardLabels(1)
        s.toolTipText = "Group of paths starting with a CMake environment variable (\$ENV{NAME})."
        s
    }
    private val myCacheVariableGroupPicker = run {
        val s = JBSlider(-5, 5, 0)
        s.paintTicks = true
        s.paintLabels = true
        s.labelTable = s.createStandardLabels(1)
        s.toolTipText = "Group of paths starting with a CMake cache variable (\$CACHE{NAME})."
        s
    }
    private val myGeneratorExprGroupPicker = run {
        val s = JBSlider(-5, 5, 0)
        s.paintTicks = true
        s.paintLabels = true
        s.labelTable = s.createStandardLabels(1)
        s.toolTipText = "Group of paths starting with a CMake generator expression (\$<EXPR>)."
        s
    }
    private val myAbsolutePathGroupPicker = run {
        val s = JBSlider(-5, 5, 0)
        s.paintTicks = true
        s.paintLabels = true
        s.labelTable = s.createStandardLabels(1)
        s.toolTipText = "Group of absolute paths (/a/b/c)."
        s
    }
    private val myRemainingGroupPicker = run {
        val s = JBSlider(-5, 5, 0)
        s.paintTicks = true
        s.paintLabels = true
        s.labelTable = s.createStandardLabels(1)
        s.toolTipText = "Group of anything not caught by the other groups."
        s
    }
    private val myReverseStatus = JBCheckBox("Reverse order")

    init {
        panel = FormBuilder.createFormBuilder()
            .addComponent(
                run {
                    val t = JBTextArea(
                        """
                    The group settings below allow treating certain kinds of paths differently.
                    
                    Paths that belong to a group with lesser priority are ordered before any path belonging to a group with greater priority.
                    For example, if the variable group is set to -1, and the generator expression group is set to 0, then the path "${'$'}{VARIABLE}_foo/bar" is ordered before "${'$'}<EXPR>/bar". 
                    """.trimIndent()
                    )
                    t.isEditable = false
                    t.lineWrap = true
                    t.wrapStyleWord = true
                    t
                }
            )
            .addLabeledComponent(JBLabel("Variable Group:"), myVariableGroupPicker, 1, false)
            .addLabeledComponent(JBLabel("Environment Variable Group:"), myEnvVariableGroupPicker, 1, false)
            .addLabeledComponent(JBLabel("Cache Variable Group:"), myCacheVariableGroupPicker, 1, false)
            .addLabeledComponent(JBLabel("Generator Expression Group:"), myGeneratorExprGroupPicker, 1, false)
            .addLabeledComponent(JBLabel("Absolute Path Group:"), myAbsolutePathGroupPicker, 1, false)
            .addLabeledComponent(JBLabel("Remaining Group:"), myRemainingGroupPicker, 1, false)
            .addComponent(
                run {
                    val t = JBTextArea(
                        """
                    The below setting allows to reverse ordering within groups. By default, a is ordered before b; with this option checked, b is ordered before a.
                    """.trimIndent()
                    )
                    t.isEditable = false
                    t.lineWrap = true
                    t.wrapStyleWord = true
                    t
                }
            )
            .addComponent(myReverseStatus, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = myReverseStatus

    var variableGroup: Int
        get() = myVariableGroupPicker.value
        set(newStatus) {
            myVariableGroupPicker.value = newStatus
        }

    var envVariableGroup: Int
        get() = myEnvVariableGroupPicker.value
        set(newStatus) {
            myEnvVariableGroupPicker.value = newStatus
        }

    var cacheVariableGroup: Int
        get() = myCacheVariableGroupPicker.value
        set(newStatus) {
            myCacheVariableGroupPicker.value = newStatus
        }

    var generatorExpressionGroup: Int
        get() = myGeneratorExprGroupPicker.value
        set(newStatus) {
            myGeneratorExprGroupPicker.value = newStatus
        }

    var absolutePathGroup: Int
        get() = myAbsolutePathGroupPicker.value
        set(newStatus) {
            myAbsolutePathGroupPicker.value = newStatus
        }

    var remainingGroup: Int
        get() = myRemainingGroupPicker.value
        set(newStatus) {
            myRemainingGroupPicker.value = newStatus
        }

    var reverse: Boolean
        get() = myReverseStatus.isSelected
        set(newStatus) {
            myReverseStatus.isSelected = newStatus
        }
}