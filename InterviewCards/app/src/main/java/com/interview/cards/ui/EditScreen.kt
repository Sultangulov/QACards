package com.interview.cards.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.interview.cards.data.Card as CardEntity

@Composable
fun EditScreen(card: CardEntity, onSave: (CardEntity) -> Unit) {
	var q by remember { mutableStateOf(card.question) }
	var a by remember { mutableStateOf(card.answer) }
	Column(
		modifier = Modifier.fillMaxSize().padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		OutlinedTextField(value = q, onValueChange = { q = it }, label = { Text("Вопрос") })
		OutlinedTextField(value = a, onValueChange = { a = it }, label = { Text("Ответ") })
		Button(onClick = { onSave(card.copy(question = q.trim(), answer = a.trim())) }) { Text("Сохранить") }
	}
}