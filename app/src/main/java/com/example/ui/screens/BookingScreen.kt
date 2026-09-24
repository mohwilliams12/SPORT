package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MalaebRepository
import com.example.model.BookingPeriod
import com.example.model.Pitch
import com.example.model.SlotStatus
import com.example.model.Team
import com.example.model.TimeSlot
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EveningSunset
import com.example.ui.theme.NightGlow
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusBooked

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    repository: MalaebRepository,
    modifier: Modifier = Modifier
) {
    val pitches by repository.pitches.collectAsState()
    val allSlots by repository.timeSlots.collectAsState()
    val selectedDate by repository.selectedDate.collectAsState()
    val teams by repository.teams.collectAsState()
    val myTeam by repository.myTeam.collectAsState()

    var selectedPitchId by remember(pitches) {
        mutableStateOf(pitches.firstOrNull()?.id ?: "")
    }
    val currentPitch = pitches.find { it.id == selectedPitchId } ?: pitches.firstOrNull()

    var selectedPeriod by remember { mutableStateOf(BookingPeriod.EVENING) }

    // Booking Dialog / Sheet State
    var slotToBook by remember { mutableStateOf<TimeSlot?>(null) }
    var slotDetailsToShow by remember { mutableStateOf<TimeSlot?>(null) }
    var showCreateTeamDialog by remember { mutableStateOf(false) }
    var bookingErrorMessage by remember { mutableStateOf<String?>(null) }

    val filteredSlots = allSlots.filter { slot ->
        slot.pitchId == selectedPitchId &&
        slot.date == selectedDate &&
        slot.period == selectedPeriod
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Stadium Banner
        item {
            StadiumHeroBanner(pitch = currentPitch)
        }

        // Pitch Selector Row
        item {
            Text(
                text = "اختر الملعب الرياضي",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pitches) { pitch ->
                    val isSelected = pitch.id == selectedPitchId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPitchId = pitch.id },
                        label = {
                            Text(
                                text = pitch.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = null,
                                tint = if (isSelected) EmeraldPrimary else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = EmeraldPrimary
                        )
                    )
                }
            }
        }

        // Date Picker Strip
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "اختر تاريخ الحجز",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            val datesList = (0..6).map { MalaebRepository.getCurrentDateString(it) }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                items(datesList) { dateStr ->
                    val isSelected = dateStr == selectedDate
                    val displayLabel = MalaebRepository.formatDateDisplay(dateStr)

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { repository.setSelectedDate(dateStr) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = displayLabel,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dateStr.substringAfterLast("-"),
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // Periods Selector Tabs (الفترة المسائية / الفترة الليلية)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    TabRow(
                        selectedTabIndex = if (selectedPeriod == BookingPeriod.EVENING) 0 else 1,
                        containerColor = Color.Transparent,
                        indicator = {},
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedPeriod == BookingPeriod.EVENING,
                            onClick = { selectedPeriod = BookingPeriod.EVENING },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selectedPeriod == BookingPeriod.EVENING)
                                        EveningSunset.copy(alpha = 0.25f)
                                    else Color.Transparent
                                )
                                .padding(vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌅", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "الفترة المسائية",
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedPeriod == BookingPeriod.EVENING) EveningSunset else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "04:00 م - 09:00 م",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Tab(
                            selected = selectedPeriod == BookingPeriod.NIGHT,
                            onClick = { selectedPeriod = BookingPeriod.NIGHT },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selectedPeriod == BookingPeriod.NIGHT)
                                        NightGlow.copy(alpha = 0.25f)
                                    else Color.Transparent
                                )
                                .padding(vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌙", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "الفترة الليلية",
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedPeriod == BookingPeriod.NIGHT) NightGlow else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "09:00 م - 02:00 ص",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    val periodPrice = if (selectedPeriod == BookingPeriod.EVENING) currentPitch?.eveningPrice ?: 180 else currentPitch?.nightPrice ?: 240
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = if (selectedPeriod == BookingPeriod.EVENING) EveningSunset else NightGlow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (selectedPeriod == BookingPeriod.EVENING) "سعر الفترة المسائية:" else "سعر الفترة الليلية (إضاءة كاملة):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "$periodPrice ر.س / فترة اللعب",
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Slots Header with Status Legends
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأوقات المتاحة للحجز (${filteredSlots.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LegendIndicator(color = StatusAvailable, label = "متاح ومباشر")
                    LegendIndicator(color = StatusBooked, label = "محجوز")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Slots List
        if (filteredSlots.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "⚽", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا توجد مواعيد مضافة لهذه الفترة.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredSlots) { slot ->
                TimeSlotCard(
                    slot = slot,
                    onBookClick = { slotToBook = slot },
                    onDetailsClick = { slotDetailsToShow = slot }
                )
            }
        }

        // Pitch Features & Amenities
        item {
            Spacer(modifier = Modifier.height(16.dp))
            currentPitch?.let { pitch ->
                PitchAmenitiesCard(pitch = pitch)
            }
        }
    }

    // Modal Sheet for Booking
    slotToBook?.let { slot ->
        BookingConfirmationSheet(
            slot = slot,
            pitch = currentPitch,
            teams = teams,
            userTeam = myTeam,
            onAddNewTeam = { showCreateTeamDialog = true },
            onDismiss = {
                slotToBook = null
                bookingErrorMessage = null
            },
            onConfirm = { teamId, teamName, captain, phone, notes ->
                val result = repository.bookSlot(
                    slotId = slot.id,
                    teamId = teamId,
                    teamName = teamName,
                    captainName = captain,
                    phone = phone,
                    notes = notes
                )
                if (result.isSuccess) {
                    slotToBook = null
                    bookingErrorMessage = null
                } else {
                    bookingErrorMessage = result.exceptionOrNull()?.message ?: "حدث خطأ أثناء الحجز"
                }
            },
            errorMessage = bookingErrorMessage
        )
    }

    // Modal Sheet for Slot Details
    slotDetailsToShow?.let { slot ->
        SlotDetailsDialog(
            slot = slot,
            onDismiss = { slotDetailsToShow = null }
        )
    }

    // Create Team Dialog directly accessible from booking
    if (showCreateTeamDialog) {
        CreateTeamDialog(
            onDismiss = { showCreateTeamDialog = false },
            onCreate = { name, captain, count, level, phone, badge ->
                val newTeam = repository.createTeam(name, captain, count, level, phone, badge)
                repository.selectActiveTeam(newTeam)
                showCreateTeamDialog = false
            }
        )
    }
}

