package com.example.mobilumtracker.db
import androidx.room.*

@Entity
data class Events(
    @ColumnInfo(name = "Event") val event: String,
    @ColumnInfo(name = "Time") val time: String,
    @ColumnInfo(name = "Distance") val distance: Int,
    @ColumnInfo(name = "Description") val description: String,
    @PrimaryKey(autoGenerate = true) val id: Long=0
)
@Dao
interface EventsDao {
    @Query("SELECT * FROM events")
    fun getAll(): List<Mileage>

    @Query("SELECT * FROM events " +
            "WHERE id IN (:credIds)"
    )
    fun loadAllByIds(credIds: LongArray): List<Mileage>?

    @Query("SELECT * FROM events " +
            "WHERE id = :id")
    fun findById(id: Long): Mileage?

    @Update(entity = Mileage::class)
    fun update(user: Mileage)

    @Insert
    fun insertAll(vararg users: Events)

    @Delete
    fun delete(user: Mileage)
    @Query("SELECT id FROM events ORDER BY id DESC LIMIT 1")
    fun getLastId(): Long
}