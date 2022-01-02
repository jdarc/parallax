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
package demo.inspector

import com.zynaps.parallax.core.Camera
import com.zynaps.parallax.core.Light
import com.zynaps.parallax.graph.LeafNode
import com.zynaps.parallax.graph.SceneGraph
import com.zynaps.parallax.io.Importer
import com.zynaps.parallax.math.Matrix4
import com.zynaps.parallax.math.Scalar.max
import com.zynaps.parallax.math.Vector3
import com.zynaps.parallax.toolkit.Controller
import com.zynaps.parallax.toolkit.OrbitController
import com.zynaps.parallax.toolkit.Visualizer
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JComponent

class Viewer(private val sceneGraph: SceneGraph) : JComponent() {
    private var camera: Camera
    private var visualizer: Visualizer
    private var controller: Controller

    init {
        val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        val width = (gd.displayMode.width * 2) / 3
        val height = (gd.displayMode.height * 2) / 3

        font = Font("Space Mono", Font.PLAIN, 15)

        background = Color(0x54445b).darker().darker().darker()
        preferredSize = Dimension(width, height)

        val fsaa = 1.0
        visualizer = Visualizer((width * fsaa).toInt(), (height * fsaa).toInt())
        visualizer.backgroundColor = background.rgb
        visualizer.lights.add(Light().shadowMapResolution(1024).moveTo(0.0F, 0.0F, 50.0F))
        visualizer.lights[0].castShadows = false

        camera = Camera.Perspective(aspect = width.toFloat() / height.toFloat(), near = 1.0F, far = 500.0F)
        camera.moveTo(50.0F, 0.0F, 0.0F)
        controller = OrbitController(camera)

        load(File("/home/jean/Projects/kotlin/parallax/demos/src/main/resources/models/axe.obj"))

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                if (camera is Camera.Perspective) {
                    (camera as Camera.Perspective).aspectRatio = e.component.width.toFloat() / e.component.height.toFloat()
                }
            }
        })
    }

    fun clear() = sceneGraph.root.clear()

    fun load(file: File) {
        val geoNode = Importer.load(file)
        val localBounds = geoNode.localBounds
        val t = localBounds.center
        val s = 20.0F / max(localBounds.maximum.z, max(localBounds.maximum.x, localBounds.maximum.y))
        geoNode.localMatrix = Matrix4.createFromPositionRotationScale(-t * s, Matrix4.IDENTITY, Vector3(s, s, s))
        sceneGraph.root.add(geoNode)
    }

    fun update(seconds: Double) {
        if (seconds <= 0) return
        controller.update(seconds, 100.0F)
        visualizer.lights[0].moveTo(camera.position.x, camera.position.y, camera.position.z)
        sceneGraph.update(seconds)
    }

    fun render() = visualizer.render(sceneGraph, camera)

    override fun paintComponent(g: Graphics) = visualizer.present(g as Graphics2D, 0, 0, width, height)

    fun handleEvent(event: AWTEvent) {
        if (event is MouseEvent && mousePosition == null) return
        controller.handleEvent(event)
    }

    fun stats(): Stats {
        var vertices = 0
        var triangles = 0
        sceneGraph.root.traverseDown {
            if (it is LeafNode) {
                vertices += it.geometry.vertexCount
                triangles += it.geometry.triangleCount
            }
            true
        }
        return Stats(vertices, triangles)
    }
}
