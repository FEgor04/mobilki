package com.koji

import com.koji.plugins.configureDatabases
import com.koji.plugins.configureMonitoring
import com.koji.plugins.configureRouting
import com.koji.plugins.configureSecurity
import com.koji.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
