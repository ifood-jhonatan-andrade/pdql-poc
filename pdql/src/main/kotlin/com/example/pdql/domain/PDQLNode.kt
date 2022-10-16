package com.example.pdql.domain
data class PDQLNode(
    val id: String?,
    val parent: String?,
    val operation: String?,
    val column: String?,
    val value: Any?,
    val type: PDQLType
)
