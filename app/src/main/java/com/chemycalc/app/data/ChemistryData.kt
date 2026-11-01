package com.chemycalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chemycalc.app.data.ChemistryData
import com.chemycalc.app.ui.theme.ChemyCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChemyCalcTheme {
                ChemyCalcApp()
            }
        }
    }
}

enum class ChemistryScreen {
    HOME,
    ELEMENTS,
    FORMULAS,
    REACTIONS,
    TASKS
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    ChemyCalcTheme {
        ChemyCalcApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemyCalcApp() {
    var selectedScreen by remember { mutableStateOf(ChemistryScreen.HOME) }
    var query by remember { mutableStateOf("") }

    val filteredElements = remember(query) {
        ChemistryData.elements.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.symbol.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
        }
    }

    val filteredFormulas = remember(query) {
        ChemistryData.formulas.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.formula.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
        }
    }

    val filteredReactions = remember(query) {
        ChemistryData.reactions.filter {
            it.name.contains(query, ignoreCase = true) ||
                it.equation.contains(query, ignoreCase = true) ||
                it.explanation.contains(query, ignoreCase = true)
        }
    }

    val filteredTasks = remember(query) {
        ChemistryData.examTasks.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.task.contains(query, ignoreCase = true) ||
                it.answer.contains(query, ignoreCase = true)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderCard()

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск: элемент, формула, реакция, задача") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            )

            ScreenTabs(selectedScreen = selectedScreen, onClick = { selectedScreen = it })

            when (selectedScreen) {
                ChemistryScreen.HOME -> HomeContent()
                ChemistryScreen.ELEMENTS -> ContentList(
                    items = filteredElements,
                    card = { element -> ElementCard(element = element) },
                    emptyText = "Нет подходящих элементов."
                )
                ChemistryScreen.FORMULAS -> ContentList(
                    items = filteredFormulas,
                    card = { formula -> FormulaCard(formula = formula) },
                    emptyText = "Нет подходящих формул."
                )
                ChemistryScreen.REACTIONS -> ContentList(
                    items = filteredReactions,
                    card = { reaction -> ReactionCard(reaction = reaction) },
                    emptyText = "Нет подходящих реакций."
                )
                ChemistryScreen.TASKS -> ContentList(
                    items = filteredTasks,
                    card = { task -> TaskCard(task = task) },
                    emptyText = "Нет подходящих задач."
                )
            }
        }
    }
}

@Composable
fun HeaderCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = "ChemyCalc",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Оффлайн-помощник по химии для 8–11 классов",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}

@Composable
fun ScreenTabs(
    selectedScreen: ChemistryScreen,
    onClick: (ChemistryScreen) -> Unit
) {
    val tabs = listOf(
        "Главная" to ChemistryScreen.HOME,
        "Элементы" to ChemistryScreen.ELEMENTS,
        "Формулы" to ChemistryScreen.FORMULAS,
        "Реакции" to ChemistryScreen.REACTIONS,
        "Задачи" to ChemistryScreen.TASKS
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tabs.forEach { (title, screen) ->
            FilterChip(
                selected = selectedScreen == screen,
                onClick = { onClick(screen) },
                label = { Text(title) }
            )
        }
    }
}

@Composable
fun HomeContent() {
    val topics = ChemistryData.topics
    ContentList(
        items = topics,
        card = { topic -> TopicCard(topic = topic) },
        emptyText = "Темы не найдены."
    )
}

@Composable
private fun <T> ContentList(
    items: List<T>,
    card: @Composable (T) -> Unit,
    emptyText: String
) {
    if (items.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = emptyText,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { item ->
                card(item)
            }
        }
    }
}

@Composable
fun TopicCard(topic: ChemistryData.Topic) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = topic.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = topic.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(text = topic.summary, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                topic.keywords.forEach { keyword ->
                    Card(shape = RoundedCornerShape(50), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Text(
                            text = keyword,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ElementCard(element: ChemistryData.ElementInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = element.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = element.symbol, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            }
            Text(text = "Порядковый номер: ${element.number} | Группа: ${element.group}", style = MaterialTheme.typography.bodyMedium)
            Text(text = element.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FormulaCard(formula: ChemistryData.FormulaEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = formula.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = formula.formula, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Text(text = formula.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ReactionCard(reaction: ChemistryData.ReactionExample) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = reaction.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = reaction.equation, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Text(text = reaction.explanation, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TaskCard(task: ChemistryData.ExamTask) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = task.task, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Ответ: ${task.answer}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}
