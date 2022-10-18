package com.example.pdql.listener

import PDQLBaseListener
import com.example.pdql.domain.ESQuery
import com.example.pdql.domain.PDQLNode
import com.example.pdql.domain.PDQLType
import com.example.pdql.port.IAntlrAdapter
import com.example.pdql.port.IElasticSearchAdapter
import java.lang.Error

class PDQListener(
    private val antlrAdapter: IAntlrAdapter,
    private val elasticSearchAdapter: IElasticSearchAdapter
) : PDQLBaseListener() {
    private var rootPDQLNode: PDQLNode? = null
    private val mapTreeClauses = mutableMapOf<String?, MutableList<PDQLNode>>()
    override fun enterExpr_pdql(ctx: PDQLParser.Expr_pdqlContext?) {
        if (rootPDQLNode == null) {
            val element = getContextNode(ctx)
            rootPDQLNode = element

            mapTreeClauses[element.id] = mutableListOf()
        } else if (ctx?.stop != ctx?.start) {
            val element = getContextNode(ctx)
            if (element.type == PDQLType.CONJUNCTION) {
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
            return antlrAdapter.toSubQuery(ctx)
        }

        return when (operation) {
            "=", "MATCH", "like" -> antlrAdapter.toEqual(ctx)
            ">" -> antlrAdapter.toGreaterThan(ctx)
            "in" -> antlrAdapter.toIn(ctx)
            "and", "or" -> antlrAdapter.toConjunction(ctx)
            "is" -> antlrAdapter.toIs(ctx)
            else -> throw Error("Node type invalid")
        }
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
        // TODO: IMPLEMENT TO ADAPTER
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
        return when (current.type) {
            PDQLType.EQUAL -> elasticSearchAdapter.toMatch(current)
            PDQLType.GT -> elasticSearchAdapter.toGreaterThan(current)
            PDQLType.IN -> elasticSearchAdapter.toIn(current)
            PDQLType.IS -> elasticSearchAdapter.toIs(current)
            else -> throw Error("Operation invalid")
        }
    }
}
