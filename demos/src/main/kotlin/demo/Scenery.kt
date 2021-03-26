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
package demo

import com.zynaps.parallax.core.Material
import com.zynaps.parallax.core.Primitives
import com.zynaps.parallax.core.Samplers
import com.zynaps.parallax.graph.BranchNode
import com.zynaps.parallax.graph.SceneGraph
import com.zynaps.parallax.io.Importer
import com.zynaps.parallax.io.ResourceLoader
import com.zynaps.parallax.io.readers.ColladaReader
import com.zynaps.parallax.math.Matrix4
import com.zynaps.parallax.math.Random.nextFloat
import com.zynaps.parallax.math.Random.nextInt
import com.zynaps.parallax.math.Scalar
import com.zynaps.parallax.math.Vector3
import demo.runaround.components.AxisSpinNode
import demo.runaround.components.RandomSpinNode
import kotlin.random.Random

class Scenery(private val sceneGraph: SceneGraph, private val loader: ResourceLoader) {
    private val blueTiles = Material.Builder().name("Blue Tiles").diffuse(Samplers.Checker(0xC5D2FF, 0x7D87D5, 8.0F)).build()
    private val greenTiles = Material.Builder().name("Green Tiles").diffuse(Samplers.Checker(0x6FAA0D, 0x5F930E)).build()
    private val yellowTiles = Material.Builder().name("Yellow Tiles").diffuse(Samplers.Checker(0xFFF787, 0xFFD51B, 8.0F)).build()
    private val purpleTiles = Material.Builder().name("Purple Tiles").diffuse(Samplers.Checker(0xFFC6F0, 0xFF8A94, 8.0F)).build()

    private val cube = Primitives.cube(blueTiles).compile()
    private val plane = Primitives.plane(greenTiles).compile()
    private val sphere = Primitives.sphere(yellowTiles).compile()
    private val capsule = Primitives.capsule(purpleTiles).compile()

    fun addPlane(offset: Float = -10.0F, scale: Float = 400.0F) =
        sceneGraph.root.add(plane.toGraph(Matrix4.createTranslation(0.0F, offset, 0.0F) * Matrix4.createScale(scale, scale, scale)))

    fun addCube(scale: Float = 1.0F) = sceneGraph.root.add(cube.toGraph(Matrix4.createScale(scale, scale, scale)))

    fun addSphere(scale: Float = 1.0F) = sceneGraph.root.add(sphere.toGraph(Matrix4.createScale(scale, scale, scale)))

    fun addCapsule(scale: Float = 1.0F) = sceneGraph.root.add(capsule.toGraph(Matrix4.createScale(scale, scale, scale)))

    fun addRandomSpinners(scale: Float = 10.0F) {
        val scaler = Matrix4.createScale(scale, scale, scale)
        val cubes = BranchNode()
        for (t in 0..99) {
            val tx = nextFloat(-250.0F, 250.0F)
            val ty = nextFloat(0.0F, 100.0F)
            val tz = nextFloat(-250.0F, 250.0F)
            val ts = nextInt(3)
            val node = when (ts) {
                0 -> cube.toGraph(scaler)
                1 -> sphere.toGraph(scaler)
                else -> capsule.toGraph(scaler)
            }
            val spinNode = RandomSpinNode(5.0F)
            spinNode.add(node)
            val branchNode = BranchNode(Matrix4.createTranslation(tx, ty, tz))
            branchNode.add(spinNode)
            cubes.add(branchNode)
        }
        sceneGraph.root.add(cubes)
    }

