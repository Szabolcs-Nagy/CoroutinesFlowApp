package com.example.coroutinesflowapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Educational UI Screen for Coroutines Flow
 * Demonstrates Cold Streams vs Hot Streams with interactive examples
 */

@Composable
fun EducationalFlowScreen(viewModel: FlowViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val count by viewModel.countStateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Header
        Text(
            text = "Kotlin Flow: Educational Guide",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Understanding Cold Streams vs Hot Streams",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Concept Overview Section
        ConceptOverviewCard()
        Spacer(modifier = Modifier.height(16.dp))

        // Cold Stream Section
        ColdStreamSection(viewModel)
        Spacer(modifier = Modifier.height(16.dp))

        // Hot Stream Section
        HotStreamSection(viewModel, count)
        Spacer(modifier = Modifier.height(16.dp))

        // Comparison Section
        ComparisonCard()
        Spacer(modifier = Modifier.height(16.dp))

        // Use Cases Section
        UseCasesCard()
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ConceptOverviewCard() {
    Card(title = "What is Kotlin Flow?") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BulletPoint("Kotlin Flow is a coroutine-based solution for async data streams")
            BulletPoint("It provides a reactive programming model for handling sequences of values")
            BulletPoint("Two main types: Cold Streams (Flow) and Hot Streams (StateFlow, SharedFlow)")
            BulletPoint("Designed to be memory efficient and cancellation-aware")
        }
    }
}

@Composable
private fun ColdStreamSection(viewModel: FlowViewModel) {
    Card(title = "🔵 Cold Streams (Regular Flow)") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Definition
            ConceptBox(
                title = "Definition",
                description = "Each collector gets an independent execution, starting from the beginning"
            )

            // Characteristics
            Text(
                text = "Characteristics:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            BulletPoint("Created on subscription (lazy)")
            BulletPoint("Each collector triggers a new flow execution")
            BulletPoint("No value is emitted if no one is collecting")
            BulletPoint("New subscribers don't receive past values")

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Code Example
            CodeExample(
                title = "Example Code",
                code = """
                    fun createColdFlow(): Flow<Int> = flow {
                        repeat(5) { index ->
                            emit(index + 1)
                            delay(1000)
                        }
                    }
                    
                    // Collector 1
                    coldFlow.collect { value ->
                        println("Collector 1: $${'$'}value")
                    }
                    
                    // Collector 2 - Gets sequence again!
                    coldFlow.collect { value ->
                        println("Collector 2: $${'$'}value")
                    }
                """.trimIndent()
            )
        }
    }
}

@Composable
private fun HotStreamSection(viewModel: FlowViewModel, count: Int) {
    Card(title = "🔴 Hot Streams (StateFlow/SharedFlow)") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Definition
            ConceptBox(
                title = "Definition",
                description = "Always active, shared among all collectors, emit independently of observers"
            )

            // Characteristics
            Text(
                text = "Characteristics:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            BulletPoint("Always running, regardless of collectors")
            BulletPoint("All collectors share the same flow execution")
            BulletPoint("Values emit even if no one is listening")
            BulletPoint("New subscribers may miss past values (unless replayed)")

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // StateFlow Example
            StateFlowExampleBox(viewModel, count)

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Code Example
            CodeExample(
                title = "Example Code",
                code = """
                    private val _count = MutableStateFlow(0)
                    val count: StateFlow<Int> = _count.asStateFlow()
                    
                    // All collectors see same value immediately
                    count.collect { value ->
                        println("Current count: $${'$'}value")
                    }
                    
                    // Update triggers all collectors
                    _count.value = 5
                """.trimIndent()
            )
        }
    }
}

@Composable
private fun StateFlowExampleBox(viewModel: FlowViewModel, count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Live Demo: StateFlow Counter",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Current Count: $count",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.incrementCount() },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Text("Increment (+1)", fontSize = 12.sp)
                }
                Button(
                    onClick = { viewModel.startAutoEmit() },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Auto Emit", fontSize = 12.sp)
                }
            }
            Text(
                text = "Notice: All observers immediately see the updated value!",
                fontSize = 12.sp,
                color = Color.Gray,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
private fun ComparisonCard() {
    Card(title = "📊 Cold vs Hot: Side-by-Side Comparison") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ComparisonRow("Aspect", "Cold Stream (Flow)", "Hot Stream (StateFlow/SharedFlow)")
            Divider()
            ComparisonRow("Execution", "On subscription", "Always active")
            Divider()
            ComparisonRow("Memory", "One per collector", "Shared among all")
            Divider()
            ComparisonRow("Start Point", "From beginning", "From current position")
            Divider()
            ComparisonRow("Values Emitted", "Only if collecting", "Always")
            Divider()
            ComparisonRow("Use Case", "Data fetching, one-time", "State management, events")
        }
    }
}

@Composable
private fun ComparisonRow(aspect: String, cold: String, hot: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = aspect,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.2f),
            fontSize = 12.sp
        )
        Text(
            text = cold,
            modifier = Modifier.weight(1.4f),
            fontSize = 12.sp,
            color = Color(0xFF0066CC)
        )
        Text(
            text = hot,
            modifier = Modifier.weight(1.4f),
            fontSize = 12.sp,
            color = Color(0xFFCC0000)
        )
    }
}

@Composable
private fun UseCasesCard() {
    Card(title = "💡 Practical Use Cases") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UseCase(
                type = "Cold Stream (Flow)",
                examples = listOf(
                    "Fetching data from API",
                    "Database queries",
                    "File I/O operations",
                    "One-time computations",
                    "Request-response patterns"
                )
            )
            Divider()
            UseCase(
                type = "Hot Stream (StateFlow)",
                examples = listOf(
                    "UI state management",
                    "User location tracking",
                    "Current user session",
                    "Application theme",
                    "Real-time notifications"
                )
            )
            Divider()
            UseCase(
                type = "Hot Stream (SharedFlow)",
                examples = listOf(
                    "Event broadcasting",
                    "User interactions",
                    "System events",
                    "Multi-observer patterns",
                    "Pub-sub communication"
                )
            )
        }
    }
}

@Composable
private fun UseCase(type: String, examples: List<String>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = type,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        examples.forEach { example ->
            BulletPoint(example, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ConceptBox(title: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF1976D2),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun CodeExample(title: String, code: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
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
}

@Composable
private fun Card(title: String, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

@Composable
private fun BulletPoint(text: String, fontSize: Int = 13) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "•",
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )
        Text(
            text = text,
            fontSize = fontSize.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

