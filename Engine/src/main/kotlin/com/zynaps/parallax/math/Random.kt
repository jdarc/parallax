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

import kotlin.random.Random

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "SpellCheckingInspection", "NOTHING_TO_INLINE")
object Random {

    private var random: ThreadLocal<Random> = ThreadLocal.withInitial { Random(seed) }

    var seed = System.nanoTime()
        set(value) {
            field = value
            random.set(Random(value))
        }

    fun nextInt() = random.get().nextInt()

    fun nextInt(bound: Int) = random.get().nextInt(bound)

    fun nextInt(origin: Int, bound: Int) = random.get().nextInt(origin, bound)

    fun nextFloat() = random.get().nextFloat()

    fun nextFloat(bound: Float) = random.get().nextDouble(bound.toDouble()).toFloat()

    fun nextFloat(origin: Float, bound: Float) = random.get().nextDouble(origin.toDouble(), bound.toDouble()).toFloat()

    fun nextDouble() = random.get().nextFloat()

    fun nextDouble(bound: Double) = random.get().nextDouble(bound)

    fun nextDouble(origin: Double, bound: Double) = random.get().nextDouble(origin, bound)
}