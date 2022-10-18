package com.example.pdql.port

import com.example.pdql.domain.ESQuery
import com.example.pdql.domain.PDQLNode

interface IElasticSearchAdapter {
    fun toMatch(node: PDQLNode): ESQuery
    fun toGreaterThan(node: PDQLNode): ESQuery
    fun toIn(node: PDQLNode): ESQuery
    fun toIs(node: PDQLNode): ESQuery
}
