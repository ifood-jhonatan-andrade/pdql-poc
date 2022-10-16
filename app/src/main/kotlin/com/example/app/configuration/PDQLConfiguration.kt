package com.example.app.configuration

import com.example.pdql.component.PDQLComponent
import com.example.pdql.port.IPDQLComponent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PDQLConfiguration {
    @Bean
    fun pdqlComponent(): IPDQLComponent = PDQLComponent()
}
