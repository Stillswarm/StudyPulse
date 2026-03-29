package com.studypulse.core.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

abstract class BaseFirebaseRepository(
    protected val auth: FirebaseAuth,
    protected val db: FirebaseFirestore,
) {
    protected fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")

    protected fun userDocument(): DocumentReference =
        db.collection("users").document(requireUserId())

    protected fun userCollection(vararg pathSegments: String): CollectionReference =
        db.collection(
            "users/${requireUserId()}/" + pathSegments.joinToString("/")
        )

    protected fun <T : Any> Query.snapshotFlow(
        transform: (DocumentSnapshot) -> T?,
    ): Flow<List<T>> = callbackFlow {
        val registration = addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull(transform) ?: emptyList()
            trySend(items)
        }
        awaitClose { registration.remove() }
    }
}
