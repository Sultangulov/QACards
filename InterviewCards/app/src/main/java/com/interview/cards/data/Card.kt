package com.interview.cards.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "cards",
	indices = [Index("deckId")]
)
data class Card(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val deckId: Long,
	val question: String,
	val answer: String,
	val section: String? = null,
	val examplesJson: String? = null,
	val intervalDays: Int = 0,
	val nextReviewAt: Long = System.currentTimeMillis()
)