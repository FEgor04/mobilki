package com.koji.auth.repository

import com.koji.UsersTable
import com.koji.AppLogger
import com.koji.DatabaseFactory.dbQuery
import com.koji.auth.model.User
import com.koji.exceptions.*
import org.jetbrains.exposed.sql.*

class UserRepository {
    private val logger = AppLogger.getLogger(this::class.java.name)

    suspend fun createUser(user: User): User = dbQuery {
        try {

            findUserByEmail(user.email)?.let {
                throw UserAlreadyExistsException(user.email)
            }

            val insertStatement = UsersTable.insert {
                it[id] = user.id
                it[email] = user.email
                it[password] = user.password
                it[name] = user.name
            }

            insertStatement.resultedValues?.singleOrNull()?.let { rowResult ->
                User(
                    id = rowResult[UsersTable.id],
                    email = rowResult[UsersTable.email],
                    password = rowResult[UsersTable.password],
                    name = rowResult[UsersTable.name]
                )
            } ?: throw DatabaseOperationException("create user")
        } catch (e: Exception) {
            logger.error("Error creating user: ${e.message}")
            throw DatabaseOperationException("create user")
        }
    }

    suspend fun findUserByEmail(email: String): User? = dbQuery {
        UsersTable.select { UsersTable.email eq email }
            .map { row ->
                User(
                    id = row[UsersTable.id],
                    email = row[UsersTable.email],
                    password = row[UsersTable.password],
                    name = row[UsersTable.name]
                )
            }
            .singleOrNull()
    }

    suspend fun findUserById(id: String): User = dbQuery {
        UsersTable.select { UsersTable.id eq id }
            .map { row ->
                User(
                    id = row[UsersTable.id],
                    email = row[UsersTable.email],
                    password = row[UsersTable.password],
                    name = row[UsersTable.name]
                )
            }
            .singleOrNull() ?: throw UserNotFoundException(id)
    }
}
