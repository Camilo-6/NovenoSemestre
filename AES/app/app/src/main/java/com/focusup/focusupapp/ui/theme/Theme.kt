package com.focusup.focusupapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.focusup.focusupapp.ui.theme.Black

private val LightColorScheme = lightColorScheme(
    // Es usada para la top bar y bottom bar
    surfaceVariant = LightBlue,
    // Se usa para los textos del top bar y bottom bar
    onSurfaceVariant = White,
    // Se usa para las celdas de los dias del calendario
    surface = BlueGray,
    // Es usado para las tareas en cada dia del calendario
    secondary = SpecialRed,
    // Se usa para los textos de las tareas en el calendario
    onSecondary = Black,
    // Es usado para los botones
    primary = Blue1,
    // Se usa para los textos de los botones
    onPrimary = White,
    // Es usado para el fondo
    background = LightYellow,
    // Se usa para el texto de los dias y el mes, y cosas dentro de background
    onBackground = Black
)

private val DarkColorScheme = darkColorScheme(
    // Es usada para la top bar y bottom bar
    surfaceVariant = LightBlack,
    // Se usa para los textos del top bar y bottom bar
    onSurfaceVariant = White,
    // Se usa para las celdas de los dias del calendario
    surface = DarkBlueGray,
    // Es usado para las tareas en cada dia del calendario
    secondary = SpecialRed,
    // Se usa para los textos de las tareas en el calendario
    onSecondary = Black,
    // Es usado para los botones
    primary = Blue2,
    // Se usa para los textos de los botones
    onPrimary = White,
    // Es usado para el fondo
    background = DarkGray,
    // Se usa para el texto de los dias y el mes, y cosas dentro de background
    onBackground = White
)

private val LightColorScheme2 = lightColorScheme(
   // Es usada para la top bar y bottom bar
    surfaceVariant = GreenBlue,
    // Se usa para los textos del top bar y bottom bar
    onSurfaceVariant = White,
    // Se usa para las celdas de los dias del calendario
    surface = LightBlueGray,
    // Es usado para las tareas en cada dia del calendario
    secondary = SpecialRed,
    // Se usa para los textos de las tareas en el calendario
    onSecondary = Black,
    // Es usado para los botones
    primary = Purple,
    // Se usa para los textos de los botones
    onPrimary = White,
    // Es usado para el fondo
    background = LightCream,
    // Se usa para el texto de los dias y el mes, y cosas dentro de background
    onBackground = DarkBlue
)

private val DarkColorScheme2 = darkColorScheme(
    // Es usada para la top bar y bottom bar
    surfaceVariant = DarkGreen2,
    // Se usa para los textos del top bar y bottom bar
    onSurfaceVariant = White,
    // Se usa para las celdas de los dias del calendario
    surface = DarkGreen3,
    // Es usado para las tareas en cada dia del calendario
    secondary = SpecialRed,
    // Se usa para los textos de las tareas en el calendario
    onSecondary = Black,
    // Es usado para los botones
    primary = DarkGreen4,
    // Se usa para los textos de los botones
    onPrimary = White,
    // Es usado para el fondo
    background = Black,
    // Se usa para el texto de los dias y el mes, y cosas dentro de background
    onBackground = LightGreen
)
val ThemePink = lightColorScheme(
    surfaceVariant = DarkPink2,
    onSurfaceVariant = White,
    surface = LightPink3,
    secondary = LightPink2,
    onSecondary = Black,
    primary = Pink,
    onPrimary = White,
    background = LightPink1,
    onBackground = White
)

val ThemeOcean = lightColorScheme(
    surfaceVariant = DarkBlue2,
    onSurfaceVariant = White,
    surface = Blue3,
    secondary = DarkBlue6,
    onSecondary = Black,
    primary = DarkBlue4,
    onPrimary = White,
    background = LightBlue4 ,
    onBackground = White
)

val ThemeRed = lightColorScheme(
    surfaceVariant = DarkRed2 ,
    onSurfaceVariant = White,
    surface = LightRed4,
    secondary = LightRed1,
    onSecondary = Black,
    primary = DarkRed3,
    onPrimary = White,
    background = LightRed2,
    onBackground = White
)

val ThemeGreen = lightColorScheme(
    surfaceVariant = DarkGreen2 ,
    onSurfaceVariant = White,
    surface = LightGreen1,
    secondary = LightGreen2,
    onSecondary = Black,
    primary = DarkGreen1,
    onPrimary = White,
    background = LightGreen2,
    onBackground = DarkGreen4
)

