package com.interview.cards.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
	entities = [Deck::class, Card::class],
	version = 2,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun deckDao(): DeckDao
	abstract fun cardDao(): CardDao

	companion object {
		@Volatile
		private var INSTANCE: AppDatabase? = null

		fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
			INSTANCE ?: Room.databaseBuilder(
				context.applicationContext,
				AppDatabase::class.java,
				"interview_cards.db"
			).fallbackToDestructiveMigration().build().also { INSTANCE = it }
		}
	}
}