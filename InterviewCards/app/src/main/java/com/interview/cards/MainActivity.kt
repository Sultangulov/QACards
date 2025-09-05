package com.interview.cards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.interview.cards.data.Card
import com.interview.cards.data.DeckWithDue
import com.interview.cards.importer.FileImporter
import com.interview.cards.navigation.Routes
import com.interview.cards.repository.CardRepository
import com.interview.cards.ui.HomeScreen
import com.interview.cards.ui.StudyScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
	private lateinit var repo: CardRepository
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		repo = CardRepository(this)
		setContent { App(repo) }
	}
}

@Composable
fun App(repo: CardRepository) {
	val navController = rememberNavController()
	val scope = rememberCoroutineScope()
	val context = LocalContext.current
	var currentCard by remember { mutableStateOf<Card?>(null) }
	var currentDeckId by remember { mutableStateOf<Long?>(null) }
	var decks by remember { mutableStateOf<List<DeckWithDue>>(emptyList()) }

	LaunchedEffect(Unit) {
		decks = repo.getDecksWithDue()
	}

	MaterialTheme {
		Surface {
			NavHost(navController = navController, startDestination = Routes.HOME) {
				composable(Routes.HOME) {
					HomeScreen(
						decks = decks,
						onOpenDeck = { deckId ->
							currentDeckId = deckId
							scope.launch {
								currentCard = repo.getNextDue(deckId)
								navController.navigate(Routes.STUDY)
							}
						},
						onImportFile = { uri ->
							scope.launch {
								try {
									val text = FileImporter.readText(context, uri)
									val parsed = run {
										val trimmed = text.trimStart()
										if (trimmed.startsWith("[")) FileImporter.parseJson(text) else FileImporter.parseCsv(text)
									}
									repo.importParsedCards(defaultDeckName = "Imported", cards = parsed)
									decks = repo.getDecksWithDue()
								} catch (_: Exception) {}
							}
						}
					)
				}
				composable(Routes.STUDY) {
					StudyScreen(
						current = currentCard,
						onAgain = { card ->
							scope.launch {
								repo.grade(card, good = false)
								currentCard = repo.getNextDue(currentDeckId)
							}
						},
						onGood = { card ->
							scope.launch {
								repo.grade(card, good = true)
								currentCard = repo.getNextDue(currentDeckId)
							}
						}
					)
				}
			}
		}
	}
}