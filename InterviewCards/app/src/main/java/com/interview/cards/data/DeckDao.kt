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
}