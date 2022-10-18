package com.example.pdql.port

import com.example.pdql.domain.PDQLNode

interface IAntlrAdapter {
    fun toIn(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode
    fun toEqual(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode
    fun toIs(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode
    fun toSubQuery(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode
    fun toConjunction(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode
    fun toGreaterThan(ctx: PDQLParser.Expr_pdqlContext?): PDQLNode
}
