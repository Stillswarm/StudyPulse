package com.studypulse.app.feat.user.domain

import com.studypulse.app.feat.user.domain.model.User

interface UserRepository {
    suspend fun fetchCurrentUser(): Result<User?>

    suspend fun addUser(user: User): Result<Unit>

    suspend fun deleteUser(user: User): Result<Unit>

    suspend fun getUserById(id: String): Result<User?>

    suspend fun updateName(newName: String): Result<Unit>

    suspend fun updateInstitution(newInstitution: String): Result<Unit>
}