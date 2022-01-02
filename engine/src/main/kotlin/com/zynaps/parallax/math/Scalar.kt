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

object Scalar {
    const val ONE = 1.0f
    const val HALF = 0.5f
    const val ZERO = 0.0f
    const val PI = 3.1415927f
    const val TAU = 6.2831855f
    const val HALF_PI = 1.5707964f
    const val EPSILON = 0.00000001f

    private const val LARGE = 1073741823.0

    fun float(x: Number) = x.toFloat()

    fun equals(a: Float, b: Float, epsilon: Float = EPSILON) = !(a - b).isNaN() && abs(a - b) <= epsilon

    fun min(a: Int, b: Int) = kotlin.math.min(a, b)

    fun min(a: Float, b: Float) = kotlin.math.min(a, b)

    fun max(a: Int, b: Int) = kotlin.math.max(a, b)

    fun max(a: Float, b: Float) = kotlin.math.max(a, b)

    fun clamp(value: Int, min: Int, max: Int) = max(min, min(max, value))

    fun clamp(value: Float, min: Float, max: Float) = max(min, min(max, value))

    fun ceil(a: Float) = 0x3FFFFFFF - (LARGE - a).toInt()

    fun floor(a: Float) = (LARGE + a).toInt() - 0x3FFFFFFF

    fun isPot(value: Int) = (value > 0) && (value and value - 1) == 0

    fun sqr(value: Float) = value * value

    fun invSqrt(n: Float) = ONE / sqrt(n)

    fun hypot(x: Float, y: Float) = sqrt(x * x + y * y)

    fun hypot(x: Float, y: Float, z: Float) = sqrt(x * x + y * y + z * z)

    fun hypot(x: Float, y: Float, z: Float, w: Float) = sqrt(x * x + y * y + z * z + w * w)

    fun hypot(vararg values: Float) = sqrt(values.map { it * it }.sum())

    fun toRadians(degrees: Float) = degrees * PI / 180

    fun toDegrees(radians: Float) = radians * 180 / PI

    fun round(x: Float) = x.roundToInt()

    fun pow(x: Float, y: Float) = x.pow(y)

    fun abs(value: Float) = kotlin.math.abs(value)

    fun sqrt(x: Float) = kotlin.math.sqrt(x)

    fun sin(a: Float) = kotlin.math.sin(a)

    fun asin(a: Float) = kotlin.math.asin(a)

    fun cos(a: Float) = kotlin.math.cos(a)

    fun acos(a: Float) = kotlin.math.acos(a)

    fun atan(a: Float) = kotlin.math.atan(a)

    fun atan2(y: Float, x: Float) = kotlin.math.atan2(y, x)

    fun tan(x: Float) = kotlin.math.tan(x)

    fun log2(x: Float) = kotlin.math.log2(x)
}
