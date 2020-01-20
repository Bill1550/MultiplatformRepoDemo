package com.mavenclinic.mobile.demo.common.utility.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A cache, where the element is supplied
 * by a suspending function.
 *
 * Created by BillH on 5/22/2019
 */
class SuspendingCache<T: Any>(
        private val creator: suspend ()->T
) {

    private val mutex = Mutex()
    private var cache: T? = null

    suspend fun get(): T {
        return cache ?: mutex.withLock {
            cache ?: let {
                creator().apply {
                    cache = this
                }
            }
        }
    }

    suspend fun clear() {
        mutex.withLock { cache = null }
    }

    val isLoaded: Boolean
        get() = cache != null
}