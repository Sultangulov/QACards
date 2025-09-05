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
		setContent {
			App(repo)
		}
	}
}

@Composable
fun App(repo: CardRepository) {
	val navController = rememberNavController()
	val scope = rememberCoroutineScope()
	val context = LocalContext.current
	var currentCard by remember { mutableStateOf<Card?>(null) }

	MaterialTheme {
		Surface {
			NavHost(navController = navController, startDestination = Routes.HOME) {
				composable(Routes.HOME) {
					HomeScreen(
						onStartStudy = {
							scope.launch {
								currentCard = repo.getNextDue(null)
								navController.navigate(Routes.STUDY)
							}
						},
						onImportFile = { uri ->
							scope.launch {
								try {
									val text = FileImporter.readText(context = context, uri)
									val trimmed = text.trimStart()
									val pairs = try {
										if (trimmed.startsWith("[")) FileImporter.parseJson(text) else FileImporter.parseCsv(text)
									} catch (_: Exception) {
										// Fallback to CSV if JSON parse failed
										FileImporter.parseCsv(text)
									}
									repo.importCards(deckName = "Imported", pairs = pairs)
								} catch (_: Exception) {
									// TODO: surface error to user
								}
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
								currentCard = repo.getNextDue(null)
							}
						},
						onGood = { card ->
							scope.launch {
								repo.grade(card, good = true)
								currentCard = repo.getNextDue(null)
							}
						}
					)
				}
			}
		}
	}
}