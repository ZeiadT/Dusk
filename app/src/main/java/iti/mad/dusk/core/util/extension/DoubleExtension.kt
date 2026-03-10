package iti.mad.dusk.core.util.extension

import kotlin.math.roundToInt


fun Double.roundCoordinate(): Double = (this * 100.0).roundToInt() / 100.0
