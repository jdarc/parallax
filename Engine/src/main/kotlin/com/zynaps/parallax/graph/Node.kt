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

import com.zynaps.parallax.core.Device
import com.zynaps.parallax.core.Frustum
import com.zynaps.parallax.math.Aabb
import com.zynaps.parallax.math.Matrix4

abstract class Node(transform: Matrix4 = Matrix4.IDENTITY) {

    private var parent: Node? = null

    open var name = "${javaClass.simpleName}_${this.hashCode()}"

    abstract val localBounds: Aabb
    val worldBounds = Aabb()

    var localMatrix = transform
    var worldMatrix = Matrix4.IDENTITY

    fun isParent(node: Node) = parent === node

    fun isContainedBy(frustum: Frustum) = frustum.evaluate(worldBounds)

    fun attachToParent(node: Node) {
        detachFromParent()
        parent = node
        node.childAttached(this)
    }

    fun detachFromParent() {
        val oldParent = parent
        parent = null
        oldParent?.childDetached(this)
    }

    open fun traverseUp(visitor: (Node) -> Unit) {
        visitor(this)
    }

    open fun traverseDown(visitor: (Node) -> Boolean) {
        visitor(this)
    }

    open fun updateProperties(properties: Map<String, Any>?) = Unit

    open fun updateTransform() {
        worldMatrix = (parent?.worldMatrix ?: Matrix4.IDENTITY) * localMatrix
    }

    abstract fun updateWorldBounds()

    open fun bubbleAttach(node: Node, parent: Node) {
        this.parent?.bubbleAttach(node, parent)
    }

    open fun bubbleDetach(node: Node, parent: Node) {
        this.parent?.bubbleDetach(node, parent)
    }

    open fun childAttached(node: Node) = bubbleAttach(node, this)
    open fun childDetached(node: Node) = bubbleDetach(node, this)

    open fun update(seconds: Double) = Unit
    open fun render(device: Device) = Unit

    override fun toString() = name

    abstract fun clone(): Node
}
