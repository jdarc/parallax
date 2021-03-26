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

import com.zynaps.parallax.system.Game
import com.zynaps.parallax.system.GameLoop
import java.awt.AWTEvent
import java.awt.BorderLayout
import java.awt.Frame
import java.awt.Toolkit
import java.awt.event.AWTEventListener
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import kotlin.system.exitProcess

class MainFrame : Frame("Parallax 3D Graphics Engine"), Game, AWTEventListener {

    private val loop: GameLoop
    private val viewer: Viewer

    init {
        layout = BorderLayout()
        viewer = Viewer()
        background = viewer.background
        add(viewer, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(null)
        isResizable = false

        val mask = AWTEvent.MOUSE_EVENT_MASK or AWTEvent.MOUSE_MOTION_EVENT_MASK or AWTEvent.KEY_EVENT_MASK or AWTEvent.WINDOW_EVENT_MASK
        Toolkit.getDefaultToolkit().addAWTEventListener(this, mask)

        loop = GameLoop(this)
        loop.delay = 1
    }

    fun start() {
        isVisible = true
        loop.start()
    }

    fun stop() {
        loop.stop()
        isVisible = false
        dispose()
        exitProcess(0)
    }

    override fun update(seconds: Double) = viewer.update(seconds)

    override fun render() = viewer.render()

    override fun eventDispatched(event: AWTEvent) = when {
        event.id == WindowEvent.WINDOW_CLOSING -> stop()
        event is KeyEvent -> {
            when (event.keyCode) {
                KeyEvent.VK_ESCAPE -> stop()
            }
            viewer.handleEvent(event)
        }
        else -> viewer.handleEvent(event)
    }
}
