package com.example.pdql.presentation.adapter

import com.example.pdql.domain.PDQLNode
import com.example.pdql.domain.PDQLType
import com.example.pdql.port.IAntlrAdapter
import org.antlr.v4.runtime.tree.TerminalNodeImpl

class AntlrAdapter : IAntlrAdapter {

    private fun getNode(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
        val id = ctx?.payload?.text
        val operation = ctx?.children?.get(1)?.text
        val column = ctx?.children?.get(0)?.text
        val value = ctx?.children?.get(2)?.text
        val parent = ctx?.parent?.text

        return PDQLNode(
            id,
            parent,
            operation,
            column,
            value,
            null
        )
    }
    override fun toIn(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
        val id = ctx?.payload?.text
        val operation = ctx?.children?.get(1)?.text
        val column = ctx?.children?.get(0)?.text
        val parent = ctx?.parent?.text

        val value = ctx?.children?.subList(2, ctx.childCount)
            ?.filter { it.javaClass != TerminalNodeImpl::class.java }?.map { it.text }

        return PDQLNode(
            id,
            parent,
            operation,
            column,
            value,
            PDQLType.IN
        )
    }

    override fun toEqual(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
        return getNode(ctx).copy(type = PDQLType.EQUAL)
    }

    override fun toIs(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
        return getNode(ctx).copy(type = PDQLType.IS)
    }

    override fun toSubQuery(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
        val operation = ctx?.children?.get(1)?.getChild(1)?.text
        val id = ctx?.children?.get(1)?.text
        val parent = ctx?.parent?.text

        return PDQLNode(
            id,
            parent,
            operation,
            null,
            null,
            PDQLType.SUB_QUERY
        )
    }

    override fun toConjunction(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
        val id = ctx?.payload?.text
        val operation = ctx?.children?.get(1)?.text
        val parent = ctx?.parent?.text

        return PDQLNode(
            id,
            parent,
            operation,
            null,
            null,
            PDQLType.CONJUNCTION
        )
    }

    override fun toGreaterThan(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
        val id = ctx?.payload?.text
        val operation = ctx?.children?.get(1)?.text
        val parent = ctx?.parent?.text
        val column = ctx?.children?.get(0)?.text
        val value = ctx?.children?.get(2)?.text

        return PDQLNode(
            id,
            parent,
            operation,
            column,
            value?.toInt(),
            PDQLType.GT
        )
    }
}
