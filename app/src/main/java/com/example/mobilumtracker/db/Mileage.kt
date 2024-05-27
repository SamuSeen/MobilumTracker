package com.example.mobilumtracker.db
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

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
    fun getMileage(id: Int): Int

    @Update(entity = Mileage::class)
    fun update(mileage: Mileage)

    @Insert
    fun insertAll(vararg mileage: Mileage)

    @Delete
    fun delete(mileage: Mileage)

    @Query("SELECT id FROM mileage ORDER BY id DESC LIMIT 1")
    fun getLastId(): Long

    @Query("UPDATE mileage SET mileage = :mileage WHERE id = :id")
    fun setMileage(id: Int, mileage: Int)
}