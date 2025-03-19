package com.koji

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(jdbcUrl: String, driverClassName: String, username: String, password: String) {
        val database = Database.connect(
            url = jdbcUrl,
            driver = driverClassName,
            user = username,
            password = password
        )

        // Initialize database schema
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
