package com.example.data

import com.example.model.AppNotification
import com.example.model.BookingPeriod
import com.example.model.ChallengeStatus
import com.example.model.ChatMessage
import com.example.model.MatchChallenge
import com.example.model.NotificationType
import com.example.model.Pitch
import com.example.model.SlotStatus
import com.example.model.Team
import com.example.model.TimeSlot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class MalaebRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    // Pitches
    private val _pitches = MutableStateFlow<List<Pitch>>(emptyList())
    val pitches: StateFlow<List<Pitch>> = _pitches.asStateFlow()

    // Time Slots
    private val _timeSlots = MutableStateFlow<List<TimeSlot>>(emptyList())
    val timeSlots: StateFlow<List<TimeSlot>> = _timeSlots.asStateFlow()

    // Teams (Open for anyone to join or register)
    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    // Chat Messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Selected Date (yyyy-MM-dd)
    private val _selectedDate = MutableStateFlow(getCurrentDateString(0))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Active User Team (Anyone can create/switch team directly)
    private val _myTeam = MutableStateFlow<Team?>(null)
    val myTeam: StateFlow<Team?> = _myTeam.asStateFlow()

    init {
        seedInitialData()
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun selectActiveTeam(team: Team) {
        _myTeam.value = team
        _teams.value = _teams.value.map { it.copy(isUserTeam = it.id == team.id) }
    }

    private fun seedInitialData() {
        // Seed Pitches
        val initialPitches = listOf(
            Pitch(
                id = "pitch_1",
                name = "ملعب السانتياغو (عشب طبيعي 7 ضد 7)",
                type = "عشب طبيعي معتمد",
                capacity = "7 ضد 7",
                location = "شمال الرياض - مجمع الأبطال الرياضي",
                eveningPrice = 180,
                nightPrice = 240,
                features = listOf("كشافات LED ليلية فائقة", "غرف تبديل واستحمام VIP", "مواقف مجانية واسعة", "ماء ومشروبات رياضية", "مدرجات جماهير"),
                rating = 4.9,
                totalBookings = 128
            ),
            Pitch(
                id = "pitch_2",
                name = "ملعب ويمبلي (عشب صناعي 5 ضد 5)",
                type = "عشب صناعي فيفا جيل رابع",
                capacity = "5 ضد 5",
                location = "حي الياسمين - ملاعب النجوم",
                eveningPrice = 140,
                nightPrice = 190,
                features = listOf("عشب هجين مقاوم للصدمات", "إضاءة ليلية متطورة", "كاميرات تصوير أهداف ومباريات", "لوحة أهداف رقمية"),
                rating = 4.8,
                totalBookings = 95
            ),
            Pitch(
                id = "pitch_3",
                name = "صالة النخبة المغلقة (أرضية باركيه مكيفة)",
                type = "صالة داخلية مكيفة",
                capacity = "6 ضد 6",
                location = "طريق الملك سلمان - المركز الرياضي الحديث",
                eveningPrice = 200,
                nightPrice = 260,
                features = listOf("تكييف مركزي ممتاز", "أرضية باركيه احترافية", "عيادة إسعاف أولية", "كافيه متكامل ومقهى رياضي"),
                rating = 4.9,
                totalBookings = 82
            )
        )
        _pitches.value = initialPitches

        // Seed Teams - Free community teams
        val initialTeams = listOf(
            Team(
                id = "team_1",
                name = "فريق النجوم",
                badgeEmoji = "⚡",
                captainName = "محمد الغامدي",
                playerCount = 11,
                skillLevel = "محترف",
                phone = "0501234567",
                wins = 24,
                matchesPlayed = 28,
                city = "الرياض",
                isUserTeam = true
            ),
            Team(
                id = "team_2",
                name = "صقور العاصمة",
                badgeEmoji = "🦅",
                captainName = "أحمد الشهري",
                playerCount = 10,
                skillLevel = "متوسط",
                phone = "0559876543",
                wins = 18,
                matchesPlayed = 25,
                city = "الرياض",
                isUserTeam = false
            ),
            Team(
                id = "team_3",
                name = "كتيبة الأبطال",
                badgeEmoji = "🏆",
                captainName = "سالم الدوسري",
                playerCount = 12,
                skillLevel = "محترف",
                phone = "0543219876",
                wins = 31,
                matchesPlayed = 35,
                city = "الرياض",
                isUserTeam = false
            ),
            Team(
                id = "team_4",
                name = "محاربو الصحراء",
                badgeEmoji = "🔥",
                captainName = "خالد العتيبي",
                playerCount = 9,
                skillLevel = "متوسط",
                phone = "0561122334",
                wins = 14,
                matchesPlayed = 22,
                city = "الرياض",
                isUserTeam = false
            ),
            Team(
                id = "team_5",
                name = "أصدقاء الكورة",
                badgeEmoji = "⚽",
                captainName = "فيصل القحطاني",
                playerCount = 8,
                skillLevel = "مبتدئ",
                phone = "0589988776",
                wins = 9,
                matchesPlayed = 19,
                city = "الرياض",
                isUserTeam = false
            )
        )
        _teams.value = initialTeams
        _myTeam.value = initialTeams.first { it.isUserTeam }

        // Generate Time Slots for 7 days
        val generatedSlots = mutableListOf<TimeSlot>()
        val dates = (0..6).map { getCurrentDateString(it) }

        dates.forEachIndexed { dayIndex, dateStr ->
            initialPitches.forEach { pitch ->
                // Evening Slots (04:00 PM to 09:00 PM)
                val eveningSlotsDef = listOf(
                    Pair("04:30 م", "06:00 م"),
                    Pair("06:00 م", "07:30 م"),
                    Pair("07:30 م", "09:00 م")
                )
                eveningSlotsDef.forEachIndexed { slotIdx, (start, end) ->
                    val slotId = "slot_${pitch.id}_${dateStr}_eve_$slotIdx"
                    val isPreBooked = (dayIndex == 0 && slotIdx == 1) || (dayIndex == 1 && slotIdx == 0)
                    val status = if (isPreBooked) SlotStatus.BOOKED else SlotStatus.AVAILABLE
                    val bookedTeam = if (isPreBooked) initialTeams[1] else null

                    generatedSlots.add(
                        TimeSlot(
                            id = slotId,
                            pitchId = pitch.id,
                            date = dateStr,
                            startTime = start,
                            endTime = end,
                            period = BookingPeriod.EVENING,
                            status = status,
                            price = pitch.eveningPrice,
                            bookedByTeamId = bookedTeam?.id,
                            bookedByTeamName = bookedTeam?.name,
                            captainName = bookedTeam?.captainName,
                            contactPhone = bookedTeam?.phone,
                            notes = if (isPreBooked) "تحدي كروي بين الفرق" else null
                        )
                    )
                }

                // Night Slots (09:00 PM to 02:00 AM)
                val nightSlotsDef = listOf(
                    Pair("09:00 م", "10:30 م"),
                    Pair("10:30 م", "12:00 ص"),
                    Pair("12:00 ص", "01:30 ص")
                )
                nightSlotsDef.forEachIndexed { slotIdx, (start, end) ->
                    val slotId = "slot_${pitch.id}_${dateStr}_ngt_$slotIdx"
                    val isPreBooked = (dayIndex == 0 && slotIdx == 0)
                    val status = if (isPreBooked) SlotStatus.BOOKED else SlotStatus.AVAILABLE
                    val bookedTeam = if (isPreBooked) initialTeams[3] else null

                    generatedSlots.add(
                        TimeSlot(
                            id = slotId,
                            pitchId = pitch.id,
                            date = dateStr,
                            startTime = start,
                            endTime = end,
                            period = BookingPeriod.NIGHT,
                            status = status,
                            price = pitch.nightPrice,
                            bookedByTeamId = bookedTeam?.id,
                            bookedByTeamName = bookedTeam?.name,
                            captainName = bookedTeam?.captainName,
                            contactPhone = bookedTeam?.phone,
                            notes = if (isPreBooked) "سهرة كروية ليلية تحت الأضواء" else null
                        )
                    )
                }
            }
        }
        _timeSlots.value = generatedSlots

        // Seed Chat Messages
        val initialChat = listOf(
            ChatMessage(
                id = "msg_1",
                channelId = "general",
                senderId = "captain_2",
                senderName = "أحمد الشهري",
                senderTeamName = "صقور العاصمة",
                senderBadge = "🦅",
                message = "السلام عليكم يا شباب، مين جاهز لمباراة ودية في الفترة المسائية غداً بملعب السانتياغو؟",
                timestamp = "منذ 25 دقيقة",
                isMe = false
            ),
            ChatMessage(
                id = "msg_2",
                channelId = "general",
                senderId = "captain_1",
                senderName = "محمد الغامدي",
                senderTeamName = "فريق النجوم",
                senderBadge = "⚡",
                message = "وعليكم السلام يا كابتن أحمد، نحن جاهزون ونتحدى فريقكم غداً في الفترة الليلية! ⚽🔥",
                timestamp = "منذ 18 دقيقة",
                isMe = true
            ),
            ChatMessage(
                id = "msg_3",
                channelId = "general",
                senderId = "captain_3",
                senderName = "سالم الدوسري",
                senderTeamName = "كتيبة الأبطال",
                senderBadge = "🏆",
                message = "الملعب ممتاز والأرضية مجددة بالكامل، أنصح بالحجز المبكر للفترة الليلية لأن الإضاءة ممتازة.",
                timestamp = "منذ 10 دقائق",
                isMe = false
            ),
            ChatMessage(
                id = "msg_4",
                channelId = "dm_team_1_team_2",
                senderId = "captain_2",
                senderName = "أحمد الشهري",
                senderTeamName = "صقور العاصمة",
                senderBadge = "🦅",
                message = "يا كابتن محمد، نود لعب مباراة حماسية معكم هذا المساء. هل يناسبكم الساعة 09:00 م؟",
                timestamp = "منذ ساعة",
                isMe = false,
                challenge = MatchChallenge(
                    id = "chal_1",
                    senderTeamId = "team_2",
                    senderTeamName = "صقور العاصمة",
                    targetTeamId = "team_1",
                    targetTeamName = "فريق النجوم",
                    pitchName = "ملعب السانتياغو",
                    matchDate = "اليوم",
                    matchTime = "09:00 م - 10:30 م (فترة ليلية)",
                    status = ChallengeStatus.PENDING
                )
            )
        )
        _chatMessages.value = initialChat

        // Seed Notifications
        val initialNotifs = listOf(
            AppNotification(
                id = "notif_1",
                title = "تأكيد حجز الملعب ⚽",
                message = "تم تأكيد حجز موعدك في ملعب السانتياغو للفترة الليلية بنجاح!",
                timeAgo = "منذ 15 دقيقة",
                type = NotificationType.BOOKING,
                isRead = false
            ),
            AppNotification(
                id = "notif_2",
                title = "تحدي مباراة جديد 🏆",
                message = "أرسل كابتن فريق صقور العاصمة طلب تحدي لمباراة ودية.",
                timeAgo = "منذ ساعتين",
                type = NotificationType.CHALLENGE,
                isRead = false
            )
        )
        _notifications.value = initialNotifs
    }

    /**
     * Instant Booking without Admin Approval - with DOUBLE-BOOKING PREVENTION
     */
    @Synchronized
    fun bookSlot(
        slotId: String,
        teamId: String,
        teamName: String,
        captainName: String,
        phone: String,
        notes: String?
    ): Result<TimeSlot> {
        val currentList = _timeSlots.value
        val slot = currentList.find { it.id == slotId }
            ?: return Result.failure(Exception("الموعد المطلوب غير موجود"))

        // Strict double-booking prevention check
        if (slot.status != SlotStatus.AVAILABLE) {
            return Result.failure(
                Exception("عذراً، هذا الموعد محجوز مسبقاً من قبل فريق آخر! يرجى اختيار موعد آخر.")
            )
        }

        // Direct instant confirmation!
        val updatedSlot = slot.copy(
            status = SlotStatus.BOOKED,
            bookedByTeamId = teamId,
            bookedByTeamName = teamName,
            captainName = captainName,
            contactPhone = phone,
            notes = notes
        )

        _timeSlots.value = currentList.map { if (it.id == slotId) updatedSlot else it }

        // Instant Confirmation Notification
        val notif = AppNotification(
            id = UUID.randomUUID().toString(),
            title = "تم تأكيد حجزك بنجاح! ⚽✅",
            message = "تم تثبيت حجز فريق $teamName لموعد ${updatedSlot.startTime} في ${updatedSlot.period.title}. نراكم على أرضية الملعب!",
            timeAgo = "الآن",
            type = NotificationType.BOOKING,
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value

        return Result.success(updatedSlot)
    }

    /**
     * Cancel a booking by the team
     */
    fun cancelBooking(slotId: String) {
        val currentList = _timeSlots.value
        val slot = currentList.find { it.id == slotId } ?: return

        val teamName = slot.bookedByTeamName ?: "الفريق"
        val updated = slot.copy(
            status = SlotStatus.AVAILABLE,
            bookedByTeamId = null,
            bookedByTeamName = null,
            captainName = null,
            contactPhone = null,
            notes = null
        )
        _timeSlots.value = currentList.map { if (it.id == slotId) updated else it }

        val notif = AppNotification(
            id = UUID.randomUUID().toString(),
            title = "تم إلغاء الحجز ❌",
            message = "تم إلغاء حجز موعد ${slot.startTime}. الموعد متاح الآن لأي فريق آخر.",
            timeAgo = "الآن",
            type = NotificationType.BOOKING,
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    /**
     * Anyone can instantly create/register their team!
     */
    fun createTeam(
        name: String,
        captainName: String,
        playerCount: Int,
        skillLevel: String,
        phone: String,
        badgeEmoji: String,
        city: String = "الرياض"
    ): Team {
        val newTeam = Team(
            id = "team_${UUID.randomUUID().toString().take(6)}",
            name = name,
            badgeEmoji = badgeEmoji,
            captainName = captainName,
            playerCount = playerCount,
            skillLevel = skillLevel,
            phone = phone,
            wins = 0,
            matchesPlayed = 0,
            city = city,
            isUserTeam = true
        )

        _teams.value = _teams.value.map { it.copy(isUserTeam = false) } + newTeam
        _myTeam.value = newTeam

        val notif = AppNotification(
            id = UUID.randomUUID().toString(),
            title = "أهلاً بفريق $name! 🛡️",
            message = "تم تسجيل وتفعيل فريقك مباشرة في التطبيق بقيادة الكابتن $captainName. يمكنك الآن الحجز وتحدي الفرق!",
            timeAgo = "الآن",
            type = NotificationType.TEAM,
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value

        return newTeam
    }

    /**
     * Send Chat Message & Auto-respond in DM or Public Arena
     */
    fun sendChatMessage(
        channelId: String,
        text: String,
        isChallenge: Boolean = false,
        challenge: MatchChallenge? = null
    ) {
        val sender = _myTeam.value ?: _teams.value.first()
        val newMsg = ChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(6)}",
            channelId = channelId,
            senderId = sender.id,
            senderName = sender.captainName,
            senderTeamName = sender.name,
            senderBadge = sender.badgeEmoji,
            message = text,
            timestamp = "الآن",
            isMe = true,
            challenge = challenge
        )

        _chatMessages.value = _chatMessages.value + newMsg

        // If it's a DM or Challenge, simulate counter-party captain response
        if (channelId.startsWith("dm_") || isChallenge) {
            scope.launch {
                delay(1000)
                val replyText = if (isChallenge) {
                    "أهلاً كابتن ${sender.captainName}! قبلنا التحدي بكل حماس ⚽ نراكم في الموعد المحدد على أرضية الملعب!"
                } else {
                    "أهلاً بك يا كابتن! فريقنا جاهز للتنسيق واللعب، دعنا نتفق على موعد مناسب في الفترة المسائية أو الليلية."
                }
                val autoReply = ChatMessage(
                    id = "msg_${UUID.randomUUID().toString().take(6)}",
                    channelId = channelId,
                    senderId = "captain_2",
                    senderName = "أحمد الشهري",
                    senderTeamName = "صقور العاصمة",
                    senderBadge = "🦅",
                    message = replyText,
                    timestamp = "الآن",
                    isMe = false
                )
                _chatMessages.value = _chatMessages.value + autoReply

                val chatNotif = AppNotification(
                    id = UUID.randomUUID().toString(),
                    title = "رسالة جديدة في الدردشة 💬",
                    message = "وصلك رد جديد من كابتن أحمد الشهري (فريق صقور العاصمة).",
                    timeAgo = "الآن",
                    type = NotificationType.CHAT,
                    isRead = false
                )
                _notifications.value = listOf(chatNotif) + _notifications.value
            }
        }
    }

    /**
     * Respond to Match Challenge (Accept / Reject)
     */
    fun answerChallenge(messageId: String, accept: Boolean) {
        val current = _chatMessages.value
        _chatMessages.value = current.map { msg ->
            if (msg.id == messageId && msg.challenge != null) {
                val newStatus = if (accept) ChallengeStatus.ACCEPTED else ChallengeStatus.REJECTED
                msg.copy(challenge = msg.challenge.copy(status = newStatus))
            } else msg
        }

        val statusText = if (accept) "قبول التحدي 🏆" else "الاعتذار عن التحدي"
        val notif = AppNotification(
            id = UUID.randomUUID().toString(),
            title = "تحديث حالة التحدي ⚽",
            message = "تم $statusText بنجاح بين الفريقين.",
            timeAgo = "الآن",
            type = NotificationType.CHALLENGE,
            isRead = false
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun markNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    companion object {
        val instance by lazy { MalaebRepository() }

        fun getCurrentDateString(offsetDays: Int = 0): String {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, offsetDays)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            return sdf.format(cal.time)
        }

        fun formatDateDisplay(dateStr: String): String {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val date = sdf.parse(dateStr) ?: Date()
                val todayCal = Calendar.getInstance()
                val targetCal = Calendar.getInstance().apply { time = date }

                val diffDays = targetCal.get(Calendar.DAY_OF_YEAR) - todayCal.get(Calendar.DAY_OF_YEAR)
                when (diffDays) {
                    0 -> "اليوم"
                    1 -> "غداً"
                    2 -> "بعد غد"
                    else -> {
                        val arabicDayFormat = SimpleDateFormat("EEEE d MMMM", Locale("ar"))
                        arabicDayFormat.format(date)
                    }
                }
            } catch (e: Exception) {
                dateStr
            }
        }
    }
}
