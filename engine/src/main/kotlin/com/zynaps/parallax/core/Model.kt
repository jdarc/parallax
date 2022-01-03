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

import com.zynaps.parallax.graph.BranchNode
import com.zynaps.parallax.graph.LeafNode
import com.zynaps.parallax.graph.MaterialNode
import com.zynaps.parallax.math.Aabb
import com.zynaps.parallax.math.Matrix4

class Model(val name: String, parts: Map<Material, Mesh> = HashMap()) {

    val localBounds = Aabb()
    val vertexCount get() = parts.values.sumOf { it.sumOf { buffer -> buffer.vertexCount } }
    val triangleCount get() = parts.values.sumOf { it.sumOf { buffer -> buffer.triangleCount } }

    private val parts = HashMap<Material, ArrayList<Mesh>>()

    init {
        addAll(parts)
    }

    val materials get() = parts.keys

    operator fun get(material: Material) = parts[material]

    fun add(material: Material, mesh: Mesh) {
        parts.getOrPut(material) { ArrayList() }.add(mesh)
        localBounds.aggregate(mesh.localBounds)
    }

    fun addAll(parts: Map<Material, Mesh>) = parts.forEach { (material, mesh) -> add(material, mesh) }

    fun toGraph(transform: Matrix4 = Matrix4.IDENTITY): BranchNode {
        val modelNode = BranchNode(transform)
        modelNode.name = name
        modelNode.add(*materials.map {
            val materialNode = MaterialNode(it)
            materialNode.name = it.name
            materialNode.add(*this[it]?.map { mesh -> LeafNode(mesh) }?.toTypedArray() ?: emptyArray())
            materialNode
        }.toTypedArray())
        return modelNode
    }
}
