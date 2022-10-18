package com.example.pdql.factory

import com.example.pdql.listener.PDQListener

object PDQListenerFactory {
    fun create() = PDQListener(
        AntlrAdapterFactory.create(),
        ElasticSearchAdapter.create()
    )
}
