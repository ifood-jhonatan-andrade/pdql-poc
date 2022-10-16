package com.example.pdql.component

import PDQLLexer
import PDQLParser
import com.example.pdql.domain.ESQuery
import com.example.pdql.listener.PDQListener
import com.example.pdql.port.IPDQLComponent
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeWalker

class PDQLComponent : IPDQLComponent {
    override fun transpiler(query: String): ESQuery {
        val lexer = PDQLLexer(CharStreams.fromString(query))
        val tokens = CommonTokenStream(lexer)
        val parser = PDQLParser(tokens)
        val walker = ParseTreeWalker()
        val tree: ParseTree = parser.parse()
        val listener = PDQListener()
        walker.walk(listener, tree)

        return listener.getESQuery()
    }
}
