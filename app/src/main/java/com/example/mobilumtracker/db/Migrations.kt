package com.example.mobilumtracker.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_43_44 = object : Migration(43, 44) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `Event_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Event` TEXT NOT NULL, `Days` INTEGER NOT NULL, `LastDate` TEXT NOT NULL, `Distance` INTEGER NOT NULL, `LastDistance` INTEGER NOT NULL, `Description` TEXT NOT NULL)")
        db.execSQL("INSERT INTO `Event_new` (`id`, `Event`, `Days`, `LastDate`, `Distance`, `LastDistance`, `Description`) SELECT `id`, `Event`, `Days`, `LastTime`, `Distance`, `LastDistance`, `Description` FROM `Event`")
        db.execSQL("DROP TABLE IF EXISTS `Event`")
        db.execSQL("ALTER TABLE `Event_new` RENAME TO `Event`")
    }
}