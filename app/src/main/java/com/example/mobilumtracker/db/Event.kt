package com.example.mobilumtracker.db
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

/**
 * Table for events
 */
@Entity
data class Event(
    @ColumnInfo(name = "Event") val event: String,
    @ColumnInfo(name = "Days") val days: Int,
    @ColumnInfo(name = "LastDate") val lastDate: String,
    @ColumnInfo(name = "Distance") val distance: Int,
    @ColumnInfo(name = "LastDistance") val lastDistance: Int,
    @ColumnInfo(name = "Description") val description: String,
    @PrimaryKey(autoGenerate = true) val id: Long=0
)
@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll(): List<Event>

    @Query("SELECT * FROM event WHERE id IN (:credIds)"
    )
    fun loadAllByIds(credIds: LongArray): List<Event>?

    @Query("SELECT * FROM event WHERE id = :id")
    fun findById(id: Long): Event?

    @Update(entity = Event::class)
    fun update(event: Event)

    @Insert
    fun insertAll(vararg events: Event)

    @Delete
    fun delete(event: Event)
    @Query("SELECT id FROM event ORDER BY id DESC LIMIT 1")
    fun getLastId(): Long

    @Query("SELECT * FROM event")
    fun findAll(): List<Event>
}