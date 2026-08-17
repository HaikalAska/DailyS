package com.example.dailytask.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Light Palette ──────────────────────────────────────────────
val PrimaryLight             = Color(0xFFFF5C57)   // Coral Red — energik
val OnPrimaryLight           = Color(0xFFFFFFFF)
val PrimaryContainerLight    = Color(0xFFFFE4E3)   // Soft coral tint
val OnPrimaryContainerLight  = Color(0xFF3D0000)

val SecondaryLight           = Color(0xFF7C3AED)   // Deep Violet
val OnSecondaryLight         = Color(0xFFFFFFFF)
val SecondaryContainerLight  = Color(0xFFEDE9FE)   // Lavender tint
val OnSecondaryContainerLight = Color(0xFF2E1065)

val TertiaryLight            = Color(0xFFF59E0B)   // Amber Gold
val SurfaceLight             = Color(0xFFFFFFFF)
val BackgroundLight          = Color(0xFFFFF8F6)   // Warm off-white
val SurfaceVariantLight      = Color(0xFFF5F0EF)   // Warm light gray
val OutlineLight             = Color(0xFFD4BFBC)   // Warm outline

// ─── Dark Palette ───────────────────────────────────────────────
val PrimaryDark              = Color(0xFFFF7A76)   // Lighter coral for dark bg
val OnPrimaryDark            = Color(0xFF5C0000)
val PrimaryContainerDark     = Color(0xFF8B1A17)
val OnPrimaryContainerDark   = Color(0xFFFFDAD8)

val SecondaryDark            = Color(0xFFC4B5FD)   // Soft lavender
val OnSecondaryDark          = Color(0xFF3B0764)
val SecondaryContainerDark   = Color(0xFF5B21B6)
val OnSecondaryContainerDark = Color(0xFFEDE9FE)

val SurfaceDark              = Color(0xFF1E1414)   // Deep warm dark
val BackgroundDark           = Color(0xFF160F0F)   // Very deep warm dark
val SurfaceVariantDark       = Color(0xFF2A1E1E)   // Warm dark variant
val OutlineDark              = Color(0xFF4A3030)   // Warm dark outline

// ─── Accents (used directly in UI) ──────────────────────────────
val AccentMint               = Color(0xFF10B981)   // Success / completed
val AccentMintLight          = Color(0xFFD1FAE5)   // Mint tint container
val AccentAmber              = Color(0xFFFBBF24)   // Warning / high priority
val AccentAmberLight         = Color(0xFFFEF3C7)   // Amber tint container
val AccentRed                = Color(0xFFEF4444)   // Error / urgent
val AccentRedLight           = Color(0xFFFEE2E2)   // Red tint container
val AccentViolet             = Color(0xFF7C3AED)   // Secondary actions
val AccentVioletLight        = Color(0xFFEDE9FE)   // Violet tint container

// ─── Gradient anchor colors ──────────────────────────────────────
val CoralGradientStart       = Color(0xFFFF5C57)
val CoralGradientEnd         = Color(0xFFFF8A65)   // Warm orange-coral
val VioletGradientStart      = Color(0xFF7C3AED)
val VioletGradientEnd        = Color(0xFFA78BFA)

// Legacy aliases for backward compat
val AccentGreen              = AccentMint
val AccentPurple             = AccentViolet