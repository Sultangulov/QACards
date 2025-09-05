package com.interview.cards.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.interview.cards.data.Card as CardEntity

@Composable
fun StudyScreen(
	current: CardEntity?,
	onAgain: (CardEntity) -> Unit,
	onGood: (CardEntity) -> Unit
) {
	var showAnswer by remember { mutableStateOf(false) }
	Column(
		modifier = Modifier.fillMaxSize().padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		if (current == null) {
			Text("Нет карточек для повторения")
		} else {
			Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier
				.padding(8.dp)
				.clickable { showAnswer = !showAnswer }
			) {
				Text(
					text = if (showAnswer) current.answer else current.question,
					modifier = Modifier.padding(24.dp)
				)
			}
			Spacer(modifier = Modifier.height(12.dp))
			if (showAnswer) {
				RowActions(
					onAgain = { onAgain(current); showAnswer = false },
					onGood = { onGood(current); showAnswer = false }
				)
			} else {
				Text("Нажмите на карточку, чтобы увидеть ответ")
			}
		}
	}
}

@Composable
private fun RowActions(onAgain: () -> Unit, onGood: () -> Unit) {
	Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Button(onClick = onAgain) { Text("Again") }
		Button(onClick = onGood) { Text("Good") }
	}
}