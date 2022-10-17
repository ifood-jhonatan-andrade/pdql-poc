package com.example.pdql.adapter

import com.example.pdql.domain.ESQuery
import com.example.pdql.domain.PDQLNode

object ESAdapter {
    fun toMatch(node: PDQLNode): ESQuery {
        val field = "doc.ItemMetadata.${node.column}"
        val value = node.value
        val map = mutableMapOf<String?, Any?>()
        map[field] = value
        return ESQuery(match = map)
    }

    fun toGT(node: PDQLNode): ESQuery {
        val field = "doc.ItemMetadata.${node.column}"
        val value = node.value
        val map = mutableMapOf<String?, ESQuery.ESQueryRange?>()
        map[field] = ESQuery.ESQueryRange(gt = value as Int?)
        return ESQuery(range = map)
    }

    fun toIn(node: PDQLNode): ESQuery {
        val matches = (node.value as Iterable<*>).map {
            toMatch(node.copy(value = it))
        }

        val esQuery = ESQuery(
            should = matches,
            minimum_should_match = 1
        )

        return ESQuery(bool = esQuery)
    }

    fun toIS(node: PDQLNode): ESQuery = toMatch(
        node.copy(
            value = (node.value as String).toBoolean()
        )
    )
}
