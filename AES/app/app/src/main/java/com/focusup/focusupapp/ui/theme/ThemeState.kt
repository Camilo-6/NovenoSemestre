package com.focusup.focusupapp.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.MutableState

// Estado global para el tema de la aplicacion
val LocalThemeId: androidx.compose.runtime.ProvidableCompositionLocal<MutableState<Int>> =
    staticCompositionLocalOf { mutableStateOf(1) }