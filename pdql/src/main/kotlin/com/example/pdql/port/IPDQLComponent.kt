package com.example.pdql.port

import com.example.pdql.domain.ESQuery

interface IPDQLComponent {
    fun transpiler(query: String): ESQuery
}
