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

import com.zynaps.parallax.ToolKit.ACCURACY
import com.zynaps.parallax.math.Scalar.hypot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Stream
import kotlin.math.roundToInt

internal class ScalarTest {

    @ParameterizedTest
    @ArgumentsSource(MinMaxProvider::class)
    fun shouldReturnMinimumOfTwoIntegerValues(a: Float, b: Float) {
        assertThat(Scalar.min(a.roundToInt(), b.roundToInt())).isEqualTo(a.roundToInt())
    }

    @ParameterizedTest
    @ArgumentsSource(MinMaxProvider::class)
    fun shouldReturnMaximumOfTwoIntegerValues(a: Float, b: Float) {
        assertThat(Scalar.max(a.roundToInt(), b.roundToInt())).isEqualTo(b.roundToInt())
    }

    @ParameterizedTest
    @ArgumentsSource(MinMaxProvider::class)
    fun shouldReturnMinimumOfTwoFloatValues(a: Float, b: Float) {
        assertThat(Scalar.min(a, b)).isCloseTo(a, ACCURACY)
    }

    @ParameterizedTest
    @ArgumentsSource(MinMaxProvider::class)
    fun shouldReturnMaximumOfTwoFloatValues(a: Float, b: Float) {
        assertThat(Scalar.max(a, b)).isCloseTo(b, ACCURACY)
    }

    @ParameterizedTest
    @ArgumentsSource(ClampProvider::class)
    fun shouldReturnIntegerValueClampedToRange(value: Float, expected: Float) {
        assertThat(Scalar.clamp(value.roundToInt(), 50, 100)).isEqualTo(expected.roundToInt())
    }

    @ParameterizedTest
    @ArgumentsSource(ClampProvider::class)
    fun shouldReturnFloatValueClampedToRange(value: Float, expected: Float) {
        assertThat(Scalar.clamp(value, 50.0F, 100.0F)).isCloseTo(expected, ACCURACY)
    }

    @Test
    fun shouldReturnTheSmallestIntegerGreaterThanOrEqualToValue() {
        (-1000000..1000000).forEach {
            val a = it / 23.15673F
            assertThat(Scalar.ceil(a)).isEqualTo(kotlin.math.ceil(a).toInt())
        }
    }

    @Test
    fun shouldReturnTheLargestIntegerLessThanOrEqualToValue() {
        (-1000000..1000000).forEach {
            val a = it / 23.15673F
            assertThat(Scalar.floor(a)).isEqualTo(kotlin.math.floor(a).toInt())
        }
    }

    @Test
    fun shouldReturnTrueIfNumberIsPowerOfTwo() {
        for (i in (0..30)) assertThat(Scalar.isPot(1 shl i)).isTrue
    }

    @Test
    fun shouldReturnFalseIfNumberIsNotPowerOfTwo() {
        for (i in (2..30)) assertThat(Scalar.isPot(1.shl(i) - 1)).isFalse
    }

    @Test
    fun shouldReturnSquareOfNumber() {
        val value = ThreadLocalRandom.current().nextDouble(-10000.0, 10000.0).toFloat()
        assertThat(Scalar.sqr(value)).isEqualTo(value * value)
    }

    @Test
    fun shouldReturnInverseSquareRootOfNumber() {
        val value = ThreadLocalRandom.current().nextDouble(0.0, 10000.0).toFloat()
        assertThat(Scalar.invSqrt(value)).isCloseTo(1.0F / kotlin.math.sqrt(value), ACCURACY)
    }

    @Test
    fun shouldConvertDegreesToRadians() {
        assertThat(Scalar.toRadians(45.0F)).isCloseTo(0.7853982F, ACCURACY)
    }

    @Test
    fun shouldConvertRadiansToDegrees() {
        assertThat(Scalar.toDegrees(0.7853982F)).isCloseTo(45.0F, ACCURACY)
    }

    @Test
    fun shouldComputeHypot() {
        assertThat(hypot(3.0F, 4.0F)).isEqualTo(5.0F)
        assertThat(hypot(5.0F, 12.0F)).isEqualTo(13.0F)
        assertThat(hypot(3.0F, 4.0F, 5.0F)).isCloseTo(7.0710678118654755F, ACCURACY)
        assertThat(hypot(-5.0F)).isEqualTo(5.0F)
    }

    private class MinMaxProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
            Arguments.of(0.0F, 0.0F),
            Arguments.of(1.0F, 2.0F),
            Arguments.of(-2.0F, -1.0F),
            Arguments.of(-1.0F, 2.0F)
        )
    }

    private class ClampProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
            Arguments.of(67.0F, 67.0F),
            Arguments.of(50.0F, 50.0F),
            Arguments.of(100.0F, 100.0F),
            Arguments.of(32.0F, 50.0F),
            Arguments.of(232.0F, 100.0F)
        )
    }
}
