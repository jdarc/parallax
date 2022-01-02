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

class DynamicArray<T>(initialCapacity: Int = 16) {
    private var elements = generate(initialCapacity)
    private var index = 0

    val size get() = index

    fun isEmpty() = index == 0

    operator fun get(index: Int) = elements[index]

    operator fun set(index: Int, value: T) {
        elements[index] = value
    }

    operator fun iterator() = object : Iterator<T> {
        private var i = 0
        override fun hasNext() = i < index
        override fun next() = elements[i++]
    }

    fun add(element: T): Int {
        if (index == elements.size) {
            Logger.debug("${this.hashCode()}: expanding array capacity by ${elements.size}")
            elements += generate(elements.size)
        }
        elements[index++] = element
        return index - 1
    }

    fun reset() {
        index = 0
    }

    @Suppress("UNCHECKED_CAST")
    private fun generate(capacity: Int) = arrayOfNulls<Any?>(capacity) as Array<T>
}
