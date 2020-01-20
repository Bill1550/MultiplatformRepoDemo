package com.mavenclinic.mobile.demo.common.utility.cache

interface CacheValidator<T,C> {

    fun isFresh( entry: CacheEntry<T, C>): Boolean

    fun createContext( entry: T) : C

    fun createContext( t: Throwable): C
}