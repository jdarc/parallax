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

import com.zynaps.parallax.graph.LeafNode
import com.zynaps.parallax.graph.MaterialNode
import com.zynaps.parallax.graph.RootNode
import demo.Icons
import java.awt.Component
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JTree
import javax.swing.SwingConstants
import javax.swing.tree.TreeCellRenderer

class NodeTreeCellRenderer : TreeCellRenderer {

    private val rootIcon = ImageIcon(Icons.Tree.root())
    private val branchIcon = ImageIcon(Icons.Tree.branch())
    private val leafIcon = ImageIcon(Icons.Tree.leaf())
    private val materialIcon = ImageIcon(Icons.Tree.material())

    override fun getTreeCellRendererComponent(tree: JTree, value: Any, selected: Boolean, expanded: Boolean,
                                              leaf: Boolean, row: Int, hasFocus: Boolean): Component {
        val icon = when (value) {
            is RootNode -> rootIcon
            is LeafNode -> leafIcon
            is MaterialNode -> materialIcon
            else -> branchIcon
        }
        return JLabel(value.toString(), icon, SwingConstants.RIGHT)
    }
}
