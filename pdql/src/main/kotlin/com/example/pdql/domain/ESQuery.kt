package com.example.pdql.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ESQuery(
    val should: List<ESQuery>? = null,
    val must: List<ESQuery>? = null,
    val minimum_should_match: Int? = null,
    val match: MutableMap<String?, Any?>? = null,
    val bool: ESQuery? = null,
    val range: MutableMap<String?, ESQueryRange?>? = null
) {
    data class ESQueryRange(
        val gt: Int? = null,
        val gte: Int? = null,
        val lt: Int? = null,
        val lte: Int? = null
    )
}
