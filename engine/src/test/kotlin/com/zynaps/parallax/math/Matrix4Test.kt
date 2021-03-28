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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.sin

internal class Matrix4Test {

    @Test
    fun shouldConstructMatrix() {
        val result = Matrix4(-8.7F, -9.7F, 5.4F, -2.2F, -9.0F, -5.0F, -5.1F, -5.8F, 2.0F, 3.0F, -9.5F, 5.6F, -9.5F, -1.3F, -8.1F, -0.4F)
        assertThat(result.m00).isCloseTo(-8.7F, ACCURACY)
        assertThat(result.m10).isCloseTo(-9.7F, ACCURACY)
        assertThat(result.m20).isCloseTo(+5.4F, ACCURACY)
        assertThat(result.m30).isCloseTo(-2.2F, ACCURACY)
        assertThat(result.m01).isCloseTo(-9.0F, ACCURACY)
        assertThat(result.m11).isCloseTo(-5.0F, ACCURACY)
        assertThat(result.m21).isCloseTo(-5.1F, ACCURACY)
        assertThat(result.m31).isCloseTo(-5.8F, ACCURACY)
        assertThat(result.m02).isCloseTo(+2.0F, ACCURACY)
        assertThat(result.m12).isCloseTo(+3.0F, ACCURACY)
        assertThat(result.m22).isCloseTo(-9.5F, ACCURACY)
        assertThat(result.m32).isCloseTo(+5.6F, ACCURACY)
        assertThat(result.m03).isCloseTo(-9.5F, ACCURACY)
        assertThat(result.m13).isCloseTo(-1.3F, ACCURACY)
        assertThat(result.m23).isCloseTo(-8.1F, ACCURACY)
        assertThat(result.m33).isCloseTo(-0.4F, ACCURACY)
    }

    @Test
    fun shouldConstructMatrixFromArray() {
        val d = ThreadLocalRandom.current().doubles(16).toArray().map { -10.0F + it.toFloat() * 20.0F }.toFloatArray()
        check(Matrix4(d), d)
    }

    @Test
    fun shouldTransposeMatrix() {
        val result = Matrix4(-1.3F, -0.8F, -4.1F, 3.6F, 6.7F, 3.5F, 1.7F, -8.3F, -7.8F, -1.9F, 5.6F, 9.1F, 1.9F, -0.9F, -8.4F, 8.0F)
        val expected = floatArrayOf(-1.3F, 6.7F, -7.8F, 1.9F, -0.8F, 3.5F, -1.9F, -0.9F, -4.1F, 1.7F, 5.6F, -8.4F, 3.6F, -8.3F, 9.1F, 8.0F)
        check(result.transpose(), expected)
    }

    @Test
    fun shouldPostMultiplyMatrix() {
        val a = Matrix4(-9.6F, 0.8F, -3.5F, -5.3F, 5.0F, -1.7F, 0.0F, 3.8F, 6.3F, 8.2F, -8.8F, -1.4F, -8.7F, -1.3F, 7.1F, -2.8F)
        val b = Matrix4(2.3F, -0.5F, -5.7F, -1.2F, -8.2F, 8.8F, -4.9F, -9.7F, 3.5F, -5.3F, -7.1F, -0.9F, -2.1F, -5.4F, 4.5F, -4.5F)
        val expected = floatArrayOf(
            -50.05F,
            -42.49F,
            33.59F,
            -2.75F,
            176.24F,
            -49.09F,
            2.95F,
            110.92F,
            -97.0F,
            -45.24F,
            43.84F,
            -26.23F,
            60.66F,
            50.25F,
            -64.2F,
            -3.09F
        )
        check(a * b, expected)
    }

    @Test
    fun shouldCreateUniformScalingMatrix() {
        check(
            Matrix4.createScale(3.5F),
            floatArrayOf(3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 3.5F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        )
    }

    @Test
    fun shouldCreateScalingMatrix() {
        val vec = Vector3(4F, -2F, 8F)
        check(
            Matrix4.createScale(vec),
            floatArrayOf(4F, 0.0F, 0.0F, 0.0F, 0.0F, -2F, 0.0F, 0.0F, 0.0F, 0.0F, 8F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        )
    }

    @Test
    fun shouldCreateRotationAboutXAxis() {
        val angle = -0.15F
        val s = sin(angle)
        val c = cos(angle)
        check(
            Matrix4.createRotationX(angle),
            floatArrayOf(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, c, s, 0.0F, 0.0F, -s, c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        )
    }

    @Test
    fun shouldCreateRotationAboutYAxis() {
        val angle = 2.45F
        val s = sin(angle)
        val c = cos(angle)
        check(
            Matrix4.createRotationY(angle),
            floatArrayOf(c, 0.0F, -s, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, s, 0.0F, c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        )
    }

    @Test
    fun shouldCreateRotationAboutZAxis() {
        val angle = 1.75F
        val s = sin(angle)
        val c = cos(angle)
        check(
            Matrix4.createRotationZ(angle),
            floatArrayOf(c, s, 0.0F, 0.0F, -s, c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        )
    }

    private fun check(result: Matrix4, expected: FloatArray) {
        assertThat(result.m00).isCloseTo(expected[0x0], ACCURACY)
        assertThat(result.m10).isCloseTo(expected[0x1], ACCURACY)
        assertThat(result.m20).isCloseTo(expected[0x2], ACCURACY)
        assertThat(result.m30).isCloseTo(expected[0x3], ACCURACY)
        assertThat(result.m01).isCloseTo(expected[0x4], ACCURACY)
        assertThat(result.m11).isCloseTo(expected[0x5], ACCURACY)
        assertThat(result.m21).isCloseTo(expected[0x6], ACCURACY)
        assertThat(result.m31).isCloseTo(expected[0x7], ACCURACY)
        assertThat(result.m02).isCloseTo(expected[0x8], ACCURACY)
        assertThat(result.m12).isCloseTo(expected[0x9], ACCURACY)
        assertThat(result.m22).isCloseTo(expected[0xA], ACCURACY)
        assertThat(result.m32).isCloseTo(expected[0xB], ACCURACY)
        assertThat(result.m03).isCloseTo(expected[0xC], ACCURACY)
        assertThat(result.m13).isCloseTo(expected[0xD], ACCURACY)
        assertThat(result.m23).isCloseTo(expected[0xE], ACCURACY)
        assertThat(result.m33).isCloseTo(expected[0xF], ACCURACY)
    }
}
