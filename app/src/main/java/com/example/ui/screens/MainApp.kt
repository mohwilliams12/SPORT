package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MalaebRepository
import com.example.model.Team
import com.example.ui.components.ActiveTeamChip
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EveningSunset

sealed class Screen(val title: String, val icon: @Composable () -> Unit) {
    object Booking : Screen("الملاعب والحجز", { Icon(Icons.Default.SportsSoccer, contentDescription = null) })
    object Teams : Screen("الفرق الرياضية", { Icon(Icons.Default.Groups, contentDescription = null) })
    object Chat : Screen("الدردشة والتحديات", { Icon(Icons.Default.Chat, contentDescription = null) })
    object MyBookings : Screen("مبارياتي وجدولي", { Icon(Icons.Default.CalendarMonth, contentDescription = null) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(repository: MalaebRepository = MalaebRepository.instance) {
    val myTeam by repository.myTeam.collectAsState()
    val teams by repository.teams.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Booking) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showTeamSelectorDialog by remember { mutableStateOf(false) }
    var showCreateTeamDialog by remember { mutableStateOf(false) }
    var targetChatChannelId by remember { mutableStateOf("general") }

    // RTL for complete Arabic experience
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Column {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = EmeraldPrimary,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = "⚽", fontSize = 16.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ملاعب كورة",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = EmeraldPrimary
                                    )
                                }
                            },
                            navigationIcon = {
                                // Active Team Chip with fast switch dialog
                                ActiveTeamChip(
                                    myTeam = myTeam,
                                    onSwitchTeamClick = { showTeamSelectorDialog = true },
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            },
                            actions = {
                                // Notification Icon
                                IconButton(onClick = { showNotificationsSheet = true }) {
                                    BadgedBox(
                                        badge = {
                                            if (unreadCount > 0) {
                                                Badge(containerColor = Color.Red) {
                                                    Text(text = "$unreadCount", color = Color.White, fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val screens = listOf(
                        Screen.Booking,
                        Screen.Teams,
                        Screen.Chat,
                        Screen.MyBookings
                    )

                    screens.forEach { screen ->
                        val isSelected = currentScreen == screen
                        val labelText = when (screen) {
                            Screen.Booking -> "الملاعب"
                            Screen.Teams -> "الفرق"
                            Screen.Chat -> "الدردشة"
                            Screen.MyBookings -> "مبارياتي"
                        }
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = screen.icon,
                            label = {
                                Text(
                                    text = labelText,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    Screen.Booking -> {
                        BookingScreen(
                            repository = repository
                        )
                    }
                    Screen.Teams -> {
                        TeamsScreen(
                            repository = repository,
                            onNavigateToChat = { channelId ->
                                targetChatChannelId = channelId
                                currentScreen = Screen.Chat
                            }
                        )
                    }
                    Screen.Chat -> {
                        ChatScreen(
                            repository = repository,
                            initialChannelId = targetChatChannelId
                        )
                    }
                    Screen.MyBookings -> {
                        MyBookingsScreen(
                            repository = repository,
                            onNavigateToBooking = { currentScreen = Screen.Booking }
                        )
                    }
                }
            }
        }

        // Notifications Modal Bottom Sheet
        if (showNotificationsSheet) {
            NotificationsBottomSheet(
                notifications = notifications,
                onDismiss = { showNotificationsSheet = false },
                onMarkAllRead = { repository.markNotificationsRead() }
            )
        }

        // Quick Team Switcher Dialog
        if (showTeamSelectorDialog) {
            TeamSelectorDialog(
                teams = teams,
                currentTeam = myTeam,
                onSelectTeam = { team ->
                    repository.selectActiveTeam(team)
                    showTeamSelectorDialog = false
                },
                onAddNewTeam = {
                    showTeamSelectorDialog = false
                    showCreateTeamDialog = true
                },
                onDismiss = { showTeamSelectorDialog = false }
            )
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
    }
}

@Composable
fun TeamSelectorDialog(
    teams: List<Team>,
    currentTeam: Team?,
    onSelectTeam: (Team) -> Unit,
    onAddNewTeam: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "اختر أو سجل فريقك ⚽", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "التسجيل متاح ومفتوح لأي شخص بدون قيود أو مراجعة:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                teams.forEach { team ->
                    val isSelected = team.id == currentTeam?.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectTeam(team) },
                        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = team.badgeEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = team.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "الكابتن: ${team.captainName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (isSelected) {
                                Surface(
                                    color = EmeraldPrimary,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "النشط",
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

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAddNewTeam,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تسجيل فريق جديد الآن")
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
