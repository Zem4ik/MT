import edu.uci.ics.jung.graph.DelegateTree
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph

private  var edgeNumber : Int = 0

fun Node.convert(): DelegateTree<Node, Int> {
    val delegateTree = DelegateTree<Node, Int>(DirectedOrderedSparseMultigraph<Node, Int>())
    delegateTree.addVertex(this)
    edgeNumber = 0;
    for (child in children) {
        child.addToTree(delegateTree, this)
    }
    return delegateTree
}

fun Node.addToTree(delegateTree: DelegateTree<Node, Int>, parent : Node) {
    delegateTree.addChild(edgeNumber++, parent, this)
    for (child in children) {
        child.addToTree(delegateTree, this)
    }
}