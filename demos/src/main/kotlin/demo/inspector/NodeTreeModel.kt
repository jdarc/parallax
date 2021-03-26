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

import com.zynaps.parallax.graph.*
import java.util.*
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

class NodeTreeModel(private val sceneGraph: SceneGraph) : TreeModel, SceneGraphListener {

    private val treeModelListeners = Vector<TreeModelListener>()

    init {
        sceneGraph.addListener(this)
    }

    override fun getRoot() = sceneGraph.root

    override fun getChild(parent: Any, index: Int) = if (parent is BranchNode) parent[index] else null

    override fun getChildCount(parent: Any) = if (parent is BranchNode) parent.childCount else 0

    override fun isLeaf(node: Any) = node is LeafNode

    override fun valueForPathChanged(path: TreePath?, newValue: Any) = println("*** valueForPathChanged : $path --> $newValue")

    override fun getIndexOfChild(parent: Any, child: Any) = if (parent is BranchNode) parent.indexOf(child) else -1

    override fun addTreeModelListener(l: TreeModelListener) = treeModelListeners.addElement(l)

    override fun removeTreeModelListener(l: TreeModelListener) {
        treeModelListeners.removeElement(l)
    }

    override fun nodesChanged(e: SceneGraphEvent) = Unit

    override fun nodesInserted(e: SceneGraphEvent) {
        val indexOf = (e.parent as BranchNode).indexOf(e.child)
        val event = TreeModelEvent(e.child, arrayOf(e.parent), intArrayOf(indexOf), arrayOf(e.child))
        treeModelListeners.forEach { it.treeNodesInserted(event) }
    }

    override fun nodesRemoved(e: SceneGraphEvent) {
        val event = TreeModelEvent(e.parent, TreePath(e.child))
        treeModelListeners.forEach { it.treeNodesRemoved(event) }
    }

    override fun structureChanged(e: SceneGraphEvent) {
        val event = TreeModelEvent(sceneGraph.root, TreePath(sceneGraph.root))
        treeModelListeners.forEach { it.treeStructureChanged(event) }
    }
}
