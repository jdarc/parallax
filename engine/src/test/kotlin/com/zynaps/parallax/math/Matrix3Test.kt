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

import com.zynaps.parallax.ToolKit
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class Matrix3Test {

    @Test
    fun shouldPerformScalarAddition() {
        val actual = Matrix3(0.0F, 1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F) + 1.0F
        val expected = Matrix3(1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F)
        assert(actual, expected)
    }

    private fun assert(actual: Matrix3, expected: Matrix3) {
        Assertions.assertThat(actual.m00).isCloseTo(expected.m00, ToolKit.ACCURACY)
        Assertions.assertThat(actual.m10).isCloseTo(expected.m10, ToolKit.ACCURACY)
        Assertions.assertThat(actual.m20).isCloseTo(expected.m20, ToolKit.ACCURACY)
        Assertions.assertThat(actual.m01).isCloseTo(expected.m01, ToolKit.ACCURACY)
        Assertions.assertThat(actual.m11).isCloseTo(expected.m11, ToolKit.ACCURACY)
        Assertions.assertThat(actual.m21).isCloseTo(expected.m21, ToolKit.ACCURACY)
        Assertions.assertThat(actual.m02).isCloseTo(expected.m02, ToolKit.ACCURACY)
        Assertions.assertThat(actual.m12).isCloseTo(expected.m12, ToolKit.ACCURACY)
        Assertions.assertThat(actual.m22).isCloseTo(expected.m22, ToolKit.ACCURACY)
    }
}
