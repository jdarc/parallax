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
package demo.runaround

import com.zynaps.parallax.core.Camera
import com.zynaps.parallax.core.Light
import com.zynaps.parallax.graph.SceneGraph
import com.zynaps.parallax.io.ResourceLoader
import com.zynaps.parallax.math.Vector3
import com.zynaps.parallax.toolkit.Controller
import com.zynaps.parallax.toolkit.FPSController
import com.zynaps.parallax.toolkit.Visualizer
import demo.FPSCounter
import demo.Scenery
import java.awt.*
import java.io.File
import kotlin.math.roundToInt

class Viewer : Canvas() {

    private var sceneGraph: SceneGraph
    private var camera: Camera
    private var visualizer: Visualizer
    private var controller: Controller
    private var fpsCounter: FPSCounter

    init {
        val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        val width = (gd.displayMode.width * 2) / 3
        val height = (gd.displayMode.height * 2) / 3

        font = Font("Space Mono", Font.PLAIN, 15)

        background = Color(0x54445b)
        size = Dimension(width, height)
        ignoreRepaint = true

        fpsCounter = FPSCounter()

        val fsaa = 1.0

        visualizer = Visualizer((width * fsaa).toInt(), (height * fsaa).toInt())
        visualizer.backgroundColor = background.rgb
        visualizer.lights.add(Light().shadowMapResolution(1024).moveTo(-1800.0F, 2500.0F, 1000.0F))
        visualizer.lights.add(Light().shadowMapResolution(1024).moveTo( 1800.0F, 2500.0F, 1000.0F))
        visualizer.lights[0].castShadows = true
        visualizer.lights[1].color = 0x8833f0
        visualizer.lights[1].castShadows = true

        camera = Camera.Perspective(aspect = width / height.toFloat(), near = 1.0F, far = 2000.0F)
        camera.moveTo(-40.0F * 1.0F, 20.0F * 1.0F, 50.0F * 1.0F)
        controller = FPSController(camera)

        sceneGraph = createScene()

//        orthoShader = object : Shader {
//            var alpha = 0xFF
//            override fun execute(buffers: Array<RenderBuffer>, samplers: Array<Sampler>, parameters: Parameters) {
//                val rgb1 = samplers[0].sample(parameters.tu, parameters.tv)
//                val rgb2 = buffers[0][parameters.offset]
//                buffers[0][parameters.offset] = com.zynaps.parallax.core.Color.blend(rgb1, rgb2, alpha)
//            }
//        }
//        sceneGraph.root.add(generateOrthoNode())
    }

    fun update(seconds: Double) {
        if (seconds <= 0) return
        fpsCounter.add(seconds)
        controller.update(seconds, 100.0F)
        sceneGraph.update(seconds)
        visualizer.render(sceneGraph, camera)
    }

    fun render() {
        if (bufferStrategy == null) {
            createBufferStrategy(3)
            return
        }

        if (bufferStrategy.contentsLost()) return

        val g = bufferStrategy.drawGraphics as Graphics2D
        visualizer.present(g, 0, 0, width, height)

//        renderOrthographic()

        renderStats(g)
        g.dispose()

        bufferStrategy.show()
    }

    private fun createScene(): SceneGraph {
        val scene = SceneGraph()
        val sceneMaker = Scenery(scene, ResourceLoader(File(javaClass.classLoader.getResource("models").toURI()).path))
        sceneMaker.addPlane()
//        sceneMaker.addCube(20.0F)
//        sceneMaker.addSphere(12.0F)
//        sceneMaker.addCapsule(10.0F)
//        sceneMaker.addRandomSpinners()
//        sceneMaker.addSpecialThing()
//        sceneMaker.addImported("dwarf.obj", Vector3(0.0F, -10.0F, 0.0F))
        sceneMaker.addImported("attack-droid.obj", Vector3(25.0F, 21.8F, -26.0F))
        sceneMaker.addImported("simba.obj", Vector3(0F, -3F, 26.0F), 8F)
//        sceneMaker.addImported("grunt.obj", Vector3(-25.0F, 10.0F, 26.0F))
//        sceneMaker.addImported("fell-lord.obj", Vector3(-2.5F, 30.0F, 2.6.0F), 20.0F)
//        sceneMaker.addImported("chipmunk.obj", Vector3(0.0F, -10.0F, 0.0F))
//        sceneMaker.addStressTest("simba.obj")
//        sceneMaker.colladaTest("aircar.dae")
//        sceneMaker.addTerrain()

//        sceneMaker.addClipStressTest("dwarf.obj")
        return scene
    }

    private fun drawString(g: Graphics2D, x: Int, y: Int, text: String, color: Color = Color.YELLOW) {
        g.color = color
        g.font = font
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.drawString(text, x, y)
    }

