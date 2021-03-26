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

import com.zynaps.parallax.graph.SceneGraph
import com.zynaps.parallax.io.ResourceLoader
import demo.*
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.File
import java.text.DecimalFormat
import javax.swing.*
import javax.swing.SwingConstants.CENTER
import javax.swing.border.Border
import javax.swing.border.EmptyBorder
import javax.swing.border.MatteBorder
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.plaf.basic.BasicSplitPaneDivider
import javax.swing.plaf.basic.BasicSplitPaneUI

class MainFrame : BaseFrame() {
    private val sceneGraph = SceneGraph()
    private var fpsCounter = FPSCounter()
    private val formatter = DecimalFormat("#,###")
    private val chooser = JFileChooser()
    private val memoryBar = MemoryBar()
    private val frameRateLabel = JLabel("", CENTER)
    private val verticesLabel = JLabel("", CENTER)
    private val trianglesLabel = JLabel("", CENTER)
    private var vsync = false

    init {
        this.viewer = Viewer(sceneGraph)
        initialiseEvents()
        initialiseFrame()
        initialiseMisc()
    }

    override fun update(seconds: Double) {
        super.update(seconds)
        fpsCounter.add(seconds)
        updateStats()
    }

    private fun updateStats() {
        val stats = viewer.stats()
        frameRateLabel.text = "Frames/Sec: ${fpsCounter.average()}"
        verticesLabel.text = "Vertices: ${formatter.format(stats.vertices)}"
        trianglesLabel.text = "Triangles: ${formatter.format(stats.triangles)}"
        memoryBar.repaint()
    }

