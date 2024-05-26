# CMake Source List Sorter

![Build](https://github.com/jan-moeller/clion-cmake-source-list-sorter/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.github.janmoeller.clioncmakesourcelistsorter.svg)](https://plugins.jetbrains.com/plugin/com.github.janmoeller.clioncmakesourcelistsorter)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.github.janmoeller.clioncmakesourcelistsorter.svg)](https://plugins.jetbrains.com/plugin/com.github.janmoeller.clioncmakesourcelistsorter)

## Summary

<!-- Plugin description -->
This is a simple plugin that sorts your CMake source file lists when you format a CMake file.

For example, this plugin will turn

```cmake
add_executable(target EXCLUDE_FROM_ALL src/main.cpp src/other.cpp include/other.h)
```

into

```cmake
add_executable(target EXCLUDE_FROM_ALL include/other.h src/main.cpp src/other.cpp)
```

This currently works for:

- [add_executable](https://cmake.org/cmake/help/latest/command/add_executable.html)
- [add_library](https://cmake.org/cmake/help/latest/command/add_library.html)
- [target_sources](https://cmake.org/cmake/help/latest/command/target_sources.html)

White space is left untouched.
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "CMake Source List
  Sorter"</kbd> >
  <kbd>Install</kbd>

- Manually:

  Download the [latest release](https://github.com/jan-moeller/clion-cmake-source-list-sorter/releases/latest) and
  install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template

[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
