package com.example.pdql.factory

import com.example.pdql.port.IAntlrAdapter
import com.example.pdql.presentation.adapter.AntlrAdapter

object AntlrAdapterFactory {
    fun create(): IAntlrAdapter = AntlrAdapter()
}
