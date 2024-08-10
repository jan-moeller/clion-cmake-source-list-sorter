package com.github.janmoeller.clioncmakesourcelistsorter.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.NotNull


@State(
    name = "com.github.janmoeller.clioncmakesourcelistsorter.config.AppSettings",
    storages = [Storage("CMakeSourceListSorterPlugin.xml")]
)
internal class AppSettings
    : PersistentStateComponent<AppSettings.State> {
    internal class State {
        var reverse: Boolean = false
    }

    private var myState = State()

    override fun getState(): State {
        return myState
    }

    override fun loadState(@NotNull state: State) {
        myState = state
    }

    companion object {
        val instance: AppSettings
            get() = ApplicationManager.getApplication()
                .getService(AppSettings::class.java)
    }
}