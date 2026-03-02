package com.example.coroutinesflowapp

/**
 * COMPREHENSIVE EDUCATIONAL GUIDE: KOTLIN FLOW
 *
 * This document explains Coroutines Flow with emphasis on Cold vs Hot Streams
 *
 * ============================================================================
 * PART 1: WHAT IS KOTLIN FLOW?
 * ============================================================================
 *
 * Kotlin Flow is a reactive stream library built on top of Kotlin coroutines.
 * It provides a way to emit and handle sequences of asynchronous values.
 *
 * Flow is:
 * - Coroutine-based: Built on top of coroutines for efficient async operations
 * - Lazy: Values are only computed when someone is listening
 * - Cancelable: Respects coroutine cancellation tokens
 * - Backpressure-aware: Can handle situations where emitter is faster than consumer
 *
 *
 * ============================================================================
 * PART 2: COLD STREAMS - Regular Flow
 * ============================================================================
 *
 * DEFINITION:
 * A Cold Stream is created fresh for each collector. Each subscriber gets
 * an independent execution of the stream.
 *
 * CHARACTERISTICS:
 *
 * 1. Created on Subscription
 *    - The flow body is executed only when someone calls .collect()
 *    - Before subscription, nothing happens
 *
 * 2. Independent Executions
 *    - Each collector gets their own independent flow execution
 *    - If two collectors subscribe to the same flow, it runs twice
 *
 * 3. Start from Beginning
 *    - Each new subscriber gets all emissions from start to finish
 *    - No values are skipped
 *
 * 4. Lazy Evaluation
 *    - Values are only computed when needed
 *    - Memory efficient
 *
 * EXAMPLE:
 *
 *    fun coldFlow(): Flow<Int> = flow {
 *        repeat(3) { index ->
 *            emit(index + 1)
 *            delay(1000)
 *        }
 *    }
 *
 *    // Collector 1 subscribes at time 0s
 *    coldFlow().collect { value ->
 *        println("Collector 1: $value")  // Prints 1, 2, 3 at 0s, 1s, 2s
 *    }
 *
 *    // Collector 2 subscribes at time 5s
 *    coldFlow().collect { value ->
 *        println("Collector 2: $value")  // Prints 1, 2, 3 at 5s, 6s, 7s
 *    }
 *
 * TIMELINE:
 * ├─ Collector 1 subscribes
 * ├─ 1s: Collector 1 receives 1
 * ├─ 2s: Collector 1 receives 2
 * ├─ 3s: Collector 1 receives 3, Collector 2 subscribes
 * ├─ 4s: Collector 2 receives 1
 * ├─ 5s: Collector 2 receives 2
 * └─ 6s: Collector 2 receives 3
 *
 * BEST USE CASES:
 * - API requests and data fetching
 * - Database queries
 * - One-time operations
 * - When each subscriber needs fresh data
 * - Request-response patterns
 *
 *
 * ============================================================================
 * PART 3: HOT STREAMS - StateFlow and SharedFlow
 * ============================================================================
 *
 * DEFINITION:
 * A Hot Stream is always active and emits values regardless of whether
 * anyone is listening. All subscribers share the same execution.
 *
 *
 * A. StateFlow - The State Holder
 *
 * CHARACTERISTICS:
 *
 * 1. Always Running
 *    - Continues to emit even if no collectors
 *    - Backed by a mutable state
 *
 * 2. Has Current Value
 *    - StateFlow always has a current/latest value
 *    - New subscribers immediately get this value
 *    - Can be accessed via .value property
 *
 * 3. Replays Latest
 *    - Only the most recent value is kept in memory
 *    - New subscribers see the current state
 *    - Old values are discarded
 *
 * 4. Deduplicated
 *    - If the same value is set twice, subscribers are notified only once
 *    - value.value = 5; value.value = 5; -> Only one notification
 *
 * EXAMPLE:
 *
 *    class UserViewModel : ViewModel() {
 *        private val _userState = MutableStateFlow<User?>(null)
 *        val userState: StateFlow<User?> = _userState.asStateFlow()
 *
 *        fun loadUser(id: String) {
 *            viewModelScope.launch {
 *                _userState.value = repository.getUser(id)
 *            }
 *        }
 *    }
 *
 *    // Collector 1 subscribes at time 0s, immediately gets current value (null)
 *    viewModel.userState.collect { user ->
 *        println("Collector 1: $user")
 *    }
 *
 *    // At time 1s, user is loaded
 *    // Both collectors immediately see the new user
 *
 *    // At time 2s, Collector 2 subscribes
 *    // Immediately gets the loaded user (doesn't wait for new data)
 *    viewModel.userState.collect { user ->
 *        println("Collector 2: $user")
 *    }
 *
 * TIMELINE:
 * ├─ Collector 1 subscribes, gets current value (null)
 * ├─ 1s: User loaded, both collectors get the new user
 * └─ 2s: Collector 2 subscribes, gets current value (user)
 *
 *
 * B. SharedFlow - The Event Broadcaster
 *
 * CHARACTERISTICS:
 *
 * 1. Event Broadcasting
 *    - Designed for sending events to multiple subscribers
 *    - No inherent "current value"
 *
 * 2. No Value Retention (by default)
 *    - New subscribers don't get past emissions
 *    - Values are lost if no one is listening
 *
 * 3. Can Have Replay
 *    - Can be configured to keep last N values
 *    - New subscribers get the replayed values
 *
 * 4. Multiple Emitters
 *    - Can have multiple sources emitting to it
 *
 * EXAMPLE (Without Replay):
 *
 *    private val _eventFlow = MutableSharedFlow<String>()
 *    val eventFlow = _eventFlow.asSharedFlow()
 *
 *    // Collector 1 subscribes
 *    eventFlow.collect { event ->
 *        println("Collector 1: $event")
 *    }
 *
 *    // Event emitted, Collector 1 receives it
 *    _eventFlow.emit("Event A")  // Collector 1 receives
 *
 *    // Collector 2 subscribes AFTER Event A
 *    eventFlow.collect { event ->
 *        println("Collector 2: $event")
 *    }
 *
 *    // Event emitted, both collectors receive it
 *    _eventFlow.emit("Event B")  // Both receive
 *
 * EXAMPLE (With Replay):
 *
 *    private val _eventFlow = MutableSharedFlow<String>(replay = 3)
 *    val eventFlow = _eventFlow.asSharedFlow()
 *
 *    _eventFlow.emit("Event 1")
 *    _eventFlow.emit("Event 2")
 *    _eventFlow.emit("Event 3")
 *    _eventFlow.emit("Event 4")  // Event 1 is now lost
 *
 *    // New subscriber gets last 3: Event 2, Event 3, Event 4
 *    eventFlow.collect { event ->
 *        println("Collector: $event")  // Gets 2, 3, 4 immediately
 *    }
 *
 *
 * ============================================================================
 * PART 4: DETAILED COMPARISON TABLE
 * ============================================================================
 *
 * ASPECT              | COLD STREAM (Flow) | HOT STREAM (StateFlow) | HOT STREAM (SharedFlow)
 * ─────────────────────────────────────────────────────────────────────────────
 * Execution           | On subscription    | Always active          | Always active
 * Memory per instance | One per collector  | Shared                 | Shared
 * Start Point         | From beginning     | From current           | From current
 * Values Emitted      | Only if collecting | Always                 | Always
 * Current Value       | No                 | Yes                    | No (unless replayed)
 * Best For            | API calls          | UI state               | Events
 * Deduplication       | No (emits all)     | Yes (duplicate values) | No
 * Multiple Emitters   | No                 | No                     | Yes
 * Replay Capability   | No                 | Full (always current)  | Optional
 *
 *
 * ============================================================================
 * PART 5: WHEN TO USE WHAT?
 * ============================================================================
 *
 * USE COLD STREAM (Flow) WHEN:
 * ✓ Fetching data from a server (each request should be fresh)
 * ✓ Querying a database (each query independent)
 * ✓ Performing one-time computations
 * ✓ Each collector needs independent execution
 * ✓ You don't need a "current value"
 * ✓ Data doesn't need to be shared among observers
 *
 * USE StateFlow WHEN:
 * ✓ Managing UI state (user info, theme, settings)
 * ✓ Tracking current application state
 * ✓ New subscribers need the latest state immediately
 * ✓ You have a "current value" that observers care about
 * ✓ Multiple UI components need the same state
 * ✓ State changes should immediately update all observers
 *
 * USE SharedFlow WHEN:
 * ✓ Broadcasting events (navigation, notifications)
 * ✓ Multiple sources emit to same receiver
 * ✓ One-time events that don't need a "current value"
 * ✓ You want to replay recent events to new subscribers (with replay > 0)
 * ✓ Pub-sub pattern communication
 *
 *
 * ============================================================================
 * PART 6: CODE PATTERNS & BEST PRACTICES
 * ============================================================================
 *
 * PATTERN 1: Cold Stream for API
 *
 *    fun getUserFromAPI(id: String): Flow<User> = flow {
 *        val user = apiClient.fetchUser(id)
 *        emit(user)
 *    }
 *
 *    // Usage
 *    getUserFromAPI("123").collect { user ->
 *        updateUI(user)
 *    }
 *
 *
 * PATTERN 2: StateFlow for UI State
 *
 *    class CounterViewModel : ViewModel() {
 *        private val _count = MutableStateFlow(0)
 *        val count: StateFlow<Int> = _count.asStateFlow()
 *
 *        fun increment() {
 *            _count.value++
 *        }
 *    }
 *
 *
 * PATTERN 3: Combining Flows
 *
 *    val userWithPosts: Flow<Pair<User, List<Post>>> =
 *        flow1.combine(flow2) { user, posts ->
 *            Pair(user, posts)
 *        }
 *
 *
 * PATTERN 4: Transforming with Operators
 *
 *    coldFlow
 *        .filter { it > 0 }
 *        .map { it * 2 }
 *        .distinctUntilChanged()
 *        .collect { value ->
 *            println(value)
 *        }
 *
 *
 * ============================================================================
 * PART 7: COMMON MISTAKES TO AVOID
 * ============================================================================
 *
 * MISTAKE 1: Using StateFlow for one-time events
 *    ❌ WRONG: private val _navigation = MutableStateFlow<Event?>(null)
 *    ✅ RIGHT: private val _navigation = MutableSharedFlow<Event>()
 *
 * MISTAKE 2: Blocking operations in Flow
 *    ❌ WRONG: flow { Thread.sleep(1000); emit(x) }
 *    ✅ RIGHT: flow { delay(1000); emit(x) }
 *
 * MISTAKE 3: Collecting without cancellation scope
 *    ❌ WRONG: GlobalScope.launch { flow.collect { } }
 *    ✅ RIGHT: lifecycleScope.launch { flow.collect { } }
 *
 * MISTAKE 4: Not understanding cold vs hot
 *    ❌ WRONG: Expecting fresh data from cached Flow
 *    ✅ RIGHT: Use Cold Flow for fresh data, Hot Streams for state
 *
 * MISTAKE 5: Ignoring backpressure
 *    ❌ WRONG: Emitting faster than consumer can handle
 *    ✅ RIGHT: Use proper coroutine coordination
 *
 *
 * ============================================================================
 * SUMMARY
 * ============================================================================
 *
 * 1. Cold Streams (Flow):
 *    - Created per subscriber
 *    - Independent executions
 *    - Perfect for API calls and one-time data
 *
 * 2. Hot Streams (StateFlow):
 *    - Always running
 *    - Holds latest state
 *    - Perfect for UI state management
 *
 * 3. Hot Streams (SharedFlow):
 *    - Always running
 *    - Broadcasts events
 *    - Perfect for event communication
 *
 * 4. Choose wisely:
 *    - API calls? → Cold Flow
 *    - UI State? → StateFlow
 *    - Events? → SharedFlow
 *
 * 5. Remember:
 *    - Always use proper scopes
 *    - Use suspending functions (delay, not Thread.sleep)
 *    - Understand your data lifecycle
 *    - Test with collectors to understand behavior
 */

