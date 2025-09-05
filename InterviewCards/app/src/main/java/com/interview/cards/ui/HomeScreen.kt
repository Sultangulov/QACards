package com.interview.cards.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.interview.cards.data.DeckWithDue

@Composable
fun HomeScreen(
	decks: List<DeckWithDue>,
	onOpenDeck: (Long) -> Unit,
	onImportFile: (Uri) -> Unit
) {
	var lastImportOk by remember { mutableStateOf(false) }
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.OpenDocument(),
		onResult = { uri -> if (uri != null) { onImportFile(uri); lastImportOk = true } }
	)

	Scaffold(
		topBar = {
			TopAppBar(title = { Text("InterviewCards") })
		},
		floatingActionButton = {
			ExtendedFloatingActionButton(onClick = { launcher.launch(arrayOf("application/json", "text/*")) }) {
				Text("Импорт")
			}
		}
	) { padding ->
		if (decks.isEmpty()) {
			Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
				Text("Нет колод. Нажмите Импорт.")
			}
		} else {
			LazyColumn(
				modifier = Modifier.fillMaxSize().padding(padding),
				verticalArrangement = Arrangement.spacedBy(8.dp),
				contentPadding = PaddingValues(16.dp)
			) {
				items(decks) { deck ->
					ElevatedCard(
						modifier = Modifier.fillMaxWidth().clickable { onOpenDeck(deck.id) }
					) {
						Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
							Column(modifier = Modifier.weight(1f)) {
								Text(deck.name, style = MaterialTheme.typography.titleMedium)
								Text("К повторению: ${deck.dueCount}", style = MaterialTheme.typography.bodyMedium)
							}
							AssistChip(onClick = { onOpenDeck(deck.id) }, label = { Text("Учить") })
						}
					}
				}
			}
		}
	}
}