package com.fiz.tetriswithlife.util

import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


val Double.componentX: Int get() = cos(this * (Math.PI / 180.0)).roundToInt()
val Double.componentY: Int get() = sin(this * (Math.PI / 180.0)).roundToInt()