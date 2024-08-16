package com.github.janmoeller.clioncmakesourcelistsorter.config

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nullable
import java.util.*
import javax.swing.*


/**
 * Provides controller functionality for application settings.
 */
internal class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    override fun getPreferredFocusedComponent(): JComponent {
        return mySettingsComponent!!.preferredFocusedComponent
    }

    @Nullable
    override fun createComponent(): JComponent {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val state: AppSettings.State =
            Objects.requireNonNull(AppSettings.instance.state)
        return mySettingsComponent!!.variableGroup != state.variableGroup
                || mySettingsComponent!!.envVariableGroup != state.envVariableGroup
                || mySettingsComponent!!.cacheVariableGroup != state.cacheVariableGroup
                || mySettingsComponent!!.generatorExpressionGroup != state.generatorExpressionGroup
                || mySettingsComponent!!.absolutePathGroup != state.absolutePathGroup
                || mySettingsComponent!!.remainingGroup != state.remainingGroup
                || mySettingsComponent!!.reverse != state.reverse
    }

    override fun apply() {
        val state: AppSettings.State =
            Objects.requireNonNull(AppSettings.instance.state)
        state.variableGroup = mySettingsComponent!!.variableGroup
        state.envVariableGroup = mySettingsComponent!!.envVariableGroup
        state.cacheVariableGroup = mySettingsComponent!!.cacheVariableGroup
        state.generatorExpressionGroup = mySettingsComponent!!.generatorExpressionGroup
        state.absolutePathGroup = mySettingsComponent!!.absolutePathGroup
        state.remainingGroup = mySettingsComponent!!.remainingGroup
        state.reverse = mySettingsComponent!!.reverse
    }

    override fun reset() {
        val state: AppSettings.State =
            Objects.requireNonNull(AppSettings.instance.state)
        mySettingsComponent!!.variableGroup = state.variableGroup
        mySettingsComponent!!.envVariableGroup = state.envVariableGroup
        mySettingsComponent!!.cacheVariableGroup = state.cacheVariableGroup
        mySettingsComponent!!.generatorExpressionGroup = state.generatorExpressionGroup
        mySettingsComponent!!.absolutePathGroup = state.absolutePathGroup
        mySettingsComponent!!.remainingGroup = state.remainingGroup
        mySettingsComponent!!.reverse = state.reverse
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }

    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "CMakeSourceListSorter Settings"
    }
}