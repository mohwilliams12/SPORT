package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BookingPeriod
import com.example.model.SlotStatus
import com.example.model.Team
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EveningSunset
import com.example.ui.theme.NightGlow
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusBooked

@Composable
fun StatusBadge(status: SlotStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon) = when (status) {
        SlotStatus.AVAILABLE -> Triple(
            StatusAvailable.copy(alpha = 0.15f),
            StatusAvailable,
            Icons.Default.CheckCircle
        )
        SlotStatus.BOOKED -> Triple(
            StatusBooked.copy(alpha = 0.15f),
            StatusBooked,
            Icons.Default.SportsSoccer
        )
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PeriodChip(period: BookingPeriod, isSelected: Boolean, onClick: () -> Unit) {
    val chipColor = if (period == BookingPeriod.EVENING) EveningSunset else NightGlow
    val bg = if (isSelected) chipColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (isSelected) chipColor else Color.Transparent

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
        color = bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = period.icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = period.title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) chipColor else MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ActiveTeamChip(
    myTeam: Team?,
    onSwitchTeamClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSwitchTeamClick() },
        color = EmeraldPrimary.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = myTeam?.badgeEmoji ?: "⚡", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = myTeam?.name ?: "سجل فريقك",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
            )
        }
    }
}
