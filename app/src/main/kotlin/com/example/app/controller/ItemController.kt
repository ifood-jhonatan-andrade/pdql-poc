package com.example.app.controller

import com.example.app.dto.CreateDTO
import com.example.pdql.domain.ESQuery
import com.example.pdql.port.IPDQLComponent
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("item")
class ItemController(
    val pdqlComponent: IPDQLComponent
) {

    @PostMapping
    fun create(
        @RequestBody createDTO: CreateDTO
    ): ESQuery {
        return pdqlComponent.transpiler(createDTO.pdql)
    }
}
