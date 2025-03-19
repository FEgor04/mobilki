package com.koji

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AppLogger {
    fun getLogger(name: String): Logger = LoggerFactory.getLogger(name)
}