val ThemeYellow = lightColorScheme(
    surfaceVariant = Yellow6,
    onSurfaceVariant = Yellow4,
    surface = LightYellow3,
    secondary = LightYellow4,
    onSecondary = Yellow4,
    primary = LightYellow1,
    onPrimary = White,
    background = LightYellow4,
    onBackground = Yellow4
)

val ThemePurple = lightColorScheme(
    surfaceVariant = DarkPurple2,
    onSurfaceVariant = White,
    surface = LightPurple1,
    secondary = DarkPurple2,
    onSecondary = Black,
    primary = DarkPurple1,
    onPrimary = White,
    background = LightPurple2,
    onBackground = White
)

val ThemeOrange = lightColorScheme(
    surfaceVariant = DarkOrange1,
    onSurfaceVariant = White,
    surface = LightOrange2,
    secondary = MediumOrange2,
    onSecondary = Black,
    primary = Orange1,
    onPrimary = White,
    background = LightOrange5,
    onBackground = White
)

val ThemePanda = lightColorScheme(
    surfaceVariant = DarkGray2,
    onSurfaceVariant = White,
    surface = SoftGray2,
    secondary = SoftGray1,
    onSecondary = Black,
    primary = DarkGray1,
    onPrimary = White,
    background = White,
    onBackground = Black
)

val ThemeRainbow = lightColorScheme(
    surfaceVariant = PastelPurple1,
    onSurfaceVariant = White,
    surface = PastelGreen1,
    secondary = PastelYellow1,
    onSecondary = PastelDarkBluePurple1,
    primary = PastelRed1,
    onPrimary = PastelDarkBluePurple1,
    background = PastelYellow1,
    onBackground = PastelDarkBluePurple1
)

val ThemeVolcano = lightColorScheme(
    surfaceVariant = DarkRed4,
    onSurfaceVariant = Yellow7,
    surface = Orange2,
    secondary = Orange3,
    onSecondary = Black,
    primary = Orange3,
    onPrimary = Black,
    background = DarkGray3,
    onBackground = Yellow7
)

val ThemeGalaxy = lightColorScheme(
    surfaceVariant = Black,
    onSurfaceVariant = White,
    surface = Purple1,
    secondary = LightPink2,
    onSecondary = White,
    primary = SpaceBlue,
    onPrimary = Black,
    background = DarkBlue3,
    onBackground = White
)


val ThemeVampire = lightColorScheme(
    surfaceVariant = GrayBlueGreen,
    onSurfaceVariant = White,
    surface = RedBlood,
    secondary = BlueGray,
    onSecondary = Black,
    primary = DarkGray3,
    onPrimary = White,
    background = Wine,
    onBackground = White
)



val ThemeCake = lightColorScheme(
    surfaceVariant = PinkCake,
    onSurfaceVariant = White,
    surface = GreenCake,
    secondary = GreenBlue,
    onSecondary = Black,
    primary = YellowCake,
    onPrimary = Pink,
    background = White,
    onBackground = DarkBlue5
)

val ThemeWaterMelon = lightColorScheme(
    surfaceVariant = DarkGreen,
    onSurfaceVariant = Black,
    surface = Red5,
    secondary = White,
    onSecondary = Black,
    primary = Black,
    onPrimary = White,
    background = Red4,
    onBackground = Black
)
@Composable
fun FocusUpTheme(
    content: @Composable () -> Unit
) {
    val themeState = LocalThemeId.current // viene de ThemeState.kt
    val themeId = themeState.value

    val colorScheme = when (themeId) {
        ThemeIds.LIGHT2 -> LightColorScheme2
        ThemeIds.DARK2  -> DarkColorScheme2
        ThemeIds.LIGHT1 -> LightColorScheme
        ThemeIds.DARK1  -> DarkColorScheme
        ThemeIds.PINK   -> ThemePink
        ThemeIds.OCEAN  -> ThemeOcean
        ThemeIds.GREEN  -> ThemeGreen
        ThemeIds.YELLOW -> ThemeYellow
        ThemeIds.RED    -> ThemeRed
        ThemeIds.PURPLE -> ThemePurple
        ThemeIds.ORANGE -> ThemeOrange
        ThemeIds.PANDA  -> ThemePanda
        ThemeIds.RAINBOW-> ThemeRainbow
        ThemeIds.VOLCANO -> ThemeVolcano
        ThemeIds.GALAXY -> ThemeGalaxy
        ThemeIds.VAMPIRE  -> ThemeVampire
        ThemeIds.CAKE   -> ThemeCake
        ThemeIds.WATERMELON   -> ThemeWaterMelon

        else -> LightColorScheme2
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
