package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MalaebRepository
import com.example.model.MatchChallenge
import com.example.model.Team
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.EveningSunset

@Composable
fun TeamsScreen(
    repository: MalaebRepository,
    onNavigateToChat: (channelId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val teams by repository.teams.collectAsState()
    val myTeam by repository.myTeam.collectAsState()

    var showCreateTeamDialog by remember { mutableStateOf(false) }
    var teamToChallenge by remember { mutableStateOf<Team?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateTeamDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 70.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Team")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تسجيل فريق جديد", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // My Team Highlight
            item {
                MyTeamHeroCard(
                    team = myTeam,
                    onRegisterNewTeam = { showCreateTeamDialog = true }
                )
            }

            // Teams Directory Title
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الفرق المسجلة في التطبيق (${teams.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { showCreateTeamDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "سجل فريقك",
                            fontSize = 12.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Teams List
            items(teams) { team ->
                TeamCardItem(
                    team = team,
                    isMyTeam = team.id == myTeam?.id,
                    onSelectAsMyTeam = { repository.selectActiveTeam(team) },
                    onChallengeClick = { teamToChallenge = team },
                    onChatClick = {
                        val userT = myTeam ?: teams.first()
                        val channelId = "dm_${userT.id}_${team.id}"
                        onNavigateToChat(channelId)
                    }
                )
            }
        }
    }

    // Create Team Dialog
    if (showCreateTeamDialog) {
        CreateTeamDialog(
            onDismiss = { showCreateTeamDialog = false },
            onCreate = { name, captain, count, level, phone, badge ->
                val newT = repository.createTeam(name, captain, count, level, phone, badge)
                repository.selectActiveTeam(newT)
                showCreateTeamDialog = false
            }
        )
    }

    // Send Challenge Dialog
    teamToChallenge?.let { targetTeam ->
        SendChallengeDialog(
            targetTeam = targetTeam,
            myTeam = myTeam ?: teams.first(),
            onDismiss = { teamToChallenge = null },
            onSend = { pitchName, date, time ->
                val userT = myTeam ?: teams.first()
                val channelId = "dm_${userT.id}_${targetTeam.id}"
                val challenge = MatchChallenge(
                    id = "chal_${System.currentTimeMillis()}",
                    senderTeamId = userT.id,
                    senderTeamName = userT.name,
                    targetTeamId = targetTeam.id,
                    targetTeamName = targetTeam.name,
                    pitchName = pitchName,
                    matchDate = date,
                    matchTime = time
                )
                repository.sendChatMessage(
                    channelId = channelId,
                    text = "طلب تحدي مباراة رسمي من ${userT.name} ضد ${targetTeam.name}",
                    isChallenge = true,
                    challenge = challenge
                )
                teamToChallenge = null
                onNavigateToChat(channelId)
            }
        )
    }
}

@Composable
fun MyTeamHeroCard(
    team: Team?,
    onRegisterNewTeam: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(EmeraldPrimaryDark, EmeraldPrimary)
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "فريقك الأساسي الحالي ⚽",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Button(
                        onClick = onRegisterNewTeam,
                        colors = ButtonDefaults.buttonColors(containerColor = EveningSunset),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تسجيل فريق جديد",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = team?.badgeEmoji ?: "⚡", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = team?.name ?: "فريق النجوم",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "القائد: ${team?.captainName ?: "محمد الغامدي"} | ${team?.skillLevel ?: "محترف"}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TeamStatItem(label = "اللاعبون", value = "${team?.playerCount ?: 11}")
                    TeamStatItem(label = "المباريات", value = "${team?.matchesPlayed ?: 28}")
                    TeamStatItem(label = "الانتصارات", value = "${team?.wins ?: 24} 🏆")
                    TeamStatItem(label = "المدينة", value = team?.city ?: "الرياض")
                }
            }
        }
    }
}

@Composable
fun TeamStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
    }
}

