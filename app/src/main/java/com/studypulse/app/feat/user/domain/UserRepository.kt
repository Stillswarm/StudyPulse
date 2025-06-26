package com.studypulse.app.feat.user.domain

interface UserRepository {
    suspend fun addUser(user: User): Result<Unit>

    suspend fun deleteUser(user: User): Result<Unit>

    suspend fun getUserById(id: String): Result<User?>
}