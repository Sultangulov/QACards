package com.interview.cards.repository

import android.content.Context
import com.interview.cards.data.AppDatabase
import com.interview.cards.data.Card
import com.interview.cards.data.Deck
import com.interview.cards.data.DeckWithDue
import com.interview.cards.importer.FileImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

class CardRepository(context: Context) {
	private val db = AppDatabase.getInstance(context)
	private val deckDao = db.deckDao()
	private val cardDao = db.cardDao()

	suspend fun getDecksWithDue(): List<DeckWithDue> = withContext(Dispatchers.IO) {
		deckDao.getAllWithDue(System.currentTimeMillis())
	}

	suspend fun getOrCreateDeck(name: String): Deck = withContext(Dispatchers.IO) {
		deckDao.findByName(name) ?: Deck(name = name).let { it.copy(id = deckDao.insert(it)) }
	}

	suspend fun importParsedCards(defaultDeckName: String, cards: List<FileImporter.ParsedCard>) = withContext(Dispatchers.IO) {
		val now = System.currentTimeMillis()
		val deckCache = mutableMapOf<String, Deck>()
		val toInsert = cards.map { pc ->
			val deckName = pc.section ?: defaultDeckName
			val deck = deckCache.getOrPut(deckName) { deckDao.findByName(deckName) ?: Deck(name = deckName).let { it.copy(id = deckDao.insert(it)) } }
			Card(
				deckId = deck.id,
				question = pc.question,
				answer = pc.answer,
				section = pc.section,
				examplesJson = pc.examplesJson,
				intervalDays = 0,
				nextReviewAt = now
			)
		}
		cardDao.insertAll(toInsert)
	}

	suspend fun getNextDue(deckId: Long?): Card? = withContext(Dispatchers.IO) {
		val now = System.currentTimeMillis()
		if (deckId != null) cardDao.getNextDueCard(deckId, now) else cardDao.getNextDueCardAll(now)
	}

	suspend fun grade(card: Card, good: Boolean) = withContext(Dispatchers.IO) {
		val newIntervalDays = if (good) nextInterval(card.intervalDays) else 0
		val nextAt = if (good) System.currentTimeMillis() + daysToMillis(newIntervalDays) else System.currentTimeMillis() + 10 * 60 * 1000
		cardDao.update(card.copy(intervalDays = newIntervalDays, nextReviewAt = nextAt))
	}

	private fun nextInterval(current: Int): Int {
		return when (current) {
			0 -> 1
			1 -> 2
			2 -> 4
			else -> max(1, current * 2)
		}
	}

	private fun daysToMillis(days: Int): Long = days.toLong() * 24L * 60L * 60L * 1000L
}