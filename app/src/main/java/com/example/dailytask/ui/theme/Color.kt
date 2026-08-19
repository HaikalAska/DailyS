package com.example.dailytask.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Warm Minimalism / Calm Tech Palette ───────────────────────────
val HeaderDarkStart          = Color(0xFF141417)   // Warm Obsidian Slate
val HeaderDarkEnd            = Color(0xFF1E1E24)   // Deep Charcoal
val BlackGradientStart       = HeaderDarkStart
val BlackGradientMid         = Color(0xFF18181D)
val BlackGradientEnd         = HeaderDarkEnd
val CoralGradientStart       = HeaderDarkStart
val CoralGradientEnd         = HeaderDarkEnd

// ─── Light Mode Palette (Clean, Airy, High Whitespace) ─────────────
val PrimaryLight             = Color(0xFF121214)   // Deep Slate Black
val OnPrimaryLight           = Color(0xFFFFFFFF)
val PrimaryContainerLight    = Color(0xFFF3F3F6)
val OnPrimaryContainerLight  = Color(0xFF121214)

val SecondaryLight           = Color(0xFF2563EB)   // Calm Electric Blue
val OnSecondaryLight         = Color(0xFFFFFFFF)
val SecondaryContainerLight  = Color(0xFFEFF6FF)
val OnSecondaryContainerLight = Color(0xFF1E40AF)

val TertiaryLight            = Color(0xFFD97706)   // Warm Amber
val SurfaceLight             = Color(0xFFFFFFFF)   // Pure Paper White
val BackgroundLight          = Color(0xFFF9F9FB)   // Airy Off-White Canvas
val SurfaceVariantLight      = Color(0xFFF1F1F4)   // Soft Warm Gray
val OutlineLight             = Color(0xFFE4E4E9)   // Ultra-thin subtle border

// ─── Dark Mode Palette (OLED Calm Charcoal) ────────────────────────
val PrimaryDark              = Color(0xFFF8F8F8)   // Pure Off-White
val OnPrimaryDark            = Color(0xFF101012)
val PrimaryContainerDark     = Color(0xFF222228)
val OnPrimaryContainerDark   = Color(0xFFF8F8F8)

val SecondaryDark            = Color(0xFF60A5FA)
val OnSecondaryDark          = Color(0xFF1E3A8A)
val SecondaryContainerDark   = Color(0xFF1E40AF)
val OnSecondaryContainerDark = Color(0xFFDBEAFE)

val SurfaceDark              = Color(0xFF18181C)
val BackgroundDark           = Color(0xFF0E0E11)
val SurfaceVariantDark       = Color(0xFF23232A)
val OutlineDark              = Color(0xFF2E2E38)

// ─── Functional Minimalist Accents (Calm & Purposeful) ─────────────
val AccentMint               = Color(0xFF059669)   // Done / Health / Sage
val AccentMintLight          = Color(0xFFECFDF5)
val AccentAmber              = Color(0xFFD97706)   // Warm Focus / Priority
val AccentAmberLight         = Color(0xFFFFFBEB)
val AccentRed                = Color(0xFFDC2626)   // Urgent / High
val AccentRedLight           = Color(0xFFFEF2F2)
val AccentViolet             = Color(0xFF7C3AED)   // Personal Growth / Habit
val AccentVioletLight        = Color(0xFFF5F3FF)
val AccentBlue               = Color(0xFF2563EB)   // Work / Deep Focus
val AccentBlueLight          = Color(0xFFEFF6FF)

val VioletGradientStart      = Color(0xFF1E1E24)
val VioletGradientEnd        = Color(0xFF2B2B36)

fun Color.isDark(): Boolean {
    val r = red
    val g = green
    val b = blue
    val luminance = 0.2126f * r + 0.7152f * g + 0.0722f * b
    return luminance < 0.45f
}

// Backward compat aliases
val AccentGreen              = AccentMint
val AccentPurple             = AccentViolet