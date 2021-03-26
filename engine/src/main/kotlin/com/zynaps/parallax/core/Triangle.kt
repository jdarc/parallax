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
package com.zynaps.parallax.core

import com.zynaps.parallax.math.Vector3

class Triangle(val v0: Int, val v1: Int, val v2: Int) {
    var n0 = 0
    var n1 = 0
    var n2 = 0
    var t0 = 0
    var t1 = 0
    var t2 = 0
    var normalType = Normal.SURFACE
    var material = Material.DEFAULT
    var normal = Vector3.ZERO

    fun withNormalType(type: Normal): Triangle {
        normalType = type
        return this
    }

    fun withNormals(a: Int, b: Int, c: Int): Triangle {
        n0 = a
        n1 = b
        n2 = c
        normalType = Normal.TRIANGLE
        return this
    }

    fun withUvs(a: Int, b: Int, c: Int): Triangle {
        t0 = a
        t1 = b
        t2 = c
        return this
    }

    fun withMaterial(material: Material): Triangle {
        this.material = material
        return this
    }
}
