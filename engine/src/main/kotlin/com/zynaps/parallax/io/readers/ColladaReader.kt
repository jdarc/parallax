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

import com.zynaps.parallax.core.Assembler
import com.zynaps.parallax.core.Material
import com.zynaps.parallax.core.Model
import com.zynaps.parallax.graph.BranchNode
import com.zynaps.parallax.io.ResourceLoader
import com.zynaps.parallax.io.readers.Collada.*
import com.zynaps.parallax.math.Vector3
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class ColladaReader {

    @Throws(IOException::class)
    fun load(loader: ResourceLoader, name: String): BranchNode {
        val bytes = ByteArrayInputStream(loader.readBytes(name))
        val collada = Collada(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bytes))
        val materials = compileMaterials(collada.libraryEffects, collada.libraryMaterials)
        val models = compileModels(collada.libraryGeometries, materials)
        val node = BranchNode()
        collada.libraryVisualScenes.visualScenes.forEach { processNodes(models, it.nodeList, node) }
        return node
    }

    private fun processNodes(geometry: Map<String, Model>, importNodes: List<CNode>, nextNode: BranchNode) {
        importNodes.forEach {
            val childNode = (geometry[it.instanceGeometryList.first().url.substring(1)] ?: error("")).toGraph(it.matrix)
            processNodes(geometry, it.nodeList, childNode)
            nextNode.add(childNode)
        }
    }

    private fun compileMaterials(effects: LibraryEffects, materials: LibraryMaterials): Map<String, Material> {
        val effectsGroup = effects.effectList.map { it.id to it.phongDiffuse }.toMap()
        return materials.materialList.map {
            val v = effectsGroup[it.instanceEffect.url.substring(1)] ?: Vector3.ZERO
            val red = (0xFF * v.x).toInt() shl 16
            val grn = (0xFF * v.y).toInt() shl 8
            val blu = (0xFF * v.z).toInt()
            it.id to Material.Builder().name(it.name).diffuse(red or grn or blu).build()
        }.toMap()
    }

    private fun compileModels(geometries: LibraryGeometries, materials: Map<String, Material>): Map<String, Model> {
        val result = HashMap<String, Model>()
        geometries.geometryList.forEach {
            val assembler = Assembler()
            val vertices = it.mesh.vertices.input.first { e -> e.semantic == "POSITION" }.source.substring(1)
            for (source in it.mesh.source) {
                if (source.id == vertices) {
                    val elements = source.floatArray
                    for (i in 0 until elements.count step 3) {
                        assembler.vertex(elements.elements[i], elements.elements[i + 1], elements.elements[i + 2])
                    }
                }
            }

            for (polyList in it.mesh.polylist) {
                val n = if (polyList.input.count { e -> e.semantic == "NORMAL" } > 0) 1 else 0
                val c = if (polyList.input.count { e -> e.semantic == "COLOR" } > 0) 1 else 0
                val delta = n + c + 1
                val material = materials[polyList.material] ?: Material.DEFAULT
                for (offset in polyList.p.indices step 3 * delta) {
                    val i0 = polyList.p[offset + 0 * delta]
                    val i1 = polyList.p[offset + 1 * delta]
                    val i2 = polyList.p[offset + 2 * delta]
                    assembler.triangle(i0, i1, i2).material = material
                }
            }

            result.putIfAbsent(it.id, assembler.compile())
        }
        return result
    }
}
