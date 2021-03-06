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

import com.zynaps.parallax.math.Scalar.ceil
import com.zynaps.parallax.math.Scalar.min
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Parallel {

    val CPUS = Runtime.getRuntime().availableProcessors()
    val F: ExecutorService = Executors.newWorkStealingPool(CPUS + CPUS / 2)

    fun invoke(vararg callbacks: () -> Unit) {
        if (callbacks.isNotEmpty()) {
            F.invokeAll(callbacks.map { Callable { it() } })
        }
    }

    fun execute(total: Int, callback: (next: Int) -> Unit) {
        if (total > 0) {
            F.invokeAll((0 until total).map { Callable { callback(it) } })
        }
    }

    fun partition(total: Int, callback: (index: Int, from: Int, to: Int) -> Unit) {
        if (total > 0) {
            val size = ceil(total.toFloat() / CPUS)
            F.invokeAll((0 until total step size).map {
                Callable { callback(it / size, it, it + min(size, total - it)) }
            })
        }
    }
}
