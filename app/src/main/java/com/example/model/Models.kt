package com.example.model

enum class BookingPeriod(val title: String, val timeRange: String, val icon: String) {
    EVENING("الفترة المسائية", "04:00 م - 09:00 م", "🌅"),
    NIGHT("الفترة الليلية", "09:00 م - 02:00 ص", "🌙")
}

enum class SlotStatus(val label: String) {
    AVAILABLE("متاح للحجز"),
    BOOKED("محجوز")
}

enum class ChallengeStatus(val label: String) {
    PENDING("في انتظار الرد"),
    ACCEPTED("تم قبول التحدي ⚽"),
    REJECTED("تم الاعتذار")
}

enum class NotificationType {
    BOOKING,
    CHAT,
    CHALLENGE,
    TEAM
}

data class Pitch(
    val id: String,
    val name: String,
    val type: String,
    val capacity: String,
    val location: String,
    val eveningPrice: Int,
    val nightPrice: Int,
    val features: List<String>,
    val rating: Double,
    val totalBookings: Int = 42
)

data class TimeSlot(
    val id: String,
    val pitchId: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val period: BookingPeriod,
    val status: SlotStatus,
    val price: Int,
    val bookedByTeamId: String? = null,
    val bookedByTeamName: String? = null,
    val captainName: String? = null,
    val contactPhone: String? = null,
    val notes: String? = null
)

data class Team(
    val id: String,
    val name: String,
    val badgeEmoji: String,
    val captainName: String,
    val playerCount: Int,
    val skillLevel: String, // مبتدئ، متوسط، محترف
    val phone: String,
    val wins: Int,
    val matchesPlayed: Int,
    val city: String,
    val isUserTeam: Boolean = false
)

data class MatchChallenge(
    val id: String,
    val senderTeamId: String,
    val senderTeamName: String,
    val targetTeamId: String,
    val targetTeamName: String,
    val pitchName: String,
    val matchDate: String,
    val matchTime: String,
    val status: ChallengeStatus = ChallengeStatus.PENDING
)

data class ChatMessage(
    val id: String,
    val channelId: String, // "general" or "dm_..."
    val senderId: String,
    val senderName: String,
    val senderTeamName: String,
    val senderBadge: String,
    val message: String,
    val timestamp: String,
    val isMe: Boolean = false,
    val challenge: MatchChallenge? = null
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false
)
