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

import java.awt.*
import javax.swing.JComponent

class MemoryBar : JComponent() {
    private val clearColor = Color(0, 0, 0, 48)
    private val barColorHi = Color(32, 128, 32)
    private val barColorWarn = Color(128, 128, 32)
    private val barColorLow = Color(128, 32, 16)

    @Override
    override fun paint(g: Graphics) {
        (g as Graphics2D).apply {
            val bounds = Rectangle(insets.left, insets.top, width - insets.right - insets.left, height - insets.bottom - insets.top)
            clearBackground(g, bounds)
            drawUsageBar(g, bounds)
            drawLabel(g, bounds)
        }
    }

    private fun Graphics2D.drawUsageBar(g: Graphics2D, bounds: Rectangle) {
        val ratio = Runtime.getRuntime().freeMemory().toFloat() / Runtime.getRuntime().totalMemory().toFloat()
        g.color = when {
            ratio < 0.1F -> barColorLow
            ratio < 0.3F -> barColorWarn
            else -> barColorHi
        }
        fillRect(bounds.x + 1, bounds.y + 1, ((bounds.width - 2) * ratio).toInt(), bounds.height - 2)
    }

    private fun drawLabel(g: Graphics2D, bounds: Rectangle) {
        val total = humanReadableByteCount(Runtime.getRuntime().totalMemory())
        val used = humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
        val label = "$used of ${total}M"
        val fontMetrics = g.getFontMetrics(font)
        val x = (bounds.width - fontMetrics.stringWidth(label)) * 0.5F
        val y = fontMetrics.ascent + (height - fontMetrics.height) * 0.5F
        g.color = foreground
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.drawString(label, x, y)
    }

    private fun clearBackground(g: Graphics2D, bounds: Rectangle) {
        g.color = clearColor
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height)
    }

    private fun humanReadableByteCount(bytes: Long) = "${bytes / 1048576}"
}
