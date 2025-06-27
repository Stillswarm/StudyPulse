package com.studypulse.app

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


object NavigationDrawerController {
    private val _events = Channel<Unit>()
    val events = _events.receiveAsFlow()

    suspend fun toggle() {
        _events.send(Unit)
    }
}