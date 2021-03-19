/*
 * Copyright (c) 2021 Jean d'Arc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zynaps.parallax.math

import kotlin.math.pow
import kotlin.math.roundToInt

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "SpellCheckingInspection", "NOTHING_TO_INLINE")
object Scalar {
    const val PI = 3.14159265359F
    const val TAU = 6.28318530718F
    const val HALF_PI = 1.57079632679F
    const val EPSILON = 0.00000001F
    const val LARGE = 1073741823.0

    inline fun equals(a: Float, b: Float, epsilon: Float = EPSILON) = !(a - b).isNaN() && abs(a - b) <= epsilon

    inline fun min(a: Int, b: Int) = if (a < b) a else b

    inline fun min(a: Float, b: Float) = if (a < b) a else b

    inline fun max(a: Int, b: Int) = if (a > b) a else b

    inline fun max(a: Float, b: Float) = if (a > b) a else b

    inline fun clamp(value: Int, min: Int, max: Int) = value.coerceIn(min, max)

    inline fun clamp(value: Float, min: Float, max: Float) = value.coerceIn(min, max)

    inline fun ceil(a: Float) = 0x3FFFFFFF - (LARGE - a).toInt()

    inline fun floor(a: Float) = (LARGE + a).toInt() - 0x3FFFFFFF

    inline fun isPot(value: Int) = (value > 0) && (value and value - 1) == 0

    inline fun sqr(value: Float) = value * value

    inline fun invSqrt(n: Float): Float {
        val x = Float.fromBits(0x5f3759df - n.toRawBits().shr(1))
        return x * (1.5F - 0.5F * n * x * x)
    }

    inline fun hypot(x: Float, y: Float) = sqrt(x * x + y * y)

    inline fun hypot(x: Float, y: Float, z: Float) = sqrt(x * x + y * y + z * z)

    inline fun hypot(x: Float, y: Float, z: Float, w: Float) = sqrt(x * x + y * y + z * z + w * w)

    inline fun hypot(vararg values: Float) = sqrt(values.map { it * it }.sum())

    inline fun toRadians(degrees: Float) = degrees * PI / 180.0F

    inline fun toDegrees(radians: Float) = radians * 180.0F / PI

    inline fun round(x: Float) = x.roundToInt()

    inline fun pow(x: Float, y: Float) = x.pow(y)

    inline fun abs(value: Float) = kotlin.math.abs(value)

    inline fun sqrt(x: Float) = kotlin.math.sqrt(x)

    inline fun sin(a: Float) = kotlin.math.sin(a)

    inline fun asin(a: Float) = kotlin.math.asin(a)

    inline fun cos(a: Float) = kotlin.math.cos(a)

    inline fun acos(a: Float) = kotlin.math.acos(a)

    inline fun atan(a: Float) = kotlin.math.atan(a)

    inline fun atan2(y: Float, x: Float) = kotlin.math.atan2(y, x)

    inline fun tan(x: Float) = kotlin.math.tan(x)

    inline fun log2(x: Float) = kotlin.math.log2(x)
}
