package com.example.pdql.domain

internal data class PDQLNode(
    val id: String?,
    val parent: String?,
    val operation: String?,
    val column: String?,
    val value: Any?,
    val type: PDQLType
) {
    companion object {
        // TODO: IN OF STRING OR NUMBER
//        fun toIn(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode<Array<Any>> {
//        }

        fun toEqual(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
            val id = ctx?.payload?.text
            val operation = ctx?.children?.get(1)?.text
            val column = ctx?.children?.get(0)?.text
            val value = ctx?.children?.get(2)?.text
            val parent = ctx?.parent?.text

            return PDQLNode(
                id,
                parent,
                operation,
                column,
                value,
                PDQLType.EQUAL
            )
        }

        fun toSubQuery(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
            val operation = ctx?.children?.get(1)?.getChild(1)?.text
            val id = ctx?.children?.get(1)?.text
            val parent = ctx?.parent?.text

            return PDQLNode(
                id,
                parent,
                operation,
                null,
                null,
                PDQLType.SUB_QUERY
            )
        }

        fun toConjunction(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode {
            val id = ctx?.payload?.text
            val operation = ctx?.children?.get(1)?.text
            val parent = ctx?.parent?.text

            return PDQLNode(
                id,
                parent,
                operation,
                null,
                null,
                PDQLType.CONJUNCTION
            )
        }

        fun toGT(ctx: PDQLParser.Expr_pdqlContext): PDQLNode {
            val id = ctx?.payload?.text
            val operation = ctx?.children?.get(1)?.text
            val parent = ctx?.parent?.text
            val column = ctx?.children?.get(0)?.text
            val value = ctx?.children?.get(2)?.text

            return PDQLNode(
                id,
                parent,
                operation,
                column,
                value,
                PDQLType.GT
            )
        }
    }
}
