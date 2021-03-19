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

import com.zynaps.parallax.math.Matrix4
import com.zynaps.parallax.math.Vector3
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

internal class Collada(document: Document) {

    val libraryEffects = LibraryEffects(document.selectNode("//library_effects"))
    val libraryMaterials = LibraryMaterials(document.selectNode("//library_materials"))
    val libraryGeometries = LibraryGeometries(document.selectNode("//library_geometries"))
    val libraryVisualScenes = LibraryVisualScenes(document.selectNode("//library_visual_scenes"))

    class BindMaterial(node: Node?) {
        val techniqueCommon = TechniqueCommon(node?.selectNode("technique_common"))
    }

    class CFloatArray(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val elements = node?.textContent?.trim()?.split(' ')?.map { it.toFloat() } ?: emptyList()
        val count = elements.size
    }

    class CNode(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val sid = node["sid"]
        val type = node["type", "NODE"]
        val lookat = toMatrix(node?.selectNode("lookat"))
        val matrix = toMatrix(node?.selectNode("matrix"))
        val rotate = toMatrix(node?.selectNode("rotate"))
        val scale = toMatrix(node?.selectNode("scale"))
        val skew = toMatrix(node?.selectNode("skew"))
        val translate = toMatrix(node?.selectNode("translate"))
        val instanceGeometryList = node?.selectNodes("instance_geometry")?.map { InstanceGeometry(it) }?.toList() ?: emptyList()
        val nodeList = node?.selectNodes("node")?.map { CNode(it) }?.toList() ?: emptyList()

        private companion object {
            fun toMatrix(node: Node?): Matrix4 {
                val v = (node?.textContent ?: "1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1").trim().split(' ').map { it.toFloat() }
                return Matrix4(v[0], v[4], v[8], v[12], v[1], v[5], v[9], v[13], v[2], v[6], v[10], v[14], v[3], v[7], v[11], v[15])
            }
        }
    }

    class Effect(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val array = node?.selectNode(PHONG_DIFFUSE)?.textContent?.split(' ')?.map { it.toFloat() }?.toFloatArray() ?: FloatArray(3)
        val phongDiffuse = Vector3(array.getOrElse(0) { 0.0F }, array.getOrElse(1) { 0.0F }, array.getOrElse(2) { 0.0F })

        private companion object {
            const val PHONG_DIFFUSE = "profile_COMMON/technique[@sid='common']/phong/diffuse/color[@sid='diffuse']"
        }
    }

    class Geometry(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val mesh = Mesh(node?.selectNode("mesh"))
    }

    class Input(node: Node) {
        val semantic = node["semantic"]
        val source = node["source"]
        val offset = node["offset", "0"].toInt()
    }

    class InstanceEffect(node: Node?) {
        val sid = node["sid"]
        val name = node["name"]
        val url = node["url"]
    }

    class InstanceGeometry(node: Node?) {
        val sid = node["sid"]
        val name = node["name"]
        val url = node["url"]
        val bindMaterial = BindMaterial(node?.selectNode("bind_material"))
    }

    class InstanceMaterial(node: Node?) {
        val sid = node["sid"]
        val name = node["name"]
        val target = node["target"]
        val symbol = node["symbol"]
    }

    class LibraryEffects(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val effectList = node?.selectNodes("effect")?.map { Effect(it) }?.toList() ?: emptyList()
    }

    class LibraryGeometries(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val geometryList = node?.selectNodes("geometry")?.map { Geometry(it) }?.toList() ?: emptyList()
    }

    class LibraryMaterials(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val materialList = node?.selectNodes("material")?.map { Material(it) }?.toList() ?: emptyList()
    }

    class LibraryVisualScenes(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val visualScenes = node?.selectNodes("visual_scene")?.map { VisualScene(it) }?.toList() ?: emptyList()
    }

    class Material(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val instanceEffect = InstanceEffect(node?.selectNode("instance_effect"))
    }

    class Mesh(node: Node?) {
        val vertices = Vertices(node?.selectNode("vertices"))
        val source = node?.selectNodes("source")?.map { Source(it) }?.toList() ?: emptyList()
        val polylist = node?.selectNodes("polylist")?.map { Polylist(it) }?.toList() ?: emptyList()
    }

    class Polylist(node: Node?) {
        val count = node["count"].toInt()
        val name = node["name"]
        val material = node["material"]
        val input = node?.selectNodes("input")?.map { Input(it) }?.toList() ?: emptyList()
        val vcount = node?.selectNode("vcount")?.textContent?.trim()?.split(' ')?.map { it.toInt() }?.toTypedArray() ?: emptyArray()
        val p = node?.selectNode("p")?.textContent?.trim()?.split(' ')?.map { it.toInt() }?.toTypedArray() ?: emptyArray()
    }

    class Source(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val floatArray = CFloatArray(node?.selectNode("float_array"))
    }

    class TechniqueCommon(node: Node?) {
        val instanceMaterialList = node?.selectNodes("instance_material")?.map { InstanceMaterial(it) }?.toList() ?: emptyList()
    }

    class Vertices(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val input = node?.selectNodes("input")?.map { Input(it) }?.toList() ?: emptyList()
    }

    class VisualScene(node: Node?) {
        val id = node["id"]
        val name = node["name"]
        val nodeList = node?.selectNodes("node")?.map { CNode(it) }?.toList() ?: emptyList()
    }

    private companion object {

        operator fun Node?.get(name: String, alt: String = "") = this?.attributes?.getNamedItem(name)?.nodeValue ?: alt

        fun Node.selectNode(xpath: String) = XPathFactory.newInstance().newXPath().evaluate(xpath, this, XPathConstants.NODE) as Node?

        fun Node.selectNodes(xpath: String): Sequence<Node> {
            val nodes = XPathFactory.newInstance().newXPath().evaluate(xpath, this, XPathConstants.NODESET) as NodeList
            return object : Sequence<Node> {
                override fun iterator(): Iterator<Node> = object : Iterator<Node> {
                    private var index: Int = 0
                    override fun hasNext() = index < nodes.length
                    override fun next() = nodes.item(index++)
                }
            }
        }
    }
}

