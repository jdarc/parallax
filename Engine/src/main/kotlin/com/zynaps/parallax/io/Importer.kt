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
package com.zynaps.parallax.io

import com.zynaps.parallax.io.readers.ColladaReader
import com.zynaps.parallax.io.readers.WavefrontReader
import java.io.File
import java.io.IOException

object Importer {

    @Throws(IOException::class)
    fun load(file: File) = load(ResourceLoader(file.parent), file.name)

    @Throws(IOException::class)
    fun load(loader: ResourceLoader, name: String) = when (val extension = name.substringAfterLast('.', "").trim().toLowerCase()) {
        "obj" -> WavefrontReader().load(loader, name).compile().toGraph()
        "dae" -> ColladaReader().load(loader, name)
        else -> throw UnsupportedFileFormat(extension)
    }
}
