import edu.uci.ics.jung.algorithms.layout.TreeLayout
import edu.uci.ics.jung.visualization.BasicVisualizationServer
import edu.uci.ics.jung.visualization.decorators.EdgeShape
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller
import java.awt.Color
import java.awt.geom.Ellipse2D
import javax.swing.JFrame


fun main(args: Array<String>) {
    val inputStream = "((abc*b|a)aa|b*b)*".toByteArray().inputStream()
    val parser = Parser()
    val root = parser.parse(inputStream)

    val layout = TreeLayout<Node, Int>(root.convert())

    val vv = BasicVisualizationServer<Node, Int>(layout)
    vv.renderContext.edgeShapeTransformer = EdgeShape.Line<Node, Int>()

    vv.renderContext.setVertexShapeTransformer { n ->
        val width = Math.max(n.toString().length * 10, 25)
        Ellipse2D.Double((-(width / 2)).toDouble(), -12.5, width.toDouble(), 25.0)
    }
    vv.renderContext.vertexLabelTransformer = ToStringLabeller<Node>()
    vv.getRenderContext().setVertexFillPaintTransformer { Color.PINK }
    vv.renderer.vertexLabelRenderer.position = edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position.CNTR

    val frame = JFrame("Simple Graph View")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.contentPane.add(vv)
    frame.pack()
    frame.isVisible = true
}