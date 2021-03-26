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

import com.zynaps.parallax.math.Aabb
import com.zynaps.parallax.math.Matrix4
import com.zynaps.parallax.math.Plane.Companion.create
import com.zynaps.parallax.math.Vector3

@Suppress("MemberVisibilityCanBePrivate", "unused", "DuplicatedCode")
class Frustum(view: Matrix4, projection: Matrix4) {

    private val matView = view
    private val matProj = projection
    private val matComb = projection * view

    private val lft = create(matComb.m30 + matComb.m00, matComb.m31 + matComb.m01, matComb.m32 + matComb.m02, matComb.m33 + matComb.m03)
    private val rht = create(matComb.m30 - matComb.m00, matComb.m31 - matComb.m01, matComb.m32 - matComb.m02, matComb.m33 - matComb.m03)
    private val bot = create(matComb.m30 + matComb.m10, matComb.m31 + matComb.m11, matComb.m32 + matComb.m12, matComb.m33 + matComb.m13)
    private val top = create(matComb.m30 - matComb.m10, matComb.m31 - matComb.m11, matComb.m32 - matComb.m12, matComb.m33 - matComb.m13)
    private val ner = create(matComb.m30 + matComb.m20, matComb.m31 + matComb.m21, matComb.m32 + matComb.m22, matComb.m33 + matComb.m23)
    private val far = create(matComb.m30 - matComb.m20, matComb.m31 - matComb.m21, matComb.m32 - matComb.m22, matComb.m33 - matComb.m23)

    val view get() = matView
    val projection get() = matProj
    val transform get() = matComb
    val farDistance get() = 2.0F * -matProj.m23 / (2.0F * -matProj.m22 - 2.0F)
    val nearDistance get() = (-matProj.m22 - 1.0F) * farDistance / (-matProj.m22 + 1.0F)

    fun evaluate(box: Aabb): Containment {
        val c0 = box.pointsBehind(ner)
        if (c0 == 8) return Containment.OUTSIDE

        val c1 = box.pointsBehind(far)
        if (c1 == 8) return Containment.OUTSIDE

        val c2 = box.pointsBehind(lft)
        if (c2 == 8) return Containment.OUTSIDE

        val c3 = box.pointsBehind(rht)
        if (c3 == 8) return Containment.OUTSIDE

        val c4 = box.pointsBehind(top)
        if (c4 == 8) return Containment.OUTSIDE

        val c5 = box.pointsBehind(bot)
        if (c5 == 8) return Containment.OUTSIDE

        return if (c0 + c1 + c2 + c3 + c4 + c5 == 0) Containment.INSIDE else Containment.PARTIAL
    }

    companion object {

        fun computeVolume(frustum: Frustum): Array<Vector3> {
            val xAxis = Vector3(frustum.matView.m00, frustum.matView.m01, frustum.matView.m02)
            val yAxis = Vector3(frustum.matView.m10, frustum.matView.m11, frustum.matView.m12)
            val zAxis = Vector3(frustum.matView.m20, frustum.matView.m21, frustum.matView.m22)
            val near = frustum.nearDistance
            val far = frustum.farDistance
            val aspectRatio = frustum.matProj.m11 / frustum.matProj.m00

            val worldView = frustum.matView.invert
            val position = Vector3(worldView.m03, worldView.m13, worldView.m23)
            val nearCenter = position - zAxis * near
            val farCenter = position - zAxis * far

            val halfFov = 1.0F / frustum.matProj.m11
            val nearHeight = near * halfFov
            val nearWidth = nearHeight * aspectRatio
            val farHeight = far * halfFov
            val farWidth = farHeight * aspectRatio

            val ntl = nearCenter + yAxis * nearHeight - xAxis * nearWidth
            val ntr = nearCenter + yAxis * nearHeight + xAxis * nearWidth
            val nbl = nearCenter - yAxis * nearHeight - xAxis * nearWidth
            val nbr = nearCenter - yAxis * nearHeight + xAxis * nearWidth
            val ftl = farCenter + yAxis * farHeight - xAxis * farWidth
            val ftr = farCenter + yAxis * farHeight + xAxis * farWidth
            val fbl = farCenter - yAxis * farHeight - xAxis * farWidth
            val fbr = farCenter - yAxis * farHeight + xAxis * farWidth

            return arrayOf(ntr, ntl, nbl, nbr, ftl, ftr, fbr, fbl)
        }
    }
}
