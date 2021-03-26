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

import com.zynaps.parallax.core.Camera
import com.zynaps.parallax.core.Containment
import com.zynaps.parallax.core.Device
import com.zynaps.parallax.core.Frustum
import com.zynaps.parallax.math.Matrix4

@Suppress("MemberVisibilityCanBePrivate", "unused")
class SceneGraph {

    private val listeners = mutableSetOf<SceneGraphListener>()

    val root = RootNode({ node, parent -> listeners.forEach { it.nodesInserted(SceneGraphEvent(node, parent)) } },
        { node, parent -> listeners.forEach { it.nodesRemoved(SceneGraphEvent(node, parent)) } },
        { node, parent -> listeners.forEach { it.structureChanged(SceneGraphEvent(node, parent)) } })

    fun addListener(l: SceneGraphListener) {
        listeners.add(l)
    }

    fun removeListener(l: SceneGraphListener) {
        listeners.remove(l)
    }

    fun update(seconds: Double) {
        root.traverseDown {
            it.update(seconds)
            it.updateTransform()
            true
        }
        root.traverseUp { it.updateWorldBounds() }
    }

    fun render(camera: Camera, device: Device, properties: Map<String, Any>? = null) =
        render(Frustum(camera.view, camera.projection), device, properties)

    fun render(view: Matrix4, projection: Matrix4, device: Device, properties: Map<String, Any>? = null) =
        render(Frustum(view, projection), device, properties)

    fun render(frustum: Frustum, device: Device, properties: Map<String, Any>? = null) {
        device.view = frustum.view
        device.projection = frustum.projection
        device.begin()
        root.traverseDown {
            it.updateProperties(properties)
            val containment = it.isContainedBy(frustum)
            if (containment != Containment.OUTSIDE) {
                device.clip = containment == Containment.PARTIAL
                it.render(device)
                true
            } else false
        }
        device.end()
    }
}
