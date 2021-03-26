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

import com.zynaps.parallax.math.Scalar.abs
import java.awt.Rectangle
import java.awt.geom.Rectangle2D

@Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate")
class WuLines(val colorBuffer: RenderBuffer, val depthBuffer: FloatArray, val width: Int, val height: Int) {

    private val rectangle = Rectangle(0, 0, width - 1, height - 1)
    private val minX = rectangle.minX.toFloat()
    private val minY = rectangle.minY.toFloat()
    private val maxX = rectangle.maxX.toFloat()
    private val maxY = rectangle.maxY.toFloat()

    fun draw(x0: Int, y0: Int, z0: Int, x1: Int, y1: Int, z1: Int, rgb: Int) {
        draw(x0.toFloat(), y0.toFloat(), z0.toFloat(), x1.toFloat(), y1.toFloat(), z1.toFloat(), rgb)
    }

    fun draw(x0: Float, y0: Float, z0: Float, x1: Float, y1: Float, z1: Float, rgb: Int) {
        var xs = x0
        var ys = y0
        var zs = z0
        var xe = x1
        var ye = y1
        var ze = z1
        var f1 = outcode(xs, ys)
        var f2 = outcode(xe, ye)
        while (f1 or f2 != 0) {
            if (f1 and f2 == 0) {
                val dx = xe - xs
                val dy = ye - ys
                val dz = ze - zs
                if (f1 != 0) {
                    when {
                        f1 and Rectangle2D.OUT_LEFT == Rectangle2D.OUT_LEFT && dx != 0.0F -> {
                            zs += (minX - xs) * dz / dx
                            ys += (minX - xs) * dy / dx
                            xs = minX
                        }
                        f1 and Rectangle2D.OUT_RIGHT == Rectangle2D.OUT_RIGHT && dx != 0.0F -> {
                            zs += (maxX - xs) * dz / dx
                            ys += (maxX - xs) * dy / dx
                            xs = maxX
                        }
                        f1 and Rectangle2D.OUT_BOTTOM == Rectangle2D.OUT_BOTTOM && dy != 0.0F -> {
                            zs += (maxY - ys) * dz / dy
                            xs += (maxY - ys) * dx / dy
                            ys = maxY
                        }
                        f1 and Rectangle2D.OUT_TOP == Rectangle2D.OUT_TOP && dy != 0.0F -> {
                            zs += (minY - ys) * dz / dy
                            xs += (minY - ys) * dx / dy
                            ys = minY
                        }
                    }
                    f1 = outcode(xs, ys)
                } else if (f2 != 0) {
                    if (f2 and Rectangle2D.OUT_LEFT == Rectangle2D.OUT_LEFT && dx != 0.0F) {
                        ze += (minX - xe) * dz / dx
                        ye += (minX - xe) * dy / dx
                        xe = minX
                    } else if (f2 and Rectangle2D.OUT_RIGHT == Rectangle2D.OUT_RIGHT && dx != 0.0F) {
                        ze += (maxX - xe) * dz / dx
                        ye += (maxX - xe) * dy / dx
                        xe = maxX
                    } else if (f2 and Rectangle2D.OUT_BOTTOM == Rectangle2D.OUT_BOTTOM && dy != 0.0F) {
                        ze += (maxY - ye) * dz / dy
                        xe += (maxY - ye) * dx / dy
                        ye = maxY
                    } else if (f2 and Rectangle2D.OUT_TOP == Rectangle2D.OUT_TOP && dy != 0.0F) {
                        ze += (minY - ye) * dz / dy
                        xe += (minY - ye) * dx / dy
                        ye = minY
                    }
                    f2 = outcode(xe, ye)
                }
            } else {
                return
            }
        }
        wuLine(xs, ys, zs, xe, ye, ze, rgb)
    }

    private fun outcode(x: Float, y: Float) = rectangle.outcode(x.toDouble(), y.toDouble())

    private fun wuLine(x0: Float, y0: Float, z0: Float, x1: Float, y1: Float, z1: Float, rgb: Int) {
        var xs = x0
        var ys = y0
        var zs = z0
        var xe = x1
        var ye = y1
        var ze = z1

        val steep = abs(ye - ys) > abs(xe - xs)
        if (steep) {
            var t = ys; ys = xs; xs = t; t = ye; ye = xe; xe = t
        }
        if (xs > xe) {
            var t = xs; xs = xe; xe = t; t = ys; ys = ye; ye = t; t = zs; zs = ze; ze = t
        }
        val dx = xe - xs
        if (dx == 0.0F) return
        val dy = ye - ys
        val dz = ze - zs
        val gradienty = dy / dx
        val gradientz = dz / dx

        val xend0 = round(xs)
        val yend0 = ys + gradienty * (xend0 - xs)
        val zend0 = zs + gradientz * (xend0 - xs)
        wuPixel(steep, xend0, yend0, zend0, rfPart(xs + 0.5F), rgb)

        val xend1 = round(xe)
        val yend1 = ye + gradienty * (xend1 - xe)
        val zend1 = ze + gradientz * (xend1 - xe)
        wuPixel(steep, xend1, yend1, zend1, fPart(xe + 0.5F), rgb)

        var intery = yend0 + gradienty
        var interz = zend0 + gradientz
        for (x in xend0 + 1 until xend1) {
            wuPixel(steep, x, intery, interz, 1.0F, rgb)
            intery += gradienty
            interz += gradientz
        }
    }

    private fun wuPixel(steep: Boolean, x: Int, intery: Float, z: Float, gap: Float, rgb: Int) {
        val y = intery.toInt()
        val c1 = rfPart(intery) * gap
        val c2 = fPart(intery) * gap
        if (steep) {
            plot(y, x, z, rgb, c1)
            plot(y + 1, x, z, rgb, c2)
        } else {
            plot(x, y, z, rgb, c1)
            plot(x, y + 1, z, rgb, c2)
        }
    }

    private fun plot(x: Int, y: Int, z: Float, rgb: Int, c: Float) {
        if (x >= width || y >= height) return
        val mem = y * width + x
        if (z <= depthBuffer[mem]) {
            colorBuffer[mem] = Color.blend(rgb, colorBuffer[mem], (255 * c).toInt())
        }
    }

    companion object {
        private fun round(x: Float) = (x + 0.5F).toInt()
        private fun fPart(x: Float) = if (x < 0.0F) x.toInt() - x else x - x.toInt()
        private fun rfPart(x: Float) = 1.0F - fPart(x)
    }
}
