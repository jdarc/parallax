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

import com.zynaps.parallax.core.Bitmap
import com.zynaps.parallax.core.Camera
import com.zynaps.parallax.core.Device
import com.zynaps.parallax.core.Light
import com.zynaps.parallax.filters.BlurFilter
import com.zynaps.parallax.filters.GammaFilter
import com.zynaps.parallax.graph.SceneGraph
import com.zynaps.parallax.postfx.GlowProcessor
import com.zynaps.parallax.postfx.LightProcessor
import java.awt.Graphics2D

class Visualizer(val width: Int, val height: Int) {

    var device = Device(Bitmap(width, height))
    val stats get() = device.stats
    val lights = ArrayList<Light>()
    var ambientColor: Int = 0x404040
    var backgroundColor: Int = 0x000000

    private var lightProcessor = LightProcessor(device)
    private var glowProcessor = GlowProcessor()

    private val blurFilter = BlurFilter(device.colorBuffer)
    private val gammaFilter = GammaFilter(device.colorBuffer)

    fun render(sceneGraph: SceneGraph, camera: Camera) = render(camera, sceneGraph)

    fun present(g: Graphics2D, x: Int = 0, y: Int = 0, width: Int = this.width, height: Int = this.height) {
        device.bitmap.draw(g, x, y, width, height)
    }

    private fun render(camera: Camera, sceneGraph: SceneGraph) {
        device.clear(backgroundColor)
        sceneGraph.render(camera, device)

        lights.forEach { it.render(sceneGraph) }
        lightProcessor.lights = lights
        lightProcessor.camera = camera
        lightProcessor.ambientRGB = ambientColor
        lightProcessor.apply()

//        glowProcessor.source = device.emissiveBuffer
//        glowProcessor.destination = device.colorBuffer
//        glowProcessor.apply()

//        blurFilter.radius = 2
//        blurFilter.steps = 1
//        blurFilter.apply()

        gammaFilter.apply()
    }
}
