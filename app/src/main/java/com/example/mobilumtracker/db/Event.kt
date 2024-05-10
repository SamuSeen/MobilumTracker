package com.example.mobilumtracker.db
import androidx.room.*

@Entity
data class Event(
    @ColumnInfo(name = "Event") val event: String,
    @ColumnInfo(name = "Days") val days: Int,
    @ColumnInfo(name = "LastTime") val lastTime: String,
    @ColumnInfo(name = "Distance") val distance: Int,
    @ColumnInfo(name = "LastDistance") val lastDistance: Int,
    @ColumnInfo(name = "Description") val description: String,
    @PrimaryKey(autoGenerate = true) val id: Long=0
)
@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll(): List<Mileage>

    @Query("SELECT * FROM event " +
            "WHERE id IN (:credIds)"
    )
    fun loadAllByIds(credIds: LongArray): List<Event>?

    @Query("SELECT * FROM event " +
            "WHERE id = :id")
    fun findById(id: Long): Event?

    @Update(entity = Event::class)
    fun update(event: Event)

    @Insert
    fun insertAll(vararg users: Event)

    @Delete
    fun delete(event: Event)
    @Query("SELECT id FROM event ORDER BY id DESC LIMIT 1")
    fun getLastId(): Long

    @Query("SELECT * FROM event")
    fun findAll(): List<Event>
}