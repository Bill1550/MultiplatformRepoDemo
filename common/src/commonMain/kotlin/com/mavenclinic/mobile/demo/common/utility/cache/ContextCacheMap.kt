package com.mavenclinic.mobile.demo.common.utility.cache

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A coroutine based cache that uses a separate CoroutineScope
 * to run the fetching function.  This allows the fetcher to complete
 * even if a requester is cancelled.
 * If additional coroutines request an item while the fetcher is running
 * from the initial request, the fetcher is not relaunched even if the
 * initiating coroutine is cancelled.
 *
 * Supports null cache entries if the fetcher returns a nullable type.
 *
 * A CacheValidator object is used to determine if a cache entry is
 * still fresh.  The validator is called when the entry is first created
 * to supply an entry context where the validator can save load time, etc.
 * When an entry is fetched the validator is asked to determine freshness.
 * This allows the TTL calculation to be item dependent.
 *
 * Type parameters:
 *  K - map key
 *  T - content type
 *  C - validation context
 */
class ContextCacheMap<in K: Any, T: Any?, C: Any>(

    /**
         * An instance of CacheValidator that determines if an item
         * in the cache can still be used.
         * The TimeValidator concrete instance provides simple TTL validation.
         */
        private val validator: CacheValidator<T, C>,

    private val fetchingScope: CoroutineScope,

    private val fetcher: suspend(K)->T

) : CacheMap<K, T> {

    private val map = mutableMapOf<K, CacheEntry<T, C>>()
    private val mutex = Mutex()
    private var fetchingJob: Job? = null


    override suspend fun get(key: K): T {

        // Initially look in cache w/o locking
        return (map[key]?.takeIf { validator.isFresh(it) } ?: let {
            // Not in cache, or stale, need to do a fetch (maybe)
            mutex.lock()

            // check cache again (double check cache locking), in case entry arrived while we were waiting for mutex
            map[key]?.takeIf { validator.isFresh(it) }?.let {
                // did appear while locking
                mutex.unlock()
                it // return what we got
            } ?: let {
                // now do the fetch
                map.remove(key) // make sure cache entry is clear (in case it was stale)

                // Go get a new entry, running in a separate coroutine scope
                fetchingJob = fetchingScope.launch {
                    try {
                        map[key] = fetcher(key).let { CacheEntry.Data(validator.createContext(it), it) }
                    } catch (ce: CancellationException) {
                        // don't store cancellation exceptions.
                    } catch (t: Throwable) {
                        map[key] = CacheEntry.Error( validator.createContext(t), t)
                    } finally {
                        mutex.unlock() // make sure mutex is always unlocked when we finish I/O
                    }
                }.apply { join() } // wait for the fetching job to complete
                map[key] // return the entry that the fetching job found
            }
        } ).let { entry ->
            if (entry == null)
                throw CancellationException("cancelled")

            entry.dispatch()
        }

    }

    override suspend fun clear() {
        fetchingJob?.cancelAndJoin()
        mutex.withLock {
            map.clear()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun CacheEntry<T, C>.dispatch(): T {
        return when ( this ) {
            is CacheEntry.Data<*,*> -> data as T
            is CacheEntry.Error -> throw error
        }
    }

}