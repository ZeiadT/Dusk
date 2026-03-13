package iti.mad.dusk.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val DuskShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),

    small = RoundedCornerShape(8.dp),

    medium = RoundedCornerShape(16.dp),

    large = RoundedCornerShape(24.dp),

    extraLarge = RoundedCornerShape(32.dp),
)

val CircleShape = RoundedCornerShape(50)
val PillShape = RoundedCornerShape(50)
val TopRoundedShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
val CardShape = RoundedCornerShape(20.dp)
val ChipShape = RoundedCornerShape(12.dp)