    private fun renderStats(g: Graphics2D) {
        val top = 30
        val right = 20
        val fontSize = (font.size * 1.2F).roundToInt()
        val fontMetrics = g.getFontMetrics(font)

        val stats = HashMap<String, Any>()
        stats["Vertices"] = visualizer.stats.vertices
        stats["Triangles"] = visualizer.stats.triangles
        stats["Rendered"] = visualizer.stats.rendered
        stats["Clipped"] = visualizer.stats.clipped
        stats["Culled"] = visualizer.stats.culled
        stats["Shaded"] = visualizer.stats.shaded
        stats["Overdraw"] = visualizer.stats.overdraw
        stats["Spans"] = visualizer.stats.spans
        stats["Time"] = "%.2f".format(visualizer.stats.nanoseconds / 1000000.0)
        stats["Frames/Sec"] = fpsCounter.average()

        val maxTitleWidth = right + (stats.map { fontMetrics.stringWidth(it.key) }.maxOrNull() ?: 0)

        g.paint = Color(0.0F, 0.0F, 0.0F, 0.5F)
        g.fillRect(1, 1, 270, 230)

        arrayOf("Vertices", "Triangles", "Rendered", "Clipped", "Culled", "Shaded", "Overdraw", "Time", "Spans", "", "Frames/Sec")
            .forEachIndexed { index, title ->
                if (title.isNotBlank()) {
                    val x = maxTitleWidth - fontMetrics.stringWidth(title)
                    drawString(g, x, top + fontSize * index, "${title}: ${stats[title]}")
                }
            }
    }


//    private val orthoRasterize = Device(320, 200)
//    private val orthoColorBuffer = RenderBuffer.create(320, 200)
//    private val orthoDepthBuffer = RenderBuffer.create(320, 200)
//    private val orthoRaster = Bitmap(320, 200)
//    private val orthoCamera = Camera.Orthographic(orthoRasterize.width.toFloat(), orthoRasterize.height.toFloat(), 1.0F, 10000.0F)
//    private val orthoShader: Shader

//    private fun generateOrthoNode(): Node {
//        BranchNode().add(object : BranchNode() {
//            var render = false
//            var model: Model? = null
//
//            override fun updateProperties(properties: Map<String, Any>?) {
//                render = (properties?.getOrDefault("ortho", false) ?: false) as Boolean
//            }
//
//            override val localBounds get() = model?.bounds ?: Aabb()
//
//            override fun render(device: Device) {
//                if (render) {
//                    orthoShader.alpha = 127
//                    model = generateFrustumModel(camera)
//                    model!!.materials.forEach { material ->
//                        model!![material]?.forEach { mesh ->
//                            device.bindSampler(0, material.diffuse)
//                            mesh.draw(device)
//                        }
//                    }
//                    orthoShader.alpha = 255
//                }
//            }
//        })
//        return BranchNode()
//    }

//    private fun renderOrthographic() {
//        orthoCamera.moveTo(0.0F, 1000.0F, 0.0F)
//        orthoCamera.lookAt(0.0F, 0.0F, 0.0F)
//        orthoCamera.worldUp(0.0F, 0.0F, -1.0F)
//
//        orthoCamera.moveTo(1000.0F, 0.0F, 0.0F)
//        orthoCamera.lookAt(0.0F, 0.0F, 0.0F)
//        orthoCamera.worldUp(0.0F, 1.0F, 0.0F)
//
//        val frustum = Frustum(orthoCamera.view, orthoCamera.projection)
//
//        orthoRasterize.shader = orthoShader
//        orthoColorBuffer.fill(0x54445b shr 1 and 0x7f7f7f)
//        orthoRasterize.bindBuffer(0, orthoColorBuffer)
//        orthoRasterize.bindBuffer(7, orthoDepthBuffer)
//        sceneGraph.render(frustum, orthoRasterize, mapOf("ortho" to true))
//
//        Graphics.blit(orthoColorBuffer, orthoRaster)
//
//        val graphics = bitmap.image.graphics
//        graphics.color = Color.BLACK
//        graphics.drawImage(orthoRaster.image, 0, bitmap.height - 200, null)
//        graphics.drawRect(0, bitmap.height - orthoRasterize.height, orthoRasterize.width - 1, orthoRasterize.height - 1)
//        graphics.dispose()
//    }

//    private fun generateFrustumModel(camera: Camera): Model {
//        val blue = Material.Builder().diffuse(0x0000FF).build()
//        val red = Material.Builder().diffuse(0xFF0000).build()
//        val green = Material.Builder().diffuse(0xFF00FF).build()
//
//        val frustum = Frustum(camera.view, camera.projection)
//        val volume = Frustum.computeVolume(frustum)
//        val assembler = Assembler()
//        assembler.vertex(volume[0])
//        assembler.vertex(volume[1])
//        assembler.vertex(volume[2])
//        assembler.vertex(volume[3])
//        assembler.vertex(volume[4])
//        assembler.vertex(volume[5])
//        assembler.vertex(volume[6])
//        assembler.vertex(volume[7])
//        assembler.triangle(0, 1, 2).withMaterial(blue)
//        assembler.triangle(2, 3, 0).withMaterial(blue)
//        assembler.triangle(4, 5, 6).withMaterial(blue)
//        assembler.triangle(6, 7, 4).withMaterial(blue)
//        assembler.triangle(1, 0, 5).withMaterial(red)
//        assembler.triangle(5, 4, 1).withMaterial(red)
//        assembler.triangle(7, 6, 3).withMaterial(red)
//        assembler.triangle(3, 2, 7).withMaterial(red)
//        assembler.triangle(3, 6, 5).withMaterial(green)
//        assembler.triangle(5, 0, 3).withMaterial(green)
//        assembler.triangle(2, 1, 4).withMaterial(green)
//        assembler.triangle(4, 7, 2).withMaterial(green)
//        return assembler.compile()
//    }

    fun handleEvent(event: AWTEvent) = controller.handleEvent(event)
}
