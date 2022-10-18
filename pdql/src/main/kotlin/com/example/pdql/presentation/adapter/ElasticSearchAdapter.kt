package com.example.pdql.presentation.adapter

import com.example.pdql.domain.ESQuery
import com.example.pdql.domain.PDQLNode
import com.example.pdql.port.IElasticSearchAdapter

class ElasticSearchAdapter : IElasticSearchAdapter {
    override fun toMatch(node: PDQLNode): ESQuery {
        val field = "doc.ItemMetadata.${node.column}"
        val value = node.value
        val map = mutableMapOf<String?, Any?>()
        map[field] = value
        return ESQuery(match = map)
    }

    override fun toGreaterThan(node: PDQLNode): ESQuery {
        val field = "doc.ItemMetadata.${node.column}"
        val value = node.value
        val map = mutableMapOf<String?, ESQuery.ESQueryRange?>()
        map[field] = ESQuery.ESQueryRange(gt = value as Int?)
        return ESQuery(range = map)
    }

    override fun toIn(node: PDQLNode): ESQuery {
        val matches = (node.value as Iterable<*>).map {
            toMatch(node.copy(value = it))
        }

        val esQuery = ESQuery(
            should = matches,
            minimum_should_match = 1
        )

        return ESQuery(bool = esQuery)
    }

    override fun toIs(node: PDQLNode): ESQuery = toMatch(
        node.copy(
            value = (node.value as String).toBoolean()
        )
    )
}
