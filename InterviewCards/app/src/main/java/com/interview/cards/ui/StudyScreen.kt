package com.interview.cards.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.interview.cards.data.Card as CardEntity
import org.json.JSONArray

@Composable
fun StudyScreen(
	current: CardEntity?,
	onAgain: (CardEntity) -> Unit,
	onGood: (CardEntity) -> Unit
) {
	var showAnswer by remember { mutableStateOf(false) }
	Column(
		modifier = Modifier.fillMaxSize().padding(20.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		if (current == null) {
			Text("Нет карточек для повторения")
		} else {
			current.section?.let { Text(it, style = MaterialTheme.typography.labelLarge) }
			ElevatedCard(modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.clickable { showAnswer = !showAnswer }
			) {
				Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
					Text(if (showAnswer) current.answer else current.question, style = MaterialTheme.typography.titleMedium)
				}
			}

			if (showAnswer && !current.examplesJson.isNullOrBlank()) {
				ExamplesList(json = current.examplesJson!!)
			}

			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				Button(onClick = { onAgain(current); showAnswer = false }, modifier = Modifier.weight(1f)) { Text("Again") }
				Button(onClick = { onGood(current); showAnswer = false }, modifier = Modifier.weight(1f)) { Text("Good") }
			}
		}
	}
}

@Composable
private fun ExamplesList(json: String) {
	val arr = remember(json) { runCatching { JSONArray(json) }.getOrNull() }
	if (arr != null && arr.length() > 0) {
		Column(
			modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text("Примеры:", style = MaterialTheme.typography.titleSmall)
			for (i in 0 until arr.length()) {
				Text("• " + arr.optString(i), style = MaterialTheme.typography.bodyMedium)
			}
		}
	}
}