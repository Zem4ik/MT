import parser.ExpressionBaseVisitor
import parser.ExpressionParser
import java.lang.IllegalArgumentException
import java.util.HashMap



class ExpressionVisitor : ExpressionBaseVisitor<Int>() {
    private val variables = HashMap<String, Int>()

    override fun visitSetVariable(ctx: ExpressionParser.SetVariableContext): Int {
        val value: Int = visit(ctx.sumOrSub())
        val key = ctx.Variable().toString()
        variables[key] = value
        println("$key: $value")
        return value
    }

    override fun visitPlus(ctx: ExpressionParser.PlusContext): Int {
        return visit(ctx.sumOrSub()) + visit(ctx.mulOrDiv())
    }

    override fun visitMinus(ctx: ExpressionParser.MinusContext): Int {
        return visit(ctx.sumOrSub()) - visit(ctx.mulOrDiv())
    }

    override fun visitMultiplication(ctx: ExpressionParser.MultiplicationContext): Int {
        return visit(ctx.mulOrDiv()) * visit(ctx.pow())
    }

    override fun visitDivision(ctx: ExpressionParser.DivisionContext): Int {
        return visit(ctx.mulOrDiv()) / visit(ctx.pow())
    }

    override fun visitPower(ctx: ExpressionParser.PowerContext): Int {
        ctx.pow()?.let {
            return Math.pow(
                    visit(ctx.unaryMinus()).toDouble(),
                    visit(it).toDouble()
            ).toInt()
        }
        return visit(ctx.unaryMinus())
    }

    override fun visitChangeSign(ctx: ExpressionParser.ChangeSignContext): Int {
        return -visit(ctx.unaryMinus())
    }

    override fun visitInteger(ctx: ExpressionParser.IntegerContext): Int {
        return ctx.INT().text.toInt()
    }

    override fun visitVariable(ctx: ExpressionParser.VariableContext): Int {
        return variables[ctx.Variable().text] ?: throw IllegalArgumentException("Can't find variable ${ctx.Variable().text}")
    }

    override fun visitBraces(ctx: ExpressionParser.BracesContext): Int {
        return visit(ctx.sumOrSub())
    }
}