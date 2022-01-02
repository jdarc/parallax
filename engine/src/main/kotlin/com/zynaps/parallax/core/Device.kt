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

import com.zynaps.parallax.math.Matrix4
import com.zynaps.parallax.math.Scalar.max
import com.zynaps.parallax.math.Scalar.min
import com.zynaps.parallax.system.Parallel

class Device(val bitmap: Bitmap) {

    val width = bitmap.width
    val height = bitmap.height

    val stats = RenderStats()

    var material = Material.DEFAULT
    var clip = false

    private var cullFn = ::renderFront
    var cullMode = CullMode.BACK
        set(value) {
            field = value
            cullFn = if (field == CullMode.BACK) ::renderFront else ::renderBack
        }

    var rasterizer: Rasterizer = CompoundRasterizer(DepthRasterizer(), ColorRasterizer())

    var world = Matrix4.IDENTITY
    var view = Matrix4.IDENTITY
    var projection = Matrix4.IDENTITY

    val colorBuffer = RenderBuffer.wrap(bitmap.data, bitmap.width)
    val specularBuffer = RenderBuffer.create(width, height)
    val normalBuffer = RenderBuffer.create(width, height)
    val emissiveBuffer = RenderBuffer.create(width, height)
    val depthBuffer = FloatArray(width * height)

    internal val spanBuffers = Array(Parallel.CPUS) { SpanBuffer(width, height) }

    constructor(width: Int, height: Int) : this(Bitmap(width, height))

    fun clear(color: Int = 0x000000, depth: Float = 1.0F) = rasterizer.clear(this, color, depth)

    fun begin() = stats.start()

    fun draw(vertexBuffer: FloatArray, indexBuffer: IntArray, count: Int) {
        stats.vertices += count * 3
        stats.triangles += count
        val transform = projection * view * world
        val normal = Matrix4.createNormalTransform(world)
        Parallel.partition(count) { index, from, to -> drawChunk(index, from, to, indexBuffer, vertexBuffer, transform, normal) }
    }

    private fun drawChunk(
        index: Int,
        from: Int,
        to: Int,
        indexBuffer: IntArray,
        vertexBuffer: FloatArray,
        w: Matrix4,
        n: Matrix4
    ) {
        val p0 = Vertex()
        val p1 = Vertex()
        val p2 = Vertex()
        val e0 = Edge()
        val e1 = Edge()
        val e2 = Edge()
        val g = Gradients()
        val clipper = Clipper()
        val stack = spanBuffers[index]
        val renderFn = if (clip) {
            { cullFn(stack, clipper.clip(p0, p1, p2), g, e0, e1, e2, clipper.a, clipper.b, clipper.c, clipper.d, clipper.e) }
        } else {
            { cullFn(stack, 3, g, e0, e1, e2, p0, p1, p2, p2, p2) }
        }
        for (p in from * 3 until to * 3 step 3) {
            p0.transform(indexBuffer[p + 0] shl 3, vertexBuffer, w, n)
            p1.transform(indexBuffer[p + 1] shl 3, vertexBuffer, w, n)
            p2.transform(indexBuffer[p + 2] shl 3, vertexBuffer, w, n)
            renderFn()
        }
    }

    fun end() {
        rasterizer.render(this)
        spanBuffers.forEach { it.reset() }
        stats.stop()
    }

    private fun renderBack(
        stack: SpanBuffer,
        delta: Int,
        g: Gradients,
        e0: Edge,
        e1: Edge,
        e2: Edge,
        a: Vertex,
        b: Vertex,
        c: Vertex,
        d: Vertex,
        e: Vertex
    ) =
        when (delta) {
            3 -> renderFront(stack, delta, g, e0, e1, e2, c, b, a, a, a)
            4 -> renderFront(stack, delta, g, e0, e1, e2, d, c, b, a, a)
            else -> renderFront(stack, delta, g, e0, e1, e2, e, d, c, b, a)
        }

    private fun renderFront(
        stack: SpanBuffer,
        delta: Int,
        g: Gradients,
        e0: Edge,
        e1: Edge,
        e2: Edge,
        a: Vertex,
        b: Vertex,
        c: Vertex,
        d: Vertex,
        e: Vertex
    ) {
        if (delta < 3 || backFacing(a, b, c)) return

        a.scale(width, height)
        b.scale(width, height)
        c.scale(width, height)

        g.configure(a, b, c)
        edgeSort(stack, g, e0, e1, e2, a, b, c)

        if (delta < 4) return
        d.scale(width, height)
        edgeSort(stack, g, e0, e1, e2, a, c, d)

        if (delta < 5) return
        e.scale(width, height)
        edgeSort(stack, g, e0, e1, e2, a, d, e)
    }

    private fun edgeSort(stack: SpanBuffer, g: Gradients, e0: Edge, e1: Edge, e2: Edge, a: Vertex, b: Vertex, c: Vertex) {
        if (max(a.vx, max(b.vx, c.vx)) < 0.0F || min(a.vx, min(b.vx, c.vx)) >= width) return
        if (a.vy < b.vy) when {
            c.vy < a.vy -> triangle(stack, g, e0, e1, e2, c, a, b, e1, e0, e2, e0)
            b.vy < c.vy -> triangle(stack, g, e0, e1, e2, a, b, c, e1, e0, e2, e0)
            else -> triangle(stack, g, e0, e1, e2, a, c, b, e0, e1, e0, e2)
        } else when {
            c.vy < b.vy -> triangle(stack, g, e0, e1, e2, c, b, a, e0, e1, e0, e2)
            a.vy < c.vy -> triangle(stack, g, e0, e1, e2, b, a, c, e0, e1, e0, e2)
            else -> triangle(stack, g, e0, e1, e2, b, c, a, e1, e0, e2, e0)
        }
    }

    private fun triangle(
        s: SpanBuffer,
        g: Gradients,
        ttb: Edge,
        ttm: Edge,
        mtb: Edge,
        a: Vertex,
        b: Vertex,
        c: Vertex,
        e00: Edge,
        e01: Edge,
        e10: Edge,
        e11: Edge
    ) {
        if (c.vy < 0 || a.vy >= height) return
        if (ttb.configure(g, a, c) > 0) {
            if (ttm.configure(g, a, b) > 0 && ttm.y1 < height) {
                s.add(material, g, e00, e01, ttm)
            }
            if (mtb.configure(g, b, c) > 0 && mtb.y1 < height) {
                s.add(material, g, e10, e11, mtb)
            }
        }
    }

    companion object {
        private fun backFacing(a: Vertex, b: Vertex, c: Vertex): Boolean {
            val bvw = 1.0F / b.vw
            val cvw = 1.0F / c.vw
            val avy = a.vy / a.vw
            val avx = a.vx / a.vw
            return (avy - b.vy * bvw) * (c.vx * cvw - avx) <= (avy - c.vy * cvw) * (b.vx * bvw - avx)
        }
    }
}