    private fun initialiseFrame() {
        extendedState = extendedState.plus((MAXIMIZED_BOTH))
        background = Color.DARK_GRAY
        contentPane.layout = BorderLayout()
        contentPane.add(buildToolbar(), BorderLayout.NORTH)
        contentPane.add(adaptSplit(JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, buildProjectPanel(), buildViewer())), BorderLayout.CENTER)
        contentPane.add(buildStatusPanel(), BorderLayout.SOUTH)
        pack()
        setLocationRelativeTo(null)
    }

    private fun buildViewer(): Component {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createMatteBorder(1, 1, 1, 0, background.darker())
        panel.add(viewer, BorderLayout.CENTER)
        return panel
    }

    private fun buildProjectPanel(): JSplitPane {
        val pane = JSplitPane(JSplitPane.VERTICAL_SPLIT, true, buildGraphView(), buildNodeEditorPane())
        pane.resizeWeight = 1.0
        return adaptSplit(pane)
    }

    private fun buildGraphView(): JScrollPane {
        val sceneGraphTree = JTree(NodeTreeModel(sceneGraph))
        sceneGraphTree.cellRenderer = NodeTreeCellRenderer()
        sceneGraphTree.border = BorderFactory.createEmptyBorder(4, 4, 4, 4)
        return JScrollPane(sceneGraphTree)
    }

    private fun buildNodeEditorPane(): JScrollPane {
        val editorPanel = JPanel()
        val scrollPane = JScrollPane(editorPanel)
        scrollPane.minimumSize = Dimension(350, 300)
        scrollPane.preferredSize = scrollPane.minimumSize
        return scrollPane
    }

    private fun initialiseMisc() {
        chooser.dialogTitle = "Choose model"
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY
        chooser.addChoosableFileFilter(FileNameExtensionFilter("Wavefront", "obj"))
        chooser.addChoosableFileFilter(FileNameExtensionFilter("Collada", "dae"))
        chooser.isAcceptAllFileFilterUsed = false
        chooser.currentDirectory = File(System.getProperty("user.home"))
    }

    private fun load() {
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) viewer.load(chooser.selectedFile)
    }

    private fun toggleVSync() {
        vsync = !vsync
        gameLoop?.delay = if (vsync) 16 else 1
    }

    private fun buildToolbar(): JComponent {
        val blendColor = Color(32, 200, 64, 32)
        val insets = Insets(4, 4, 4, 8)

        val toolbar = JPanel()
        toolbar.border = EmptyBorder(2, 2, 2, 2)
        toolbar.layout = BoxLayout(toolbar, BoxLayout.X_AXIS)

        val newButton = createButton("New", Icons.Toolbar.file(blendColor), KeyEvent.VK_N, insets)
        newButton.toolTipText = "Create a new empty scene"
        newButton.addActionListener { viewer.clear() }

        val loadButton = createButton("Load...", Icons.Toolbar.diskette(blendColor), KeyEvent.VK_L, insets)
        loadButton.toolTipText = "Load model from file system"
        loadButton.addActionListener { load() }

        val plane = createButton("Plane", Icons.Toolbar.plane(blendColor), 0, insets)
        plane.toolTipText = "Create a plane model"
        plane.addActionListener { addPlane() }

        val cube = createButton("Cube", Icons.Toolbar.cube(blendColor), 0, insets)
        cube.toolTipText = "Create a cube model"
        cube.addActionListener { addCube() }

        val sphere = createButton("Sphere", Icons.Toolbar.sphere(blendColor), 0, insets)
        sphere.toolTipText = "Create a sphere model"
        sphere.addActionListener { addSphere() }

        val capsule = createButton("Capsule", Icons.Toolbar.capsule(blendColor), 0, insets)
        capsule.toolTipText = "Create a capsule model"
        capsule.addActionListener { addCapsule() }

        val vsyncButton = createToggleButton("V-Sync", Icons.Toolbar.vsync(blendColor), KeyEvent.VK_S, insets)
        vsyncButton.toolTipText = ""
        vsyncButton.addActionListener { toggleVSync() }

        toolbar.add(Box.createRigidArea(Dimension(2, 0)))
        toolbar.add(newButton)
        toolbar.add(Box.createRigidArea(Dimension(2, 0)))
        toolbar.add(loadButton)
        toolbar.add(Box.createGlue())
        toolbar.add(plane)
        toolbar.add(Box.createRigidArea(Dimension(2, 0)))
        toolbar.add(cube)
        toolbar.add(Box.createRigidArea(Dimension(2, 0)))
        toolbar.add(sphere)
        toolbar.add(Box.createRigidArea(Dimension(2, 0)))
        toolbar.add(capsule)
        toolbar.add(Box.createGlue())
        toolbar.add(vsyncButton)
        toolbar.add(Box.createRigidArea(Dimension(2, 0)))
        return toolbar
    }

    private fun createButton(label: String, iconImage: BufferedImage, mnemonic: Int, insets: Insets): AbstractButton =
        embellishButton(JButton(label, ImageIcon(iconImage)), mnemonic, insets)

    private fun createToggleButton(label: String, iconImage: BufferedImage, mnemonic: Int, insets: Insets): AbstractButton =
        embellishButton(JToggleButton(label, ImageIcon(iconImage)), mnemonic, insets)

    private fun embellishButton(button: AbstractButton, mnemonic: Int, insets: Insets): AbstractButton {
        button.isFocusPainted = false
        button.mnemonic = mnemonic
        button.margin = insets
        return button
    }

    private fun adaptSplit(pane: JSplitPane): JSplitPane {
        pane.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        pane.setUI(object : BasicSplitPaneUI() {
            override fun createDefaultDivider(): BasicSplitPaneDivider {
                return object : BasicSplitPaneDivider(this) {
                    override fun setBorder(b: Border) {
                    }
                }
            }
        })
        return pane
    }

    private fun buildStatusPanel(): JPanel {
        val separatorColor = Color(0, 0, 0, 64)
        val matteBorder = MatteBorder(0, 0, 0, 1, separatorColor)
        val wrapIt = { comp: JComponent ->
            val wrap = JPanel(BorderLayout())
            wrap.border = matteBorder
            wrap.add(comp, BorderLayout.CENTER)
            wrap
        }

        val statusPanel = JPanel()
        statusPanel.layout = BoxLayout(statusPanel, BoxLayout.X_AXIS)
        statusPanel.add(Box.createRigidArea(Dimension(4, 38)))

        val parentPanel = JPanel(BorderLayout())
        parentPanel.border = EmptyBorder(2, 2, 2, 2)

        val statusLabel = JLabel("Ready")
        parentPanel.add(statusLabel, BorderLayout.CENTER)

        val subPanel = JPanel()
        subPanel.layout = BoxLayout(subPanel, BoxLayout.X_AXIS)

        frameRateLabel.preferredSize = Dimension(172, 32)
        subPanel.add(wrapIt(frameRateLabel))

        verticesLabel.preferredSize = Dimension(172, 32)
        subPanel.add(wrapIt(verticesLabel))

        trianglesLabel.preferredSize = Dimension(172, 32)
        subPanel.add(trianglesLabel)

        memoryBar.preferredSize = Dimension(200, 32)
        subPanel.add(memoryBar)

        parentPanel.add(subPanel, BorderLayout.EAST)
        statusPanel.add(parentPanel)

        statusPanel.add(Box.createRigidArea(Dimension(1, 32)))

        return statusPanel
    }

    private fun addPlane(offset: Float = -10.0F, scale: Float = 100.0F) = Scenery(sceneGraph, ResourceLoader()).addPlane(offset, scale)
    private fun addCube(scale: Float = 10.0F) = Scenery(sceneGraph, ResourceLoader()).addCube(scale)
    private fun addSphere(scale: Float = 10.0F) = Scenery(sceneGraph, ResourceLoader()).addSphere(scale)
    private fun addCapsule(scale: Float = 10.0F) = Scenery(sceneGraph, ResourceLoader()).addCapsule(scale)
}
