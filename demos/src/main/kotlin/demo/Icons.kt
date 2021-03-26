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
package demo

import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object Icons {

    object Toolbar {
        fun file(color: Color) = readImage("file.png")
        fun trash(color: Color) = readImage("trash.png")
        fun diskette(color: Color) = readImage("diskette.png")
        fun cube(color: Color) = readImage("cube.png")
        fun capsule(color: Color) = readImage("capsule.png")
        fun plane(color: Color) = readImage("plane.png")
        fun sphere(color: Color) = readImage("sphere.png")
        fun shapes(color: Color) = readImage("shapes.png")
        fun vsync(color: Color) = readImage("vsync-off.png")
    }

    object Tree {
        fun root() = readImage("tree/root.png")
        fun branch() = readImage("tree/branch.png")
        fun leaf() = readImage("tree/leaf.png")
        fun material() = readImage("tree/material.png")
    }

    private fun readImage(filename: String) = ImageIO.read(this.javaClass.classLoader.getResourceAsStream(filename))

    private fun readImage(filename: String, color: Color) = blend(ImageIO.read(this.javaClass.classLoader.getResourceAsStream(filename)), color)

    private fun blend(image: BufferedImage, color: Color): BufferedImage {
        val blend = color.rgb.and(0xFFFFFF)
        val blendAlpha = color.rgb shr 24 and 0xFF
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val rgb = image.getRGB(x, y)
                val alpha = (rgb shr 24 and 0xFF) * blendAlpha shr 8
                image.setRGB(x, y, alpha.shr(24) or rgb or blend)
            }
        }
        return image
    }
}
