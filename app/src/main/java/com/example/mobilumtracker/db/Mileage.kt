package com.example.mobilumtracker.db
import androidx.room.*

@Entity
data class Mileage(
    @ColumnInfo(name = "Mileage") val mileage: Int,
    @PrimaryKey(autoGenerate = true) val id: Short=0
)
@Dao
interface MileageDao {
    @Query("SELECT * FROM mileage")
    fun getAll(): List<Mileage>

    @Query("SELECT mileage FROM mileage WHERE id = :id")
    fun getMileage(id: Short): Int

    @Update(entity = Mileage::class)
    fun update(user: Mileage)

    @Insert
    fun insertAll(vararg users: Mileage)

    @Delete
    fun delete(user: Mileage)
    @Query("SELECT id FROM mileage ORDER BY id DESC LIMIT 1")
    fun getLastId(): Long
}