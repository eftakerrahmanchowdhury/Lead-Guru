package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Person as PersonIcon // just in case
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Lead
import com.example.ui.LeadViewModel
import com.example.ui.SearchUiState
import com.example.ui.TranscriptionUiState
import com.example.ui.theme.GeoActiveIndicator
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoAlertError
import com.example.ui.theme.GeoAlertWarning
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoItemBg
import com.example.ui.theme.GeoOnPrimaryContainer
import com.example.ui.theme.GeoOnSecondaryContainer
import com.example.ui.theme.GeoOnSurface
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.ui.theme.GeoSecondaryContainer
import com.example.ui.theme.GeoSubduedText
import com.example.ui.theme.GeoSuccessEmerald

enum class DashboardTab(val title: String, val icon: ImageVector) {
    SEARCH("Search Maps", Icons.Default.Search),
    LEADS("Leads", Icons.Default.ListAlt),
    VOICE("Voice Memo", Icons.Default.Mic),
    ANALYTICS("Analytics", Icons.Default.Analytics)
}

@Composable
fun MainDashboard(
    viewModel: LeadViewModel,
    modifier: Modifier = Modifier,
    onLogout: (() -> Unit)? = null
) {
    var currentTab by remember { mutableStateOf(DashboardTab.SEARCH) }
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(GeoItemBg)
                    .border(1.dp, GeoBorder.copy(alpha = 0.5f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DashboardTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { currentTab = tab }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GeoActiveIndicator)
                                    .padding(horizontal = 20.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = GeoOnPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = GeoSubduedText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tab.title,
                            fontSize = 10.sp,
                            color = if (isSelected) GeoOnSurface else GeoSubduedText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Elegant Header
            HeaderBlock(
                title = currentTab.title,
                onLogout = onLogout
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when (currentTab) {
                    DashboardTab.SEARCH -> SearchTabScreen(viewModel, context)
                    DashboardTab.LEADS -> LeadsTabScreen(viewModel, context)
                    DashboardTab.VOICE -> VoiceTabScreen(viewModel, context)
                    DashboardTab.ANALYTICS -> AnalyticsTabScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun HeaderBlock(
    title: String,
    onLogout: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GeoBackground)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "LOCAL BUSINESS",
                color = GeoPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp
            )
            Text(
                text = title,
                color = GeoOnSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5).sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(GeoPrimaryContainer)
                .border(2.dp, Color.White, CircleShape)
                .clickable(enabled = onLogout != null) { onLogout?.invoke() }
                .testTag("logout_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Active User",
                tint = GeoOnPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ==================== SEARCH TAB SCREEN ====================

@Composable
fun SearchTabScreen(viewModel: LeadViewModel, context: Context) {
    var query by remember { mutableStateOf("") }
    val searchUiState by viewModel.searchUiState.collectAsState()
    val transcriptionUiState by viewModel.transcriptionUiState.collectAsState()

    // Sync query with voice transcription success
    if (transcriptionUiState is TranscriptionUiState.Success) {
        query = (transcriptionUiState as TranscriptionUiState.Success).text
        viewModel.resetVoiceMemo() // clear transcription state after applying to input
        Toast.makeText(context, "Voice transcription loaded into search!", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Grounded Maps Query Generator",
            color = GeoOnSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = "Find local B2B prospects using live Google Maps grounding queries.",
            color = GeoSubduedText,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Query input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("e.g. Dentists in Brooklyn", color = GeoSubduedText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GeoPrimary,
                    unfocusedBorderColor = GeoBorder,
                    focusedContainerColor = GeoSurface,
                    unfocusedContainerColor = GeoSurface,
                    focusedTextColor = GeoOnSurface,
                    unfocusedTextColor = GeoOnSurface
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Quick Voice-to-Text Button
            val isRecording = transcriptionUiState is TranscriptionUiState.Recording
            IconButton(
                onClick = {
                    if (isRecording) {
                        viewModel.stopVoiceRecording()
                    } else {
                        viewModel.startVoiceRecording(context)
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) GeoAlertError else GeoItemBg)
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.KeyboardVoice,
                    contentDescription = "Voice Input",
                    tint = if (isRecording) Color.White else GeoPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.searchMaps(query) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(Icons.Default.Map, contentDescription = "Search", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ground & Scan Google Maps", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System states rendering
        when (val state = searchUiState) {
            is SearchUiState.Idle -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Grounded Locator",
                        tint = GeoSubduedText.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Intelligence engine ready for query.",
                        color = GeoOnSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Try typing 'Hair salons in Miami' or tap the voice recorder.",
                        color = GeoSubduedText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
            is SearchUiState.Searching -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = GeoPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Grounding query on Google Maps API...",
                        color = GeoPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Compiling live businesses and scanning structure gaps...",
                        color = GeoSubduedText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            is SearchUiState.Success -> {
                Text(
                    text = "Grounded Maps Results (${state.results.size} Found)",
                    color = GeoPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.results) { lead ->
                        GroundedResultCard(lead) {
                            viewModel.addLeadDirect(lead)
                            Toast.makeText(context, "${lead.name} added as Active Lead!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            is SearchUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Listings Recovered",
                        color = GeoAlertError,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        color = GeoSubduedText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun GroundedResultCard(lead: Lead, onImport: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lead.category.uppercase(),
                        color = GeoPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = lead.name,
                        color = GeoOnSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                // Rating badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GeoItemBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = GeoAlertWarning, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = lead.rating.toString(),
                        color = GeoOnSurface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " (${lead.reviewCount})",
                        color = GeoSubduedText,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = lead.address, color = GeoSubduedText, fontSize = 12.sp)

            if (lead.phone.isNotEmpty()) {
                Text(text = "Tel: ${lead.phone}", color = GeoOnSurface, fontSize = 12.sp)
            }
            if (lead.website.isNotEmpty()) {
                Text(text = "Web: ${lead.website}", color = GeoPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            // Strategy Audit Callout
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GeoItemBg)
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Audit Note",
                            tint = GeoPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI VULNERABILITY AUDIT",
                            color = GeoPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lead.notes,
                        color = GeoSubduedText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EST. VALUE: $${lead.revenueEstimate.toInt()}",
                    color = GeoSuccessEmerald,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onImport,
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Import as Lead", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== LEADS TAB SCREEN ====================

@Composable
fun LeadsTabScreen(viewModel: LeadViewModel, context: Context) {
    val leads by viewModel.allLeads.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredLeads = when (selectedFilter) {
        "ALL" -> leads
        "NEW" -> leads.filter { it.status == "NEW" }
        "CONTACTED" -> leads.filter { it.status == "CONTACTED" }
        "FOLLOW_UP" -> leads.filter { it.status == "FOLLOW_UP" }
        "WON" -> leads.filter { it.status == "CONVERTED" }
        "LOST" -> leads.filter { it.status == "LOST" }
        else -> leads
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Quick Filters Roll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val filters = listOf("ALL", "NEW", "CONTACTED", "FOLLOW_UP", "WON", "LOST")
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                val count = when (filter) {
                    "ALL" -> leads.size
                    "NEW" -> leads.count { it.status == "NEW" }
                    "CONTACTED" -> leads.count { it.status == "CONTACTED" }
                    "FOLLOW_UP" -> leads.count { it.status == "FOLLOW_UP" }
                    "WON" -> leads.count { it.status == "CONVERTED" }
                    "LOST" -> leads.count { it.status == "LOST" }
                    else -> 0
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) GeoPrimary else GeoItemBg)
                        .border(1.dp, if (isSelected) Color.Transparent else GeoBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$filter ($count)",
                        color = if (isSelected) Color.White else GeoSubduedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (filteredLeads.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Leads in matching state.",
                    color = GeoOnSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Go to 'Search Maps' to generate valid leads.",
                    color = GeoSubduedText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredLeads) { lead ->
                    ActiveLeadCard(
                        lead = lead,
                        onStatusChange = { newStatus -> viewModel.updateStatus(lead, newStatus) },
                        onTrackMapInteraction = { type -> viewModel.trackInteraction(lead, type) },
                        onDelete = { viewModel.deleteLead(lead.id) },
                        onSaveNotes = { newNotes -> viewModel.saveLeadNotes(lead, newNotes) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveLeadCard(
    lead: Lead,
    onStatusChange: (String) -> Unit,
    onTrackMapInteraction: (String) -> Unit,
    onDelete: () -> Unit,
    onSaveNotes: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf(lead.notes) }

    val statusColor = when (lead.status) {
        "NEW" -> GeoPrimary
        "CONTACTED" -> GeoAlertWarning
        "FOLLOW_UP" -> Color(0xFFC084FC) // Balanced Purple
        "CONVERTED" -> GeoSuccessEmerald
        "LOST" -> GeoAlertError
        else -> GeoSubduedText
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, if (isExpanded) GeoPrimary else GeoBorder.copy(alpha = 0.5f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).clickable { isExpanded = !isExpanded }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = lead.status,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = lead.name,
                        color = GeoOnSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Lead", tint = GeoAlertError.copy(alpha = 0.8f))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = lead.address, color = GeoSubduedText, fontSize = 12.sp)

            // Click interaction log counts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GeoItemBg)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CALL CLICKS", color = GeoSubduedText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("${lead.mapClicksCall}", color = GeoOnSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("WEB CLICKS", color = GeoSubduedText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("${lead.mapClicksWebsite}", color = GeoOnSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MAP DIRECTS", color = GeoSubduedText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("${lead.mapClicksDirections}", color = GeoOnSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EST VALUE", color = GeoSubduedText, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("$${lead.revenueEstimate.toInt()}", color = GeoSuccessEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Quick CTAs representing simulated Map widgets to track CTR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = { onTrackMapInteraction("CALL") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GeoPrimary),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call Click", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = { onTrackMapInteraction("WEBSITE") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GeoPrimary),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.Language, contentDescription = "Web", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Web Click", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = { onTrackMapInteraction("DIRECTIONS") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GeoPrimary),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.Directions, contentDescription = "Directions", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Map Click", fontSize = 10.sp)
                }
            }

            // Expanded view for strategy tweaks & stage upgrades
            if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "STRATEGY RETARGETING & AUDIT CHEAT",
                    color = GeoPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    textStyle = TextStyle(fontSize = 12.sp, color = GeoOnSurface),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeoPrimary,
                        unfocusedBorderColor = GeoBorder,
                        unfocusedContainerColor = GeoItemBg,
                        focusedContainerColor = GeoSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )

                Button(
                    onClick = { onSaveNotes(notesText) },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Audit Notes", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "CONVERSION STAGE STACK", color = GeoSubduedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val stages = listOf("NEW", "CONTACTED", "FOLLOW_UP", "CONVERTED", "LOST")
                    stages.forEach { stage ->
                        val active = lead.status == stage
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) statusColor else GeoItemBg)
                                .border(1.dp, if (active) Color.Transparent else GeoBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable { onStatusChange(stage) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stage.replace("_", " "),
                                color = if (active) Color.White else GeoSubduedText,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Tap to open Audit notes & conversion controllers",
                    color = GeoPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clickable { isExpanded = true },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ==================== VOICE TAB SCREEN ====================

@Composable
fun VoiceTabScreen(viewModel: LeadViewModel, context: Context) {
    val transcriptionUiState by viewModel.transcriptionUiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Voice Memo Lead Synthesizer",
                color = GeoOnSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Record notes from client conversations or search queries. Gemini will transcribe them verbatim.",
                color = GeoSubduedText,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )
        }

        // Live mic visual widget
        val isRecording = transcriptionUiState is TranscriptionUiState.Recording
        val animatePulse = rememberInfiniteTransition()
        val pulseScale by animatePulse.animateFloat(
            initialValue = 1f,
            targetValue = if (isRecording) 1.2f else 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(160.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(if (isRecording) GeoAlertError.copy(alpha = 0.15f) else GeoItemBg)
                .border(
                    2.dp,
                    if (isRecording) GeoAlertError else GeoBorder,
                    CircleShape
                )
                .clickable {
                    if (isRecording) {
                        viewModel.stopVoiceRecording()
                    } else {
                        viewModel.startVoiceRecording(context)
                    }
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) GeoAlertError else GeoPrimary)
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mic Trigger",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            when (val state = transcriptionUiState) {
                is TranscriptionUiState.Idle -> {
                    Text("Tap button above to Dictate Memo", color = GeoSubduedText, fontSize = 14.sp)
                }
                is TranscriptionUiState.Recording -> {
                    Text("RECORDING VOICE MEMO LIVE...", color = GeoAlertError, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Tap mic again to conclude voice tracking.", color = GeoSubduedText, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }
                is TranscriptionUiState.Transcribing -> {
                    CircularProgressIndicator(color = GeoPrimary, modifier = Modifier.size(24.dp))
                    Text("Gemini-3.5-Flash transcribing audio...", color = GeoPrimary, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                }
                is TranscriptionUiState.Success -> {
                    Text("Verbatim Memo Compiled!", color = GeoSuccessEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GeoSurface)
                            .border(1.dp, GeoBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = state.text,
                            color = GeoOnSurface,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(state.text))
                                Toast.makeText(context, "Copied verbatim!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GeoItemBg, contentColor = GeoPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Copy Memo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                // Add as an quick custom manual Lead notes
                                val quickLead = Lead(
                                    name = "Quick Audio Contact",
                                    category = "Audio Lead Notes",
                                    address = "Voice Recorded",
                                    notes = state.text,
                                    revenueEstimate = 500.0,
                                    status = "NEW"
                                )
                                viewModel.addLeadDirect(quickLead)
                                Toast.makeText(context, "Memo imported as Quick Lead!", Toast.LENGTH_SHORT).show()
                                viewModel.resetVoiceMemo()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Import as Lead Notes", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is TranscriptionUiState.Error -> {
                    Text("Transcription error", color = GeoAlertError, fontWeight = FontWeight.Bold)
                    Text(state.message, color = GeoSubduedText, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.resetVoiceMemo() },
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Memo Engine")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

// ==================== ANALYTICS SCREEN ====================

@Composable
fun AnalyticsTabScreen(viewModel: LeadViewModel) {
    val leads by viewModel.allLeads.collectAsState()

    // Aggregate counts
    val totalLeads = leads.size
    val newLeads = leads.count { it.status == "NEW" }
    val contactedLeads = leads.count { it.status == "CONTACTED" }
    val followUpLeads = leads.count { it.status == "FOLLOW_UP" }
    val wonLeads = leads.count { it.status == "CONVERTED" }
    val lostLeads = leads.count { it.status == "LOST" }

    // Click aggregates
    val totalCalls = leads.sumOf { it.mapClicksCall }
    val totalWebClicks = leads.sumOf { it.mapClicksWebsite }
    val totalMapDirections = leads.sumOf { it.mapClicksDirections }
    val totalInteractions = totalCalls + totalWebClicks + totalMapDirections

    // Conversion rate calculations
    val conversionRate = if (totalLeads > 0) {
        (wonLeads.toFloat() / totalLeads) * 100f
    } else {
        0f
    }

    // Revenue calculations
    val totalOpportunityVal = leads.sumOf { it.revenueEstimate }
    val wonRevenueVal = leads.filter { it.status == "CONVERTED" }.sumOf { it.revenueEstimate }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // First item: Funnel stats & progress circle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CONVERSION SUCCESS", color = GeoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Total Conversion Rate", color = GeoOnSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Proportion of listed leads successfully transformed into paying clients.",
                            color = GeoSubduedText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Canvas(modifier = Modifier.size(80.dp)) {
                            // Bottom Track Circle
                            drawCircle(
                                color = GeoItemBg,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Progress sweep
                            drawArc(
                                color = if (conversionRate >= 50f) GeoSuccessEmerald else GeoPrimary,
                                startAngle = -90f,
                                sweepAngle = (conversionRate / 100f) * 360f,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = String.format("%.1f%%", conversionRate),
                            color = GeoOnSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Second item: Revenue stats cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("TOTAL OPPORTUNITY", color = GeoSubduedText, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(
                            text = String.format("$%,d", totalOpportunityVal.toLong()),
                            color = GeoPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text("Projected catalog pipeline value", color = GeoSubduedText, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("WON PIPELINE REVENUE", color = GeoSubduedText, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(
                            text = String.format("$%,d", wonRevenueVal.toLong()),
                            color = GeoSuccessEmerald,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text("Confirmed commercial earnings", color = GeoSubduedText, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }

        // Third item: Local Maps Interaction CTR Tracker (Click to conversion rates)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TACTICAL MAP INTERACTIONS", color = GeoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("Google Maps Listed Action CTR", color = GeoOnSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Track customer interactions from direct Maps actions like click to calls and web clicks.",
                        color = GeoSubduedText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    // Progress Bars for each interaction type
                    InteractionStatRow(
                        title = "Click to Phone Call CTA",
                        count = totalCalls,
                        maxCount = totalInteractions.coerceAtLeast(1),
                        color = GeoPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    InteractionStatRow(
                        title = "Website Redirect Link clicks",
                        count = totalWebClicks,
                        maxCount = totalInteractions.coerceAtLeast(1),
                        color = GeoSuccessEmerald
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    InteractionStatRow(
                        title = "Driving Directions Map actions",
                        count = totalMapDirections,
                        maxCount = totalInteractions.coerceAtLeast(1),
                        color = GeoAlertWarning
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GeoItemBg)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Logged Actions", color = GeoSubduedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("$totalInteractions Direct Clicks", color = GeoOnSurface, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // Fourth item: Funnel breakdown details
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GeoBorder.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CONVERSION STAGE STACK VOLUME", color = GeoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    FunnelLevelRow(stage = "Prospect (NEW)", count = newLeads, total = totalLeads.coerceAtLeast(1), color = GeoPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    FunnelLevelRow(stage = "Connected (CONTACTED)", count = contactedLeads, total = totalLeads.coerceAtLeast(1), color = GeoAlertWarning)
                    Spacer(modifier = Modifier.height(8.dp))
                    FunnelLevelRow(stage = "Proposal Sent (FOLLOW UP)", count = followUpLeads, total = totalLeads.coerceAtLeast(1), color = Color(0xFFC084FC))
                    Spacer(modifier = Modifier.height(8.dp))
                    FunnelLevelRow(stage = "Deals Closed (WON)", count = wonLeads, total = totalLeads.coerceAtLeast(1), color = GeoSuccessEmerald)
                    Spacer(modifier = Modifier.height(8.dp))
                    FunnelLevelRow(stage = "Deals Inactive (LOST)", count = lostLeads, total = totalLeads.coerceAtLeast(1), color = GeoAlertError)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InteractionStatRow(title: String, count: Int, maxCount: Int, color: Color) {
    val progress = count.toFloat() / maxCount.toFloat()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = GeoOnSurface, fontSize = 11.sp)
            Text("$count clicks", color = GeoOnSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(GeoItemBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun FunnelLevelRow(stage: String, count: Int, total: Int, color: Color) {
    val faction = count.toFloat() / total.toFloat()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stage,
            color = GeoOnSurface,
            fontSize = 11.sp,
            modifier = Modifier.width(180.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(GeoItemBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(faction)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
            Text(
                text = "$count",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp)
            )
        }
    }
}
