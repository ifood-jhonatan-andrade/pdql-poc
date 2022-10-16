package com.example.pdql.adapter

import com.example.pdql.domain.PDQLNode
import com.example.pdql.domain.PDQLType
import org.antlr.v4.runtime.tree.TerminalNodeImpl

object AntlrAdapter {
    fun toIn(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
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
            PDQLType.EQUAL
        )
    }

    fun toEqual(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
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
            PDQLType.EQUAL
        )
    }

    fun toSubQuery(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
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

    fun toConjunction(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
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

    fun toGT(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
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
