package com.koji

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

object UsersTable : Table("users") {
    val id = varchar("id", 36)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val name = varchar("name", 255)

    override val primaryKey = PrimaryKey(id)
}