@Composable
fun TeamCardItem(
    team: Team,
    isMyTeam: Boolean,
    onSelectAsMyTeam: () -> Unit,
    onChallengeClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (isMyTeam) EmeraldPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = team.badgeEmoji, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = team.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            if (isMyTeam) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "فريقك الأساسي",
                                        color = EmeraldPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "الكابتن: ${team.captainName}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Skill Badge
                Surface(
                    color = when (team.skillLevel) {
                        "محترف" -> EmeraldPrimary.copy(alpha = 0.15f)
                        "متوسط" -> EveningSunset.copy(alpha = 0.15f)
                        else -> Color.Gray.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = team.skillLevel,
                        color = when (team.skillLevel) {
                            "محترف" -> EmeraldPrimary
                            "متوسط" -> EveningSunset
                            else -> Color.Gray
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${team.playerCount} لاعبين", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(15.dp), tint = EveningSunset)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${team.wins} فوز", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (!isMyTeam) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(
                            onClick = onSelectAsMyTeam,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("تحديد كفريقي", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = onChatClick,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("محادثة", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onChallengeClick,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("تحدي ⚔️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateTeamDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, captain: String, count: Int, level: String, phone: String, badge: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var captain by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var playerCountText by remember { mutableStateOf("10") }
    var selectedLevel by remember { mutableStateOf("متوسط") }
    var selectedBadge by remember { mutableStateOf("⚡") }

    val badges = listOf("⚡", "🦅", "🏆", "🔥", "⚽", "🦁", "🐺", "🌟")
    val levels = listOf("مبتدئ", "متوسط", "محترف")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("تسجيل فريقك الرياضي مباشرة 🛡️", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "التسجيل مفتوح وفوري لأي لاعب أو قائد فريق دون الحاجة لأي موافقة إدارية:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("اختر شعار الفريق:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(badges) { badge ->
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { selectedBadge = badge }
                                .border(
                                    2.dp,
                                    if (selectedBadge == badge) EmeraldPrimary else Color.Transparent,
                                    CircleShape
                                ),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = badge, fontSize = 20.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الفريق (مثلاً: فرسان الرياض)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = captain,
                    onValueChange = { captain = it },
                    label = { Text("اسم قائد الفريق") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = playerCountText,
                        onValueChange = { playerCountText = it },
                        label = { Text("عدد اللاعبين") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم الجوال") },
                        singleLine = true,
                        modifier = Modifier.weight(1.5f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("مستوى الفريق:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    levels.forEach { level ->
                        val isSelected = level == selectedLevel
                        OutlinedButton(
                            onClick = { selectedLevel = level },
                            colors = if (isSelected) ButtonDefaults.outlinedButtonColors(
                                containerColor = EmeraldPrimary.copy(alpha = 0.15f),
                                contentColor = EmeraldPrimary
                            ) else ButtonDefaults.outlinedButtonColors(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text(text = level, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && captain.isNotBlank()) {
                        onCreate(
                            name,
                            captain,
                            playerCountText.toIntOrNull() ?: 10,
                            selectedLevel,
                            phone.ifBlank { "0500000000" },
                            selectedBadge
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("تسجيل وتفعيل الفريق فوراً ⚽")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun SendChallengeDialog(
    targetTeam: Team,
    myTeam: Team,
    onDismiss: () -> Unit,
    onSend: (pitchName: String, date: String, time: String) -> Unit
) {
    var selectedPitch by remember { mutableStateOf("ملعب السانتياغو (عشب طبيعي)") }
    var selectedDate by remember { mutableStateOf("غداً") }
    var selectedTime by remember { mutableStateOf("09:00 م - 10:30 م (فترة ليلية)") }

    val pitches = listOf(
        "ملعب السانتياغو (عشب طبيعي)",
        "ملعب ويمبلي (عشب صناعي)",
        "صالة النخبة (أرضية باركيه)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إرسال تحدي إلى ${targetTeam.name} ⚔️", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "حدد تفاصيل المباراة وسيتم إرسال بطاقة التحدي فوراً لكابتن الفريق في الدردشة:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("الملعب المقترح:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                pitches.forEach { pitch ->
                    val isSel = pitch == selectedPitch
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedPitch = pitch }
                            .border(
                                1.dp,
                                if (isSel) EmeraldPrimary else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            ),
                        color = if (isSel) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = pitch,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp),
                            color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("تاريخ المباراة المقترح") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = { selectedTime = it },
                    label = { Text("وقت وفترة المباراة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(selectedPitch, selectedDate, selectedTime) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("إرسال التحدي 🏆")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
