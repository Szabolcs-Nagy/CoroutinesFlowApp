package com.example.coroutinesflowapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Comprehensive educational content about Flow patterns and best practices
 */
@Composable
fun FlowPatternsScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Flow Patterns & Best Practices",
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PatternCard(
            title = "Pattern 1: Cold Stream for Network Requests",
            description = "Use regular Flow for API calls and database queries",
            code = """
                fun getUserData(userId: String): Flow<User> = flow {
                    val user = apiService.getUser(userId)
                    emit(user)
                }
                
                // Each subscription makes a fresh API call
                getUserData("123").collect { user ->
                    println("User: $${'$'}user")
                }
            """.trimIndent(),
            benefits = listOf(
                "Fresh data on each request",
                "No unnecessary API calls",
                "Cancellation safe"
            )
        )

        PatternCard(
            title = "Pattern 2: Hot Stream for State Management",
            description = "Use StateFlow for application state that changes over time",
            code = """
                class UserViewModel : ViewModel() {
                    private val _userState = MutableStateFlow<User?>(null)
                    val userState: StateFlow<User?> = _userState.asStateFlow()
                    
                    fun updateUser(user: User) {
                        _userState.value = user
                    }
                }
                
                // All collectors see the latest state
                viewModel.userState.collect { user ->
                    updateUI(user)
                }
            """.trimIndent(),
            benefits = listOf(
                "Shared state among observers",
                "Immediate updates to all subscribers",
                "Memory efficient"
            )
        )

        PatternCard(
            title = "Pattern 3: Transforming Flows with Operators",
            description = "Use map, filter, and other operators to transform data",
            code = """
                coldFlow
                    .filter { it > 0 }
                    .map { it * 2 }
                    .collect { value ->
                        println("Transformed: $${'$'}value")
                    }
                
                stateFlow
                    .map { it.userName }
                    .distinctUntilChanged()
                    .collect { name ->
                        println("User name: $${'$'}name")
                    }
            """.trimIndent(),
            benefits = listOf(
                "Composable transformations",
                "Efficient filtering and mapping",
                "Lazy evaluation"
            )
        )

        PatternCard(
            title = "Pattern 4: Combining Multiple Flows",
            description = "Merge or combine multiple flows for complex scenarios",
            code = """
                val combined = flow1
                    .combine(flow2) { value1, value2 ->
                        Pair(value1, value2)
                    }
                
                val merged = merge(flow1, flow2, flow3)
                
                combined.collect { (val1, val2) ->
                    println("Combined: $${'$'}val1, $${'$'}val2")
                }
            """.trimIndent(),
            benefits = listOf(
                "Coordinate multiple streams",
                "Complex data aggregation",
                "Type-safe combinations"
            )
        )

        AntiPatternCard(
            title = "❌ Anti-Pattern 1: Blocking in Flow",
            description = "Don't use blocking operations in Flow",
            code = """
                // WRONG - Don't do this!
                flow {
                    val result = Thread.sleep(1000) // Blocking!
                    emit(result)
                }.collect { }
                
                // CORRECT - Use suspending functions
                flow {
                    delay(1000) // Non-blocking suspension
                    emit(result)
                }.collect { }
            """.trimIndent()
        )

        AntiPatternCard(
            title = "❌ Anti-Pattern 2: StateFlow for One-Time Data",
            description = "Don't use StateFlow for data that should only be emitted once",
            code = """
                // WRONG - Using StateFlow for navigation
                private val _navigation = MutableStateFlow<Event?>(null)
                
                // CORRECT - Use SharedFlow or sealed state
                private val _navigation = MutableSharedFlow<NavigationEvent>()
                
                // Or wrap in sealed state
                private val _uiState = MutableStateFlow<UiState>(
                    UiState.Idle
                )
            """.trimIndent()
        )

        AntiPatternCard(
            title = "❌ Anti-Pattern 3: Collecting Without Cancellation",
            description = "Always cancel flows when scope ends",
            code = """
                // WRONG - Potential memory leak
                GlobalScope.launch {
                    flow.collect { value ->
                        println(value)
                    }
                }
                
                // CORRECT - Use lifecycle-aware scope
                viewLifecycleOwner.lifecycleScope.launch {
                    flow.collect { value ->
                        println(value)
                    }
                }
            """.trimIndent()
        )

        KeyTakeawaysCard()
    }
}

@Composable
private fun PatternCard(
    title: String,
    description: String,
    code: String,
    benefits: List<String>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Divider()
            CodeBlock(code)
            Divider()
            Text(
                text = "Benefits:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            benefits.forEach { benefit ->
                BulletText(benefit)
            }
        }
    }
}

@Composable
private fun AntiPatternCard(
    title: String,
    description: String,
    code: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Divider()
            CodeBlock(code)
        }
    }
}

@Composable
private fun CodeBlock(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF263238), RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Text(
            text = code,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = Color(0xFF81C784)
        )
    }
}

@Composable
private fun BulletText(text: String) {
    Text(
        text = "✓ $text",
        fontSize = 12.sp,
        color = Color(0xFF2E7D32)
    )
}

@Composable
private fun KeyTakeawaysCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3E5F5), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🎯 Key Takeaways",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A)
            )
            Divider()
            BulletText("Cold Streams (Flow) are created per subscriber - ideal for API calls")
            BulletText("Hot Streams (StateFlow) are shared - ideal for state management")
            BulletText("Always cancel flows with appropriate scope")
            BulletText("Use operators like map, filter, combine for transformations")
            BulletText("Avoid blocking operations; use suspending functions instead")
            BulletText("StateFlow is best for UI state, SharedFlow for event broadcasting")
            BulletText("Use distinctUntilChanged() to avoid duplicate emissions")
            BulletText("Test flows using virtual time with turbine")
        }
    }
}

