package com.github.janmoeller.clioncmakesourcelistsorter.config

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.annotations.NotNull
import javax.swing.JComponent
import javax.swing.JPanel


/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class AppSettingsComponent {
    val panel: JPanel
    private val myReverseStatus = JBCheckBox("Reverse order")

    init {
        panel = FormBuilder.createFormBuilder()
            .addComponent(myReverseStatus, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = myReverseStatus

    var reverse: Boolean
        get() = myReverseStatus.isSelected
        set(newStatus) {
            myReverseStatus.isSelected = newStatus
        }
}