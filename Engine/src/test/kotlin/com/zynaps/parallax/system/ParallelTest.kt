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

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

internal class ParallelTest {

    private val cpus = Runtime.getRuntime().availableProcessors()

    @Test
    fun shouldPartitionZeroTasksAcrossAvailableProcessors() {
        val invocations = AtomicInteger(0)
        val acc = AtomicInteger(0)
        Parallel.partition(0) { _, from, to ->
            acc.getAndAdd(to - from)
            invocations.incrementAndGet()
        }
        Assertions.assertThat(acc.get()).isEqualTo(0)
        Assertions.assertThat(invocations.get()).isEqualTo(0)
    }

    @Test
    fun shouldPartitionOneTasksAcrossAvailableProcessors() {
        val invocations = AtomicInteger(0)
        val acc = AtomicInteger(0)
        Parallel.partition(1) { _, from, to ->
            acc.getAndAdd(to - from)
            invocations.incrementAndGet()
        }
        Assertions.assertThat(acc.get()).isEqualTo(1)
        Assertions.assertThat(invocations.get()).isEqualTo(1)
    }

    @Test
    fun shouldPartitionFewTasksAcrossAvailableProcessors() {
        val invocations = AtomicInteger(0)
        val acc = AtomicInteger(0)
        Parallel.partition(16) { _, from, to ->
            acc.getAndAdd(to - from)
            invocations.incrementAndGet()
        }
        Assertions.assertThat(acc.get()).isEqualTo(16)
        Assertions.assertThat(invocations.get()).isEqualTo(cpus)
    }

    @Test
    fun shouldPartitionManyTasksAcrossAvailableProcessors() {
        val invocations = AtomicInteger(0)
        val acc = AtomicInteger(0)
        Parallel.partition(3711) { _, from, to ->
            acc.getAndAdd(to - from)
            invocations.incrementAndGet()
        }
        Assertions.assertThat(acc.get()).isEqualTo(3711)
        Assertions.assertThat(invocations.get()).isEqualTo(cpus)
    }
}
