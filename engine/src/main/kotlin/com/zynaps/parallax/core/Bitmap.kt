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

import java.awt.Graphics2D
import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt

@Suppress("unused")
class Bitmap(val image: BufferedImage) {
    val width get() = image.width
    val height get() = image.height
    val data = (image.raster.dataBuffer as DataBufferInt).data as IntArray

    constructor(width: Int, height: Int) : this(config.createCompatibleImage(width, height))

    operator fun get(x: Int, y: Int) = data[y * width + x]

    operator fun set(x: Int, y: Int, argb: Int) {
        data[y * width + x] = argb
    }

    fun draw(g: Graphics2D, x: Int = 0, y: Int = 0, width: Int = image.width, height: Int = image.height) {
        val transform = AffineTransform.getScaleInstance(width.toDouble() / this.width, height.toDouble() / this.height)
        g.drawImage(image, AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR), x, y)
    }

    companion object {
        private val localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        private val config: GraphicsConfiguration = localGraphicsEnvironment.defaultScreenDevice.defaultConfiguration
    }
}
