package com.example.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "leads")
data class Lead(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val address: String,
    val phone: String = "",
    val website: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val notes: String = "",
    val status: String = "NEW", // "NEW", "CONTACTED", "FOLLOW_UP", "CONVERTED", "LOST"
    val revenueEstimate: Double = 500.0, // default $500 potential value
    val mapClicksCall: Int = 0,
    val mapClicksWebsite: Int = 0,
    val mapClicksDirections: Int = 0,
    val lastInteractionTime: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY timestamp DESC")
    fun getAllLeads(): Flow<List<Lead>>

    @Query("SELECT * FROM leads WHERE id = :id LIMIT 1")
    suspend fun getLeadById(id: Int): Lead?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: Lead): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLeads(leads: List<Lead>)

    @Update
    suspend fun updateLead(lead: Lead)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLeadById(id: Int)

    @Query("DELETE FROM leads")
    suspend fun deleteAllLeads()
}

@Database(entities = [Lead::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leadDao(): LeadDao
}
