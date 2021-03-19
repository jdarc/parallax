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
package com.zynaps.parallax.system

import com.zynaps.parallax.math.Scalar.max
import javax.swing.Timer

@Suppress("MemberVisibilityCanBePrivate")
class GameLoop(val game: Game) {
    private val timer: Timer
    private var tock: Long = 0

    init {
        timer = Timer(1) {
            val elapsed = (System.nanoTime() - tock) / 1000000.0
            tock = System.nanoTime()
            game.update(elapsed / 1000.0)
            game.render()
        }
    }

    var delay
        get() = timer.delay
        set(value) {
            timer.delay = max(1, value)
        }

    fun start() {
        if (!timer.isRunning) {
            tock = System.nanoTime()
            timer.restart()
        }
    }

    fun stop() {
        if (timer.isRunning) timer.stop()
    }
}
