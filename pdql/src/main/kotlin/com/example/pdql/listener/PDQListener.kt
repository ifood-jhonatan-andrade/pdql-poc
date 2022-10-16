package com.example.pdql.listener

import PDQLBaseListener
import com.example.pdql.domain.ESQuery
import com.example.pdql.domain.PDQLNode
import com.example.pdql.domain.PDQLType
import java.lang.Error

internal class PDQListener : PDQLBaseListener() {
    private var rootPDQLNode: PDQLNode? = null
    private val mapTreeClauses = mutableMapOf<String?, MutableList<PDQLNode>>()
    override fun enterExpr_pdql(ctx: PDQLParser.Expr_pdqlContext?) {
        if (rootPDQLNode == null) {
            val element = getContextNode(ctx)
            rootPDQLNode = element

            mapTreeClauses[element.id] = mutableListOf()
        } else if (ctx?.stop != ctx?.start) {
            val element = getContextNode(ctx)
            if (element.operation == "and" || element.operation == "or") {
                mapTreeClauses[element.parent]?.add(element)
                mapTreeClauses[element.id] = mutableListOf()
            } else {
                mapTreeClauses[element.parent]?.add(element)
            }
        }
    }

    private fun getContextNode(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
        val operation = ctx?.children?.get(1)?.text

        if (ctx?.children?.get(0)?.text == "(") {
            return PDQLNode.toSubQuery(ctx)
        }

        if (operation == "=" || operation == "MATCH" || operation == "like") {
            return PDQLNode.toEqual(ctx)
        }

        if (operation == ">") {
            return PDQLNode.toGT(ctx)
        }

        if (operation == "and" || operation == "or") {
            return PDQLNode.toConjunction(ctx)
        }

        throw Error("Node type invalid")
    }

    fun getESQuery(): ESQuery = walkTree(rootPDQLNode!!)

    private fun walkTree(current: PDQLNode): ESQuery {
        val children = mapTreeClauses[current.id] ?: mutableListOf()
        if (children.size > 0) {
            val resultChildren = children.map { walkTree(it) }

            return mergeESQuery(current.operation!!, resultChildren)
        }
        return createESQuery(current)
    }

    private fun mergeESQuery(
        operation: String,
        values: List<ESQuery>
    ): ESQuery {
        if (operation == "or") {
            val should = mutableListOf<ESQuery>()

            should.addAll(values)
            val esQuery = ESQuery(
                should = should,
                minimum_should_match = 1
            )

            return ESQuery(bool = esQuery)
        }
        if (operation == "and") {
            val must = mutableListOf<ESQuery>()

            must.addAll(values)
            val esQuery = ESQuery(must = must)

            return ESQuery(bool = esQuery)
        }
        throw Error("Operation invalid")
    }

    private fun createESQuery(current: PDQLNode): ESQuery {
        // TODO: not supports multi index
        val field = "doc.ItemMetadata.${current.column}"
        val type = current.type
        val value = current.value

        // TODO: only supports equality (=)(>) operation
        if (type == PDQLType.EQUAL) {
            val map = mutableMapOf<String?, Any?>()
            map[field] = value
            return ESQuery(match = map)
        }
        if (type == PDQLType.GT) {
            val map = mutableMapOf<String?, ESQuery.ESQueryRange?>()
            map[field] = ESQuery.ESQueryRange(gt = (value as String?)?.toInt())
            return ESQuery(range = map)
        }

        throw Error("Operation invalid")
    }
}
