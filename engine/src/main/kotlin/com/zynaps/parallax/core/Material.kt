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

import java.awt.image.BufferedImage

class Material private constructor(
    val name: String,
    var ambient: Sampler,
    var diffuse: Sampler,
    var specular: Sampler,
    var emissive: Sampler,
    var glossiness: Int
) {

    fun rma(map: BufferedImage) {
        val modified = Raster.create(map.width, map.height)
        for (y in 0 until map.height) {
            val v = y.toFloat() / map.height
            for (x in 0 until map.width) {
                val u = x.toFloat() / map.width
                modified[y * map.width + x] = Color.mul(diffuse.sample(u, v), map.getRGB(x, y) and 0xFF)
            }
        }
        diffuse = Samplers.Texture(TextureMap.create(modified))
    }

    class Builder {
        private var name = ""
        private var ambient = Samplers.Color.WHITE as Sampler
        private var diffuse = Samplers.Color.WHITE as Sampler
        private var specular = Samplers.Color.BLACK as Sampler
        private var emissive = Samplers.Color.BLACK as Sampler
        private var glossiness = 0

        private fun configure(function: () -> Unit): Builder {
            function()
            return this
        }

        fun name(value: String) = configure { name = value }

        fun ambient(value: Sampler) = configure { diffuse = value }
        fun ambient(value: Int) = ambient(Samplers.Color(value))
        fun ambient(value: TextureMap) = ambient(Samplers.Texture(value))

        fun diffuse(value: Sampler) = configure { diffuse = value }
        fun diffuse(value: Int) = diffuse(Samplers.Color(value))
        fun diffuse(value: TextureMap) = diffuse(Samplers.Texture(value))

        fun specular(value: Sampler) = configure { specular = value }
        fun specular(value: Int) = specular(Samplers.Color(value))
        fun specular(value: TextureMap) = specular(Samplers.Texture(value))

        fun emissive(value: Sampler) = configure { emissive = value }
        fun emissive(value: Int) = emissive(Samplers.Color(value))
        fun emissive(value: TextureMap) = emissive(Samplers.Texture(value))

        fun glossiness(value: Int) = configure { glossiness = value }

        fun build() = Material(name, ambient, diffuse, specular, emissive, glossiness)
    }

    companion object {
        val DEFAULT = Builder().build()
    }
}