    fun addSpecialThing() {
        val smallSpinner1 = AxisSpinNode(Vector3(1.0F, 0.0F, 0.0F), nextFloat(1.0F, 5.0F))
        smallSpinner1.add(cube.toGraph())
        val smallSpinner1Node = BranchNode(Matrix4.createFromPositionRotationScale(Vector3(0.75F, 0.0F, 0.0F), Matrix4.IDENTITY, Vector3(0.5F, 0.5F, 0.5F)))
        smallSpinner1Node.add(smallSpinner1)

        val smallSpinner2 = AxisSpinNode(Vector3(-1.0F, 0.0F, 0.0F), nextFloat(1.0F, 5.0F))
        smallSpinner2.add(cube.toGraph())
        val smallSpinner2Node = BranchNode(Matrix4.createFromPositionRotationScale(Vector3(-0.75F, 0.0F, 0.0F), Matrix4.IDENTITY, Vector3(0.5F, 0.5F, 0.5F)))
        smallSpinner2Node.add(smallSpinner2)

        val bigBoxSpinner = RandomSpinNode(1.0F)
        bigBoxSpinner.add(cube.toGraph())
        bigBoxSpinner.add(smallSpinner1Node)
        bigBoxSpinner.add(smallSpinner2Node)

        val bigBoxNode = BranchNode(Matrix4.createFromPositionRotationScale(Vector3(-55.0F, 100.0F, 100.0F), Matrix4.createRotationY(nextFloat()), Vector3(10.0F, 10.0F, 10.0F)))
        bigBoxNode.add(bigBoxSpinner)

        val axisSpinNode = AxisSpinNode(Vector3(0.0F, 1.0F, 0.0F), nextFloat(-1.0F, 1.0F))
        axisSpinNode.add(bigBoxNode)

        sceneGraph.root.add(axisSpinNode)

    }

    fun addImported(name: String = "simba.obj", position: Vector3 = Vector3(0.0F, 4.0F, 0.0F), scale: Float = 40.0F) {
        val geometry = Importer.load(loader, name)
        geometry.localMatrix = Matrix4.createFromPositionRotationScale(position, Matrix4.createRotationY(Scalar.PI), Vector3(scale, scale, scale))
        sceneGraph.root.add(geometry)
    }

    fun addClipStressTest(name: String = "dwarf.obj", position: Vector3 = Vector3(0.0F, 4.0F, 0.0F), scale: Float = 50.0F) {
        val rand = Random(1973)
        val geometry = loadModel(name)
        (-160..160 step 40).forEach { z ->
            (-160..160 step 40).forEach { x ->
                val radians = rand.nextFloat() * Scalar.TAU
                val pos = position + Vector3(x.toFloat(), 0.0F, z.toFloat())
                val mat = Matrix4.createFromPositionRotationScale(pos, Matrix4.createRotationY(radians), Vector3(scale, scale, scale))
                geometry.localMatrix = mat
                sceneGraph.root.add(geometry.clone())
            }
        }
    }

    fun addStressTest(name: String = "simba.obj", scale: Float = 70.0F) {
        val geometry = loadModel(name)
        val scaler = Vector3(scale, scale, scale)
        var y = 180
        while (y > -180) {
            var z = -180
            while (z < 180) {
                var x = -180
                while (x < 180) {
                    val mat = Matrix4.createFromPositionRotationScale(Vector3(x.toFloat(), (y + 10).toFloat(), z.toFloat()), Matrix4.IDENTITY, scaler)
                    val spinner = RandomSpinNode(1.0F)
                    spinner.add(geometry.clone())
                    val branch = BranchNode(mat)
                    branch.add(spinner)
                    sceneGraph.root.add(branch)
                    x += 80
                }
                z += 80
            }
            y -= 80
        }
    }

    private fun loadModel(name: String) = Importer.load(loader, name)

    fun colladaTest(name: String = "aircar.dae", position: Vector3 = Vector3(0.0F, 4.0F, 0.0F), scaler: Vector3 = Vector3(100.0F, 100.0F, 100.0F)) {
        val collada = ColladaReader().load(loader, name)
        collada.localMatrix = Matrix4.createTranslation(position) * Matrix4.createScale(scaler.x, scaler.y, scaler.z)
        sceneGraph.root.add(collada)
    }

    fun addTerrain() {
        val material = Material.Builder().name("Terrain Material").diffuse(Samplers.Checker(0x6FAA0D, 0x5F930E)).build()
        val terrain = Primitives.terrain(300.0F, 300.0F, 128.0F, 256, 256, material).compile().toGraph()
        terrain.localMatrix = Matrix4.createTranslation(0.0F, -10.0F, 0.0F)
        sceneGraph.root.add(terrain)
    }
}
