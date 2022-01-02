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

import java.util.function.Supplier

class DynamicPool<T>(initialCapacity: Int = 16, private val generator: Supplier<T>) {
    private var elements = generate(generator, initialCapacity.coerceAtLeast(16))
    private val simpleName = elements[0]!!::class.simpleName
    private var index = 0

    fun next(): T {
        if (index == elements.size) {
            Logger.debug("${this.hashCode()}: pooling ${elements.size} new instances of <$simpleName>")
            elements += generate(generator, elements.size)
        }
        return elements[index++]
    }

    fun reset() {
        index = 0
    }

    private fun generate(generator: Supplier<T>, capacity: Int): Array<T> {
        val dest = arrayOfNulls<Any?>(capacity) as Array<T>
        (0 until capacity).mapIndexed { index, _ -> dest[index] = generator.get() }
        return dest
    }
}
