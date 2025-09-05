package com.interview.cards.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CardDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(cards: List<Card>)

	@Insert
	suspend fun insert(card: Card): Long

	@Update
	suspend fun update(card: Card)

	@Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReviewAt <= :now ORDER BY nextReviewAt LIMIT 1")
	suspend fun getNextDueCard(deckId: Long, now: Long): Card?

	@Query("SELECT * FROM cards WHERE nextReviewAt <= :now ORDER BY nextReviewAt LIMIT 1")
	suspend fun getNextDueCardAll(now: Long): Card?

	@Query("SELECT COUNT(*) FROM cards WHERE deckId = :deckId AND nextReviewAt <= :now")
	suspend fun dueCount(deckId: Long, now: Long): Int
}