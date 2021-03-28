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

import com.zynaps.parallax.graph.SceneGraph
import com.zynaps.parallax.math.Matrix4
import com.zynaps.parallax.math.Scalar
import com.zynaps.parallax.math.Scalar.clamp
import com.zynaps.parallax.math.Scalar.max
import com.zynaps.parallax.math.Vector3

class Light(color: Int = 0xFFFFFF) {
    private var pos = Vector3(0.0F, 100.0F, 0.0F)
    private var dir = Vector3.ZERO
    private var tol = (pos - dir).normalize()
    private var combined = Matrix4.IDENTITY
    private var device = Device(2, 2)
    private var resolution: Int = 512

    var red = Color.red(color)
        set(value) {
            field = clamp(value, 0, 255)
        }

    var grn = Color.grn(color)
        set(value) {
            field = clamp(value, 0, 255)
        }

    var blu = Color.blu(color)
        set(value) {
            field = clamp(value, 0, 255)
        }

    var color = Color.pack(red, grn, blu)
        set(value) {
            red = Color.red(value)
            grn = Color.grn(value)
            blu = Color.blu(value)
        }

    var size = 100.0F
        set(value) {
            field = clamp(value / 2.0F, 1.0F, Float.MAX_VALUE)
        }

    var near = 1.0F
        set(value) {
            field = clamp(value, 1.0F, Float.MAX_VALUE)
        }

    var far = 10000.0F
        set(value) {
            field = clamp(value, 2.0F, Float.MAX_VALUE)
        }

    val position get() = pos

    val direction get() = dir

    val toLight get() = tol

    var castShadows = false

    fun shadowMapResolution(value: Int = 512): Light {
        resolution = max(128, if (Scalar.isPot(value)) value else 512)
        return this
    }

    fun moveTo(x: Float, y: Float, z: Float): Light {
        pos = Vector3(x, y, z)
        tol = (pos - dir).normalize()
        return this
    }

    fun directAt(x: Float, y: Float, z: Float): Light {
        dir = Vector3(x, y, z)
        tol = (pos - dir).normalize()
        return this
    }

    fun trace(nx: Float, ny: Float, nz: Float, wx: Float, wy: Float, wz: Float): Float {
        return clamp(tol.dot(nx, ny, nz) * if (castShadows) sample(wx, wy, wz) else 1.0F, 0.0F, 1.0F)
    }

    fun render(sceneGraph: SceneGraph) {
        if (!castShadows) return
        if (resolution != device.width) {
            device = Device(resolution, resolution)
            device.rasterizer = DepthRasterizer()
        }
        val up = if (Vector3.UNIT_Y.dot(tol) == 1.0F) Vector3.UNIT_X else Vector3.UNIT_Y
        val view = Matrix4.createLookAt(pos, dir, up)
        val proj = Matrix4.createOrthographic(-size, size, -size, size, near, far)
        combined = proj * view
        device.projection = proj
        device.cullMode = CullMode.FRONT
        device.clear()
        sceneGraph.render(view, proj, device)
    }

    private fun sample(wx: Float, wy: Float, wz: Float): Float {
        val vx = combined.m00 * wx + combined.m01 * wy + combined.m02 * wz + combined.m03
        val vy = combined.m10 * wx + combined.m11 * wy + combined.m12 * wz + combined.m13
        val vz = combined.m20 * wx + combined.m21 * wy + combined.m22 * wz + combined.m23
        val vw = 0.5F / (combined.m30 * wx + combined.m31 * wy + combined.m32 * wz + combined.m33)
        val x = 0.5F + vx * vw
        val y = 0.5F - vy * vw
        if (x !in 0.0F..1.0F || y !in 0.0F..1.0F) return 0.0F
        val z = 0.5F + vz * vw
        val sx = clamp((resolution * x).toInt(), 1, resolution - 2)
        val sy = clamp((resolution * y).toInt(), 1, resolution - 2)
        val mem1 = sy * resolution + sx - 1
        val mem0 = mem1 - resolution
        val mem2 = mem1 + resolution
        var acc = 0.0F
        for (t in 0..2) {
            if (z < device.depthBuffer[mem0 + t]) acc += 0.11111F
            if (z < device.depthBuffer[mem1 + t]) acc += 0.11111F
            if (z < device.depthBuffer[mem2 + t]) acc += 0.11111F
        }
        return acc
    }
}