@Composable
fun StadiumHeroBanner(pitch: Pitch?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.pitch_banner_1790259576855),
            contentDescription = "Stadium Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Surface(
                color = EmeraldPrimary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${pitch?.rating ?: 4.9} (حجز فوري ومباشر)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = pitch?.name ?: "ملعب كرة القدم",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pitch?.location ?: "الرياض",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = pitch?.capacity ?: "7 ضد 7",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TimeSlotCard(
    slot: TimeSlot,
    onBookClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (slot.status) {
                SlotStatus.AVAILABLE -> MaterialTheme.colorScheme.surface
                SlotStatus.BOOKED -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        border = BorderStroke(
            1.dp,
            when (slot.status) {
                SlotStatus.AVAILABLE -> EmeraldPrimary.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = slot.period.icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${slot.startTime} - ${slot.endTime}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = slot.period.title,
                            fontSize = 11.sp,
                            color = if (slot.period == BookingPeriod.EVENING) EveningSunset else NightGlow,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                StatusBadge(status = slot.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${slot.price} ر.س",
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldPrimary,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "تأكيد فوري ومباشر",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                when (slot.status) {
                    SlotStatus.AVAILABLE -> {
                        Button(
                            onClick = onBookClick,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "احجز لفريقك الآن",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    SlotStatus.BOOKED -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { onDetailsClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "محجوز (${slot.bookedByTeamName ?: "فريق"})",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendIndicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PitchAmenitiesCard(pitch: Pitch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "مواصفات ومميزات الملعب 🌟",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            pitch.features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationSheet(
    slot: TimeSlot,
    pitch: Pitch?,
    teams: List<Team>,
    userTeam: Team?,
    onAddNewTeam: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (teamId: String, teamName: String, captain: String, phone: String, notes: String?) -> Unit,
    errorMessage: String? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTeam by remember {
        mutableStateOf(userTeam ?: teams.firstOrNull())
    }
    var captainName by remember {
        mutableStateOf(selectedTeam?.captainName ?: "كابتن الفريق")
    }
    var phone by remember {
        mutableStateOf(selectedTeam?.phone ?: "0501234567")
    }
    var notes by remember {
        mutableStateOf("")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "تأكيد حجز الملعب مباشرة ⚽",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldPrimary
            )
            Text(
                text = "الحجز فوري ومؤكد لفريقك دون انتظار أي موافقات",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Booking Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = pitch?.name ?: "الملعب الرياضي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${slot.price} ر.س",
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldPrimary,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📅 التاريخ: ${slot.date}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "⏰ الوقت: ${slot.startTime} - ${slot.endTime}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${slot.period.icon} ${slot.period.title}",
                        fontSize = 11.sp,
                        color = if (slot.period == BookingPeriod.EVENING) EveningSunset else NightGlow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Team Selection with option to add new team
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "اختر الفريق الحاجز:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                TextButton(onClick = onAddNewTeam) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("سجل فريق جديد", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(teams) { team ->
                    val isSelected = team.id == selectedTeam?.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedTeam = team
                                captainName = team.captainName
                                phone = team.phone
                            }
                            .border(
                                1.5.dp,
                                if (isSelected) EmeraldPrimary else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            ),
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = team.badgeEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = team.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = captainName,
                onValueChange = { captainName = it },
                label = { Text("اسم قائد الفريق / الحاجز") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("رقم الجوال لتأكيد الحجز والتواصل") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("ملاحظات المباراة (اختياري)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val team = selectedTeam ?: teams.first()
                    onConfirm(team.id, team.name, captainName, phone, notes.ifBlank { null })
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = "تأكيد الحجز الفوري ⚽",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun SlotDetailsDialog(
    slot: TimeSlot,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "تفاصيل الموعد المحجوز", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = slot.status)
            }
        },
        text = {
            Column {
                Text("⏰ الوقت: ${slot.startTime} - ${slot.endTime} (${slot.period.title})")
                Text("📅 التاريخ: ${slot.date}")
                Text("💰 السعر: ${slot.price} ر.س")
                Spacer(modifier = Modifier.height(8.dp))
                slot.bookedByTeamName?.let {
                    Text("🛡️ الفريق الحاجز: $it", fontWeight = FontWeight.Bold)
                }
                slot.captainName?.let {
                    Text("👤 كابتن الفريق: $it")
                }
                slot.notes?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📝 ملاحظات: $it", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}
