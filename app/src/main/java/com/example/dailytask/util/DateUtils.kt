package com.example.dailytask.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    val idLocale: Locale = Locale.forLanguageTag("id-ID")

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun formatDateToDisplay(dateString: String): String {
        return try {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdfInput.parse(dateString) ?: return dateString

            val today = getTodayDateString()
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 2)
            val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

            when (dateString) {
                today -> {
                    val sdfDay = SimpleDateFormat("EEEE, d MMMM yyyy", idLocale)
                    "Hari Ini • ${sdfDay.format(date)}"
                }
                yesterday -> {
                    val sdfDay = SimpleDateFormat("EEEE, d MMMM yyyy", idLocale)
                    "Kemarin • ${sdfDay.format(date)}"
                }
                tomorrow -> {
                    val sdfDay = SimpleDateFormat("EEEE, d MMMM yyyy", idLocale)
                    "Besok • ${sdfDay.format(date)}"
                }
                else -> {
                    val sdfFull = SimpleDateFormat("EEEE, d MMMM yyyy", idLocale)
                    sdfFull.format(date)
                }
            }
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatShortDate(dateString: String): String {
        return try {
            val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdfInput.parse(dateString) ?: return dateString
            val sdfShort = SimpleDateFormat("d MMM", idLocale)
            sdfShort.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatMonthYear(year: Int, month: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val sdf = SimpleDateFormat("MMMM yyyy", idLocale)
        return sdf.format(cal.time)
    }

    data class DayItem(
        val dateString: String,
        val dayName: String,
        val dayNumber: String,
        val isToday: Boolean
    )

    fun getWeekDaysAround(centerDateString: String): List<DayItem> {
        val list = mutableListOf<DayItem>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = getTodayDateString()

        val cal = Calendar.getInstance().apply {
            time = try {
                sdf.parse(centerDateString) ?: Date()
            } catch (e: Exception) {
                Date()
            }
            add(Calendar.DAY_OF_MONTH, -15)
        }

        val sdfDayName = SimpleDateFormat("EEE", idLocale)
        val sdfDayNum = SimpleDateFormat("dd", Locale.getDefault())

        for (i in 0..30) {
            val currentStr = sdf.format(cal.time)
            list.add(
                DayItem(
                    dateString = currentStr,
                    dayName = sdfDayName.format(cal.time).uppercase(),
                    dayNumber = sdfDayNum.format(cal.time),
                    isToday = currentStr == todayStr
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return list
    }

    data class CalendarDay(
        val dateString: String,
        val dayNumber: Int,
        val isCurrentMonth: Boolean,
        val isToday: Boolean
    )

    fun getDaysInMonthGrid(year: Int, month: Int): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val todayStr = getTodayDateString()

        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val startOffset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

        val prevMonthCal = (cal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, -startOffset)
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 0 until startOffset) {
            days.add(
                CalendarDay(
                    dateString = sdf.format(prevMonthCal.time),
                    dayNumber = prevMonthCal.get(Calendar.DAY_OF_MONTH),
                    isCurrentMonth = false,
                    isToday = sdf.format(prevMonthCal.time) == todayStr
                )
            )
            prevMonthCal.add(Calendar.DAY_OF_MONTH, 1)
        }

        val maxDaysInCurrentMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDaysInCurrentMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateStr = sdf.format(cal.time)
            days.add(
                CalendarDay(
                    dateString = dateStr,
                    dayNumber = day,
                    isCurrentMonth = true,
                    isToday = dateStr == todayStr
                )
            )
        }

        val remainingCells = (7 - (days.size % 7)) % 7
        val nextMonthCal = (cal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        for (i in 0 until remainingCells) {
            days.add(
                CalendarDay(
                    dateString = sdf.format(nextMonthCal.time),
                    dayNumber = nextMonthCal.get(Calendar.DAY_OF_MONTH),
                    isCurrentMonth = false,
                    isToday = sdf.format(nextMonthCal.time) == todayStr
                )
            )
            nextMonthCal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }
}
