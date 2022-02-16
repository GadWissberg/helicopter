package com.gadarts.helicopter.core.systems

interface Notifier<T> {
    val subscribers: HashSet<T>


    fun subscribeForEvents(subscriber: T) {
        subscribers.add(subscriber)
    }


}
