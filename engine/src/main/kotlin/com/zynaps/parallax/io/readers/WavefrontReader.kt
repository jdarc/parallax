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
package com.zynaps.parallax.io.readers

import com.zynaps.parallax.core.*
import com.zynaps.parallax.io.ResourceLoader
import com.zynaps.parallax.math.Scalar.clamp
import com.zynaps.parallax.system.Logger
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.AffineTransformOp.TYPE_BICUBIC
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@Suppress("SpellCheckingInspection")
class WavefrontReader {

    private val textureCache = ConcurrentHashMap<String, BufferedImage>()

    @Throws(IOException::class)
    fun load(loader: ResourceLoader, path: String): Assembler {
        val t1 = System.nanoTime()
        var smooth = false
        val assembler = Assembler()
        assembler.name = path.substringBeforeLast(".")
        var material = Material.DEFAULT
        val materials = mutableMapOf<String, Material>()
        val textureLoaders = mutableListOf<TextureLoadHandler>()
        loader.stream(path) { stream ->
            stream.filter { it.isNotEmpty() }.map { it.trim() }.forEach {
                when {
                    it.startsWith("mtllib ", true) -> materials.putAll(loadMaterials(loader, it, textureLoaders))
                    it.startsWith("v ", true) -> assembler.vertex(floats(it, "v ".length))
                    it.startsWith("vt ", true) -> assembler.uv(floats(it, "vt ".length))
                    it.startsWith("vn ", true) -> assembler.normal(floats(it, "vn ".length))
                    it.startsWith("f ", true) -> assembleFace(it, smooth, material, assembler)
                    it.startsWith("s ", true) -> smooth = isSmoothing(it.substring("s ".length).trim().toLowerCase())
                    it.startsWith("g ", true) -> assembler.group = it.substring("g ".length).trim()
                    it.startsWith("o ", true) -> assembler.name = it.substring("o ".length).trim()
                    it.startsWith("usemtl ", true) -> material = materials[it.substring("usemtl ".length)] ?: Material.DEFAULT
                }
            }
        }
        loadTexturesAsync(loader, textureLoaders)
        Logger.info("loaded ${assembler.vertexCount} vertices and ${assembler.triangleCount} triangles")
        Logger.info("$path loaded in ${(System.nanoTime() - t1) / 1000000000.0} seconds")
        return assembler
    }

    private fun isSmoothing(s: String) = s == "on" || s == "1"

    private fun loadMaterials(loader: ResourceLoader, matlib: String, textures: MutableList<TextureLoadHandler>): Map<String, Material> {
        val path = matlib.substring("mtllib ".length)
        return if (loader.exists(path)) {
            loader.readLines(path)
                .filter { it.isNotBlank() }
                .joinToString(System.lineSeparator()) { it.trim() }
                .replace("newmtl", "~newmtl").split("~").map(String::lines)
                .filter { it.isNotEmpty() }
                .associate { findIn(it, "newmtl") to buildMaterial(it, textures) }
                .toMap()
        } else {
            Logger.error("material library $path not found")
            emptyMap()
        }
    }

    private fun loadTexturesAsync(loader: ResourceLoader, texturesToLoad: List<TextureLoadHandler>) {
        Thread {
            texturesToLoad.forEach {
                it.handler(textureCache.getOrPut(it.filename, { loadImageAndScale(loader, it) }))
            }
        }.start()
    }

    private fun loadImageAndScale(loader: ResourceLoader, it: TextureLoadHandler): BufferedImage {
        Logger.info("loading image ${it.filename}")
        if (loader.exists(it.filename)) {
            val image = loader.loadImage(it.filename)
            return if (image.width > 1024) {
                Logger.info("rescaling ${it.filename} from ${image.width}x${image.height} to 1024x1024")
                val w = 1024.0 / image.width
                val h = 1024.0 / image.height
                val rescaled = BufferedImage(1024, 1024, TYPE_INT_ARGB)
                val g = rescaled.graphics as Graphics2D
                g.drawImage(image, AffineTransformOp(AffineTransform.getScaleInstance(w, h), TYPE_BICUBIC), 0, 0)
                g.dispose()
                rescaled
            } else {
                image
            }
        } else {
            Logger.error("image ${it.filename} not found")
            return errorImage
        }
    }

