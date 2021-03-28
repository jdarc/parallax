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
package com.zynaps.parallax.graph

import com.zynaps.parallax.math.Aabb
import com.zynaps.parallax.math.Matrix4

open class BranchNode(transform: Matrix4 = Matrix4.IDENTITY) : Node(transform) {

    protected val children = mutableListOf<Node>()

    val childCount get() = children.size

    fun indexOf(child: Any?) = children.indexOf(child)

    fun add(vararg nodes: Node) = nodes.forEach { if (it != this && !it.isParent(this)) it.attachToParent(this) }

    fun remove(vararg nodes: Node) = nodes.forEach { if (it.isParent(this)) it.detachFromParent() }

    operator fun get(index: Int) = children[index]

    override val localBounds get() = children.fold(Aabb(), { dst, src -> dst.aggregate(src.localBounds) })

    override fun traverseUp(visitor: (Node) -> Unit) {
        children.forEach { it.traverseUp(visitor) }
        visitor(this)
    }

    override fun traverseDown(visitor: (Node) -> Boolean) {
        if (!visitor(this)) return
        children.forEach { it.traverseDown(visitor) }
    }

    override fun updateWorldBounds() {
        worldBounds.reset()
        children.forEach { worldBounds.aggregate(it.worldBounds) }
    }

    override fun childAttached(node: Node) {
        children.add(node)
        super.childAttached(node)
    }

    override fun childDetached(node: Node) {
        children.remove(node)
        super.childDetached(node)
    }

    override fun clone(): Node {
        val clone = BranchNode(localMatrix)
        clone.add(*children.map { it.clone() }.toTypedArray())
        return clone
    }
}
