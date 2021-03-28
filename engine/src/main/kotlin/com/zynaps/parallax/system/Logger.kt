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

import java.util.concurrent.TimeUnit

object Logger : Logging {

    const val ANSI_RESET = "\u001B[0m"
    const val ANSI_BLACK = "\u001B[30m"
    const val ANSI_RED = "\u001B[31m"
    const val ANSI_GREEN = "\u001B[32m"
    const val ANSI_YELLOW = "\u001B[33m"
    const val ANSI_BLUE = "\u001B[34m"
    const val ANSI_PURPLE = "\u001B[35m"
    const val ANSI_CYAN = "\u001B[36m"

    private val log = ConsoleLogger()

    override var level
        get() = log.level
        set(value) {
            log.level = value
        }

    override fun debug(msg: Any) = log.debug(msg)

    override fun info(msg: Any) = log.info(msg)

    override fun warn(msg: Any) = log.warn(msg)

    override fun error(msg: Any) = log.error(msg)

    fun every(tag: String, seconds: Double = 0.0): Logging {
        val logging = timedLoggers.getOrPut(tag) {
            object : ConsoleLogger() {

                private var lastSeen = 0L

                override fun log(msg: Any, level: LogLevel) {
                    if (TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - lastSeen) > seconds) {
                        super.log(msg, level)
                        lastSeen = System.nanoTime()
                    }
                }
            }
        }
        logging.level = level
        return logging
    }

    private val timedLoggers = HashMap<String, Logging>()

    private open class ConsoleLogger : Logging {

        override var level = LogLevel.OFF

        override fun debug(msg: Any) = log(msg, LogLevel.DEBUG)

        override fun info(msg: Any) = log(msg, LogLevel.INFO)

        override fun warn(msg: Any) = log(msg, LogLevel.WARN)

        override fun error(msg: Any) = log(msg, LogLevel.ERROR)

        protected open fun log(msg: Any, level: LogLevel) {
            if (this.level.value > level.value) return
            println("${if (level == LogLevel.ERROR) ANSI_RED else ANSI_RESET}${msg}${ANSI_RESET}")
        }
    }
}