    private fun buildMaterial(directives: List<String>, textures: MutableList<TextureLoadHandler>): Material {
        val name = findIn(directives, "newmtl", "")
        val kd = toRGB(findIn(directives, "kd", "1 1 1").split(' ').map(String::toFloat))
        val ks = toRGB(findIn(directives, "ks", "0 0 0").split(' ').map(String::toFloat))
        val ke = toRGB(findIn(directives, "ke", "0 0 0").split(' ').map(String::toFloat))
        val ns = clamp((255.0F * findIn(directives, "ns", "100.0").toFloat() / 1000.0F).toInt(), 0, 255)
        val builder = Material.Builder().name(name).diffuse(kd).specular(ns.shl(24) or ks).emissive(ke).glossiness(ns)

        val loadTexture = { key: String, cb: (s: BufferedImage) -> Unit ->
            arrayOf(findIn(directives, key)).filter { it.isNotBlank() }.forEach { textures.add(TextureLoadHandler(it, cb)) }
        }

        val material = builder.build()

        loadTexture("map_kd") { material.diffuse = Samplers.Texture(TextureMap.create(it)) }
        loadTexture("map_ks") { material.specular = Samplers.Texture(TextureMap.create(it)) }
        loadTexture("map_ke") { material.emissive = Samplers.Texture(TextureMap.create(it)) }
        loadTexture("map_rma", material::rma)

        return material
    }

    private fun findIn(items: List<String>, item: String, default: String = "") = (items.find {
        it.startsWith(item, true)
    } ?: "$item $default").substring(item.length).trim()

    private fun assembleFace(line: String, smooth: Boolean, material: Material, assembler: Assembler) {
        val vx = IntArray(3)
        val vt = IntArray(3)
        val vn = IntArray(3)

        val match = line.substring("f ".length).trim().split(' ')

        val a = match.first()
        vx[0] = extractVx(a)
        vt[0] = extractVt(a)
        vn[0] = extractVn(a)
        match.subList(1, match.size).windowed(2).forEach { (b, c) ->
            vx[1] = extractVx(b)
            vt[1] = extractVt(b)
            vn[1] = extractVn(b)
            vx[2] = extractVx(c)
            vt[2] = extractVt(c)
            vn[2] = extractVn(c)
            val triangle = assembler.triangle(vx[0], vx[1], vx[2])
            if (!vt.contains(-1)) triangle.withUvs(vt[0], vt[1], vt[2])
            when {
                !vn.contains(-1) -> triangle.withNormals(vn[0], vn[1], vn[2]).withNormalType(Normal.TRIANGLE)
                else -> triangle.withNormalType(if (smooth) Normal.VERTEX else Normal.SURFACE)
            }
            triangle.withMaterial(material)
        }
    }


    private companion object {

        val errorImage = generateErrorImage()

        fun toRGB(xyz: List<Float>) = to256(xyz[0], 16) or to256(xyz[1], 8) or to256(xyz[2])

        fun to256(x: Float, shift: Int = 0) = (clamp(x, 0.0F, 1.0F) * 0xFF).toInt() shl shift

        fun floats(line: String, offset: Int) = line.substring(offset).trim().split(' ').map(String::toFloat).toTypedArray()

        private fun extractVx(it: String) = when {
            it.contains('/') -> it.substring(0, it.indexOf('/')).trim().toInt() - 1
            else -> it.toInt() - 1
        }

        private fun extractVt(it: String): Int {
            val slash1 = it.indexOf('/')
            val slash2 = it.lastIndexOf('/')
            return when {
                slash1 != -1 && slash1 == slash2 -> tryParse(it.substring(slash1 + 1)) - 1
                slash1 != -1 && slash2 != -1 && slash1 != slash2 -> tryParse(it.substring(slash1 + 1, slash2)) - 1
                else -> -1
            }
        }

        private fun extractVn(it: String): Int {
            val slash = it.lastIndexOf('/')
            if (it.indexOf('/') == slash) return -1
            return if (slash != -1) tryParse(it.substring(slash + 1)) - 1 else -1
        }

        fun tryParse(s: String): Int {
            return try {
                val trimmed = s.trim { it <= ' ' }
                if (trimmed.isBlank()) 0 else trimmed.toInt()
            } catch (ignore: NumberFormatException) {
                0
            }
        }

        fun generateErrorImage(): BufferedImage {
            val error = BufferedImage(256, 256, TYPE_INT_ARGB)
            val g = error.graphics as Graphics2D
            g.color = Color(255, 0, 255)
            var skip = 0
            for (y in 0 until error.height step 4) {
                for (x in skip * 4 until error.width step 8) g.fillRect(x, y, 4, 4)
                skip = 1 - skip
            }
            g.dispose()
            return error
        }

        data class TextureLoadHandler(val filename: String, val handler: (s: BufferedImage) -> Unit)
    }
}
