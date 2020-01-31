package com.example.mailbox.controller

import com.example.mailbox.backend.BackendManager

interface ControllerExtension {
    fun init(controller: MessagingController, backendManager: BackendManager, controllerInternals: ControllerInternals)

    interface ControllerInternals {
        fun put(description: String, listener: MessagingListener?, runnable: Runnable)
        fun putBackground(description: String, listener: MessagingListener?, runnable: Runnable)
    }
}
