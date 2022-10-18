package com.example.pdql.factory

import com.example.pdql.port.IElasticSearchAdapter
import com.example.pdql.presentation.adapter.ElasticSearchAdapter

object ElasticSearchAdapter {
    fun create(): IElasticSearchAdapter = ElasticSearchAdapter()
}
