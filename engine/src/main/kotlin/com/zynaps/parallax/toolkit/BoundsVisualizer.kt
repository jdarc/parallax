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
package com.zynaps.parallax.toolkit

import com.zynaps.parallax.core.*
import com.zynaps.parallax.graph.SceneGraph
import com.zynaps.parallax.math.Matrix4
import com.zynaps.parallax.math.Vector4
import java.lang.Math.fma

class BoundsVisualizer(colorBuffer: RenderBuffer, depthBuffer: FloatArray) {
    private val width = colorBuffer.width
    private val height = colorBuffer.height
    private val lines = WuLines(colorBuffer, depthBuffer, width, height)

    fun render(sceneGraph: SceneGraph, camera: Camera) {
        val frustum = Frustum(camera.view, camera.projection)
        val viewTransform = camera.projection * camera.view
        sceneGraph.root.traverseDown {
            val containment = it.isContainedBy(frustum)
            if (containment != Containment.OUTSIDE) {
                val minimum = it.worldBounds.minimum
                val maximum = it.worldBounds.maximum
                drawEdge(minimum.x, minimum.y, minimum.z, maximum.x, minimum.y, minimum.z, viewTransform)
                drawEdge(minimum.x, maximum.y, minimum.z, maximum.x, maximum.y, minimum.z, viewTransform)
                drawEdge(minimum.x, minimum.y, minimum.z, minimum.x, maximum.y, minimum.z, viewTransform)
                drawEdge(maximum.x, minimum.y, minimum.z, maximum.x, maximum.y, minimum.z, viewTransform)
                drawEdge(minimum.x, minimum.y, maximum.z, maximum.x, minimum.y, maximum.z, viewTransform)
                drawEdge(minimum.x, maximum.y, maximum.z, maximum.x, maximum.y, maximum.z, viewTransform)
                drawEdge(minimum.x, minimum.y, maximum.z, minimum.x, maximum.y, maximum.z, viewTransform)
                drawEdge(maximum.x, minimum.y, maximum.z, maximum.x, maximum.y, maximum.z, viewTransform)
                drawEdge(minimum.x, minimum.y, minimum.z, minimum.x, minimum.y, maximum.z, viewTransform)
                drawEdge(maximum.x, minimum.y, minimum.z, maximum.x, minimum.y, maximum.z, viewTransform)
                drawEdge(minimum.x, maximum.y, minimum.z, minimum.x, maximum.y, maximum.z, viewTransform)
                drawEdge(maximum.x, maximum.y, minimum.z, maximum.x, maximum.y, maximum.z, viewTransform)
                true
            } else false
        }
    }

    private fun drawEdge(minx: Float, miny: Float, minz: Float, maxx: Float, maxy: Float, maxz: Float, transform: Matrix4) {
        val v1 = transform * Vector4(minx, miny, minz, 1.0F)
        val v2 = transform * Vector4(maxx, maxy, maxz, 1.0F)
        if (clip(v1, v2)) {
            val a = toScreen(src[0])
            val b = toScreen(src[1])
            lines.draw(a.x, a.y, a.z, b.x, b.y, b.z, 0xFF0000)
        }
    }

    private fun toScreen(v: Vector4): Vector4 {
        val vw = 1.0F / v.w
        val vx = 0.5F * fma(v.x, vw, 1.0F) * width
        val vy = 0.5F * fma(-v.y, vw, 1.0F) * height
        val vz = 0.5F * fma(v.z, vw, 1.0F)
        return Vector4(vx, vy, vz, 1.0F)
    }

    private var src = Array(2) { Vector4.ZERO }
    private var dst = Array(2) { Vector4.ZERO }

    private fun clip(a: Vector4, b: Vector4): Boolean {
        val mask = computeClipMask(a, b)
        if (outside(mask)) return false

        src[0] = a
        src[1] = b
        if (inside(mask)) return true

        clip(src, dst, -1.0F)
        clip(dst, src, 1.0F)
        return true
    }

    private companion object {
        private const val NER = 0b000000000000000111
        private const val FAR = 0b000000000000111000
        private const val BOT = 0b000000000111000000
        private const val TOP = 0b000000111000000000
        private const val LFT = 0b000111000000000000
        private const val RGH = 0b111000000000000000

        private const val SAFETY = 0.999995F

        private fun inside(mask: Int) = mask == 0

        private fun outside(packed: Int) = (packed and NER == NER) or (packed and FAR == FAR) or
                (packed and BOT == BOT) or (packed and TOP == TOP) or
                (packed and LFT == LFT) or (packed and RGH == RGH)

        private fun computeClipMask(v0: Vector4, v1: Vector4): Int {
            var acc = if (v1.x < v1.w) 0 else 1
            acc = acc shl 1 or if (v0.x < v0.w) 0 else 1
            acc = acc shl 1 or if (v1.x > -v1.w) 0 else 1
            acc = acc shl 1 or if (v0.x > -v0.w) 0 else 1
            acc = acc shl 1 or if (v1.y < v1.w) 0 else 1
            acc = acc shl 1 or if (v0.y < v0.w) 0 else 1
            acc = acc shl 1 or if (v1.y > -v1.w) 0 else 1
            acc = acc shl 1 or if (v0.y > -v0.w) 0 else 1
            acc = acc shl 1 or if (v1.z < v1.w) 0 else 1
            acc = acc shl 1 or if (v0.z < v0.w) 0 else 1
            acc = acc shl 1 or if (v1.z > -v1.w) 0 else 1
            return acc shl 1 or if (v0.z > -v0.w) 0 else 1
        }

        private fun clip(src: Array<Vector4>, dst: Array<Vector4>, side: Float): Int {
            val a1 = src[0]
            val a2 = src[1]
            val na = fma(a1.z, side, -a1.w * SAFETY)
            val nb = fma(a2.z, side, -a2.w * SAFETY)
            if (na < 0.0F) {
                if (nb < 0.0F) {
                    dst[0] = a1
                    dst[1] = a2
                    return 2
                } else {
                    dst[0] = a1
                    dst[1] = a1.lerp(a2, na / (na - nb))
                    return 2
                }
            } else if (nb < 0.0F) {
                dst[0] = a1.lerp(a2, na / (na - nb))
                dst[1] = a2
                return 2
            }
            return 0
        }
    }
}
