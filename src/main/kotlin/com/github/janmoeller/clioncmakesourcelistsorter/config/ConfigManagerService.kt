package com.github.janmoeller.clioncmakesourcelistsorter.config

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project

class MyState : BaseState() {
    var case_sensitive = true
    var increasing = true
}

@Service(Service.Level.PROJECT)
@State(name = "cmake-source-list-sorter-config")
class ConfigManagerService(private val project: Project) : SimplePersistentStateComponent<MyState>(MyState()) {
}


