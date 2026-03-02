# Coroutines Flow Educational App

A comprehensive Android educational application demonstrating **Kotlin Coroutines Flow**, with clear explanations of **Hot Streams** vs **Cold Streams** and real-world usage patterns.

## 📱 Overview

This project is designed to teach developers about:
- **Cold Streams** (Flow) - Created per subscriber, ideal for API calls and database queries
- **Hot Streams** (StateFlow, SharedFlow) - Shared among subscribers, ideal for state management
- Flow patterns and best practices
- Common anti-patterns to avoid

## 🎯 Key Features

### Educational Content
- **Flow Patterns**: Four essential patterns for using Flow effectively
- **Anti-Patterns**: Three common mistakes and their corrections
- **Code Examples**: Practical, runnable code snippets for each concept
- **Key Takeaways**: Summary of important Flow principles

### Pattern Coverage

1. **Cold Stream for Network Requests**
   - Fresh API calls per subscription
   - Cancellation-safe operations

2. **Hot Stream for State Management**
   - StateFlow for shared application state
   - Immediate updates to all subscribers

3. **Flow Transformations**
   - map, filter, and operator chains
   - distinctUntilChanged() for efficiency

4. **Combining Multiple Flows**
   - Merge and combine operations
   - Complex data aggregation

### Anti-Patterns Highlighted

- ❌ Blocking operations in Flow
- ❌ Using StateFlow for one-time data
- ❌ Collecting without proper cancellation

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM-ready structure
- **Build Tool**: Gradle

## 📋 Requirements

- Android Studio Panda 1 | 2025.3.1 Patch 1 or later
- Kotlin 1.9+
- Gradle 8.0+
- Minimum SDK: 24

## 🚀 Getting Started

### Clone the Repository
```bash
git clone https://github.com/Szabolcs-Nagy/coroutines-flow-app.git
cd coroutines-flow-app