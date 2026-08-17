package com.example.dailytask.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryInfo(
    val name: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val containerColor: Color
)

object CategoryConstants {

    val categories = listOf(
        CategoryInfo(
            name = "Umum",
            icon = Icons.Rounded.Bookmark,
            primaryColor = Color(0xFF475569),
            containerColor = Color(0xFFF1F5F9)
        ),
        CategoryInfo(
            name = "Kerja",
            icon = Icons.Rounded.Work,
            primaryColor = Color(0xFF2563EB),
            containerColor = Color(0xFFDBEAFE)
        ),
        CategoryInfo(
            name = "Belajar",
            icon = Icons.Rounded.School,
            primaryColor = Color(0xFF7C3AED),
            containerColor = Color(0xFFEDE9FE)
        ),
        CategoryInfo(
            name = "Pribadi",
            icon = Icons.Rounded.Person,
            primaryColor = Color(0xFF059669),
            containerColor = Color(0xFFD1FAE5)
        ),
        CategoryInfo(
            name = "Kesehatan",
            icon = Icons.Rounded.FitnessCenter,
            primaryColor = Color(0xFFE11D48),
            containerColor = Color(0xFFFFE4E6)
        ),
        CategoryInfo(
            name = "Penting",
            icon = Icons.Rounded.LocalFireDepartment,
            primaryColor = Color(0xFFEA580C),
            containerColor = Color(0xFFFFEDD5)
        )
    )

    fun getCategoryInfo(name: String): CategoryInfo {
        return categories.find { it.name.equals(name, ignoreCase = true) } ?: categories[0]
    }
}
