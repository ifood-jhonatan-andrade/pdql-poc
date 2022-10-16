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
}
