package com.interview.cards.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeckDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(deck: Deck): Long

	@Query("SELECT * FROM decks ORDER BY createdAt DESC")
	suspend fun getAll(): List<Deck>

	@Query("SELECT * FROM decks WHERE name = :name LIMIT 1")
	suspend fun findByName(name: String): Deck?

	@Query(
		"SELECT d.*, (SELECT COUNT(*) FROM cards c WHERE c.deckId = d.id AND c.nextReviewAt <= :now) AS dueCount FROM decks d ORDER BY d.createdAt DESC"
	)
	suspend fun getAllWithDue(now: Long): List<DeckWithDue>
}

data class DeckWithDue(
	val id: Long,
	val name: String,
	val createdAt: Long,
	val dueCount: Int
)