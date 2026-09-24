package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MalaebRepository
import com.example.model.SlotStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name from context matches Malaeb Koura`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ملاعب كورة", appName)
    }

    @Test
    fun `anyone can register team directly and book slot with instant confirmation`() {
        val repo = MalaebRepository()
        
        // Anyone registers their team directly without admin
        val newTeam = repo.createTeam(
            name = "فرسان الرياض",
            captainName = "سامي الجابر",
            playerCount = 11,
            skillLevel = "محترف",
            phone = "0509998877",
            badgeEmoji = "🦁"
        )
        assertNotNull(newTeam)
        assertEquals("فرسان الرياض", newTeam.name)

        val availableSlot = repo.timeSlots.value.first { it.status == SlotStatus.AVAILABLE }

        // Instant booking without admin approval
        val result = repo.bookSlot(
            slotId = availableSlot.id,
            teamId = newTeam.id,
            teamName = newTeam.name,
            captainName = newTeam.captainName,
            phone = newTeam.phone,
            notes = "مباراة حماسية"
        )
        assertTrue(result.isSuccess)
        assertEquals(SlotStatus.BOOKED, result.getOrNull()?.status)
    }

    @Test
    fun `double booking prevention prevents duplicate slot reservation`() {
        val repo = MalaebRepository()
        val availableSlot = repo.timeSlots.value.first { it.status == SlotStatus.AVAILABLE }

        // First booking succeeds
        val firstResult = repo.bookSlot(
            slotId = availableSlot.id,
            teamId = "team_1",
            teamName = "فريق النجوم",
            captainName = "محمد الغامدي",
            phone = "0501234567",
            notes = "مباراة ودية"
        )
        assertTrue(firstResult.isSuccess)
        assertEquals(SlotStatus.BOOKED, firstResult.getOrNull()?.status)

        // Attempting to book the SAME slot again MUST fail
        val secondResult = repo.bookSlot(
            slotId = availableSlot.id,
            teamId = "team_2",
            teamName = "صقور العاصمة",
            captainName = "أحمد الشهري",
            phone = "0559876543",
            notes = "محاولة حجز متعارض"
        )
        assertTrue(secondResult.isFailure)
        assertTrue(secondResult.exceptionOrNull()?.message?.contains("محجوز مسبقاً") == true)
    }

    @Test
    fun `team can cancel their booking and restore slot to available`() {
        val repo = MalaebRepository()
        val availableSlot = repo.timeSlots.value.first { it.status == SlotStatus.AVAILABLE }

        repo.bookSlot(
            slotId = availableSlot.id,
            teamId = "team_1",
            teamName = "فريق النجوم",
            captainName = "محمد الغامدي",
            phone = "0501234567",
            notes = null
        )

        repo.cancelBooking(availableSlot.id)

        val slotAfterCancel = repo.timeSlots.value.first { it.id == availableSlot.id }
        assertEquals(SlotStatus.AVAILABLE, slotAfterCancel.status)
    }
}
