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

import com.zynaps.parallax.system.LogLevel
import com.zynaps.parallax.system.Logger
import java.awt.Font
import java.io.IOException
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess

fun main() {
    Logger.level = LogLevel.INFO
    System.setProperty("sun.java2d.uiScale.enabled", "false")
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    configureGlobalFont(Font("Liberation Sans", Font.PLAIN, 14))
    SwingUtilities.invokeLater {
        try {
            MainFrame().isVisible = true
        } catch (e: IOException) {
            Logger.error(e)
            exitProcess(0)
        }
    }
}

private fun configureGlobalFont(labelFont: Font) {
    val defaults = UIManager.getDefaults()
    for (key in defaults.keys()) {
        if (defaults.getString(key) != null) {
            val keyAsString = key.toString()
            val keyName = if (keyAsString.endsWith("UI")) keyAsString.dropLast(2) else keyAsString
            UIManager.put("$keyName.font", labelFont)
        }
    }
}
