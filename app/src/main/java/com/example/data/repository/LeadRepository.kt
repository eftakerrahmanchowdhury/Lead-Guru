package com.example.data.repository

import com.example.data.database.Lead
import com.example.data.database.LeadDao
import kotlinx.coroutines.flow.Flow

class LeadRepository(private val leadDao: LeadDao) {

    val allLeads: Flow<List<Lead>> = leadDao.getAllLeads()

    suspend fun getLeadById(id: Int): Lead? {
        return leadDao.getLeadById(id)
    }

    suspend fun insertLead(lead: Lead): Long {
        return leadDao.insertLead(lead)
    }

    suspend fun insertLeads(leads: List<Lead>) {
        leadDao.insertLeads(leads)
    }

    suspend fun updateLead(lead: Lead) {
        leadDao.updateLead(lead)
    }

    suspend fun deleteLeadById(id: Int) {
        leadDao.deleteLeadById(id)
    }

    suspend fun deleteAllLeads() {
        leadDao.deleteAllLeads()
    }

    suspend fun incrementInteraction(lead: Lead, type: String) {
        val updatedLead = when (type) {
            "CALL" -> lead.copy(
                mapClicksCall = lead.mapClicksCall + 1,
                lastInteractionTime = System.currentTimeMillis()
            )
            "WEBSITE" -> lead.copy(
                mapClicksWebsite = lead.mapClicksWebsite + 1,
                lastInteractionTime = System.currentTimeMillis()
            )
            "DIRECTIONS" -> lead.copy(
                mapClicksDirections = lead.mapClicksDirections + 1,
                lastInteractionTime = System.currentTimeMillis()
            )
            else -> lead
        }
        leadDao.updateLead(updatedLead)
    }

    suspend fun updateLeadStatus(lead: Lead, newStatus: String) {
        val updatedLead = lead.copy(
            status = newStatus,
            lastInteractionTime = System.currentTimeMillis()
        )
        leadDao.updateLead(updatedLead)
    }

    suspend fun updateLeadNotes(lead: Lead, newNotes: String) {
        val updatedLead = lead.copy(
            notes = newNotes,
            lastInteractionTime = System.currentTimeMillis()
        )
        leadDao.updateLead(updatedLead)
    }
}
