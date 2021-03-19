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

import com.zynaps.parallax.system.Game
import com.zynaps.parallax.system.GameLoop
import demo.inspector.Viewer
import java.awt.AWTEvent
import java.awt.Toolkit
import java.awt.event.WindowEvent
import javax.swing.JFrame

open class BaseFrame : JFrame("Parallax 3D Graphics Engine"), Game {

    protected lateinit var viewer: Viewer

    protected var gameLoop: GameLoop? = null

    override fun update(seconds: Double) = viewer.update(seconds)

    override fun render() {
        viewer.render()
        viewer.repaint()
    }

    private fun start() {
        gameLoop = gameLoop ?: GameLoop(this)
        gameLoop?.start()
    }

    private fun stop() {
        gameLoop?.stop()
    }

    protected fun initialiseEvents() {
        val keyMask = AWTEvent.KEY_EVENT_MASK or AWTEvent.WINDOW_EVENT_MASK
        val mouseMask = AWTEvent.MOUSE_EVENT_MASK or AWTEvent.MOUSE_MOTION_EVENT_MASK or AWTEvent.MOUSE_WHEEL_EVENT_MASK
        Toolkit.getDefaultToolkit().addAWTEventListener({
            when (it.id) {
                WindowEvent.WINDOW_ACTIVATED -> start()
                WindowEvent.WINDOW_DEACTIVATED -> stop()
                WindowEvent.WINDOW_CLOSING -> exit()
                else -> viewer.handleEvent(it)
            }
        }, keyMask or mouseMask)
    }

    private fun exit() {
        stop()
        isVisible = false
        dispose()
    }
}
