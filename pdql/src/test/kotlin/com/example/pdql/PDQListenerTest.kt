package com.example.pdql

import com.example.pdql.component.PDQLComponent
import com.example.pdql.domain.ESQuery
import com.example.pdql.port.IPDQLComponent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class PDQListenerTest {

    val sut: IPDQLComponent = PDQLComponent()

    @Test
    fun `ensure distribution of the 'AND' operation`() {
        // A ( B | C ) -> AB | AC
        val pdql = "PDQL a = 100 and (b = 30 or c = 70)"

        val result = sut.transpiler(pdql)

        val expect = ESQuery(
            bool = ESQuery(
                must = mutableListOf(
                    ESQuery(
                        match = mutableMapOf(Pair("doc.ItemMetadata.a", "100"))
                    ),
                    ESQuery(
                        bool = ESQuery(
                            should = mutableListOf(
                                ESQuery(
                                    match = mutableMapOf(Pair("doc.ItemMetadata.b", "30"))
                                ),
                                ESQuery(
                                    match = mutableMapOf(Pair("doc.ItemMetadata.c", "70"))
                                )
                            ),
                            minimum_should_match = 1
                        )
                    )
                )
            )
        )

        Assertions.assertEquals(2, result.bool?.must?.size)
        Assertions.assertEquals(expect, result)
    }

    @Test
    fun `ensure 3 operating clauses with 'or'`() {
        val pdql = "PDQL a = 100 or b = 100 or c = 'teste'"

        val result = sut.transpiler(pdql)

        val expect = ESQuery(
            bool = ESQuery(
                should = mutableListOf(
                    ESQuery(
                        bool = ESQuery(
                            should = mutableListOf(
                                ESQuery(
                                    match = mutableMapOf(Pair("doc.ItemMetadata.a", "100"))
                                ),
                                ESQuery(
                                    match = mutableMapOf(Pair("doc.ItemMetadata.b", "100"))
                                )
                            ),
                            minimum_should_match = 1
                        )
                    ),
                    ESQuery(
                        match = mutableMapOf(Pair("doc.ItemMetadata.c", "'teste'"))
                    )
                ),
                minimum_should_match = 1
            )
        )

        Assertions.assertEquals(expect, result)
    }
}
