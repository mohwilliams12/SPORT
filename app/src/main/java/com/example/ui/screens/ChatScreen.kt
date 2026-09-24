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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MalaebRepository
import com.example.model.ChallengeStatus
import com.example.model.ChatMessage
import com.example.model.MatchChallenge
import com.example.model.Team
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EveningSunset

@Composable
fun ChatScreen(
    repository: MalaebRepository,
    initialChannelId: String = "general",
    modifier: Modifier = Modifier
) {
    val messages by repository.chatMessages.collectAsState()
    val teams by repository.teams.collectAsState()
    val myTeam by repository.myTeam.collectAsState()

    var activeTab by remember { mutableStateOf(if (initialChannelId == "general") 0 else 1) }
    var currentChannelId by remember { mutableStateOf(initialChannelId) }
    var messageInput by remember { mutableStateOf("") }
    var selectedTargetTeam by remember {
        mutableStateOf(teams.firstOrNull { it.id != myTeam?.id } ?: teams.first())
    }

    val userTeam = myTeam ?: teams.first()

    // Determine current channel when tab changes
    val effectiveChannelId = if (activeTab == 0) {
        "general"
    } else {
        "dm_${userTeam.id}_${selectedTargetTeam.id}"
    }

    val channelMessages = messages.filter { it.channelId == effectiveChannelId }

    Column(modifier = modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📢", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ساحة التحديات العامة",
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💬", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "محادثات قادة الفرق",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }

        // If in DMs tab, show horizontal captains selector
        if (activeTab == 1) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "اختر كابتن الفريق للمحادثة الخاصة:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(teams.filter { it.id != userTeam.id }) { team ->
                            val isSelected = team.id == selectedTargetTeam.id
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedTargetTeam = team }
                                    .border(
                                        1.5.dp,
                                        if (isSelected) EmeraldPrimary else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    ),
                                color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = team.badgeEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = team.name,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "كابتن: ${team.captainName}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp)
        ) {
            if (channelMessages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = if (activeTab == 0) "🏟️" else "💬", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (activeTab == 0)
                                "مرحباً بكم في ساحة التحديات العامة!\nاطرح طلب مباراة أو نسّق مع الفرق الرياضية هنا."
                            else
                                "ابدأ المحادثة مع كابتن ${selectedTargetTeam.name}\nلتنسيق موعد مباراة أو الاتفاق على التحدي.",
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(channelMessages) { msg ->
                    ChatMessageItem(
                        message = msg,
                        onAcceptChallenge = { repository.answerChallenge(msg.id, true) },
                        onRejectChallenge = { repository.answerChallenge(msg.id, false) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // Message Input Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Quick Action Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuickChatChip(label = "جاهزون للتحدي! ⚽") { messageInput = "فريقنا جاهز لمباراة اليوم! مين يتحدى؟" }
                    QuickChatChip(label = "نقص لاعبين 🏃") { messageInput = "محتاجين لاعبين اثنين لمباراة الليلة، مين حاب يشارك؟" }
                    QuickChatChip(label = "الفترة الليلية 🌙") { messageInput = "نفضل الحجز في الفترة الليلية بعد الساعة 9:00 م." }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = {
                            Text(
                                text = if (activeTab == 0) "اكتب رسالة لجميع الفرق..." else "رسالة خاصة لكابتن ${selectedTargetTeam.name}...",
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )

                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                repository.sendChatMessage(
                                    channelId = effectiveChannelId,
                                    text = messageInput.trim()
                                )
                                messageInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickChatChip(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onAcceptChallenge: () -> Unit,
    onRejectChallenge: () -> Unit
) {
    val isMe = message.isMe
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = message.senderBadge, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Header info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${message.senderName} (${message.senderTeamName})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMe) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = message.timestamp,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Bubble
            Surface(
                color = if (isMe) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (isMe) 14.dp else 2.dp,
                    bottomEnd = if (isMe) 2.dp else 14.dp
                ),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.message,
                        color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // Embedded Match Challenge Card
                    message.challenge?.let { chal ->
                        Spacer(modifier = Modifier.height(10.dp))
                        ChallengeCardBubble(
                            challenge = chal,
                            isMe = isMe,
                            onAccept = onAcceptChallenge,
                            onReject = onRejectChallenge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeCardBubble(
    challenge: MatchChallenge,
    isMe: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 بطاقة تحدي مباراة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isMe) Color.White else EmeraldPrimary
                )
                Surface(
                    color = when (challenge.status) {
                        ChallengeStatus.PENDING -> EveningSunset
                        ChallengeStatus.ACCEPTED -> EmeraldPrimary
                        ChallengeStatus.REJECTED -> Color.Red
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = challenge.status.label,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "📍 ${challenge.pitchName}",
                fontSize = 11.sp,
                color = if (isMe) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "📅 التاريخ: ${challenge.matchDate} | ${challenge.matchTime}",
                fontSize = 10.sp,
                color = if (isMe) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // If not me and pending, allow accept/reject
            if (!isMe && challenge.status == ChallengeStatus.PENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("قبول التحدي ⚽", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("اعتذار", fontSize = 11.sp, color = Color.Red)
                    }
                }
            }
        }
    }
}
