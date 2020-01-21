package com.mavenclinic.mobile.demo.common.domain.repos.production

import com.mavenclinic.mobile.demo.common.domain.model.State
import com.mavenclinic.mobile.demo.common.domain.repos.MetadataRepo
import com.mavenclinic.mobile.demo.common.utility.cache.SuspendingCache
import kotlinx.coroutines.delay

class MetadataRepoImpl : MetadataRepo {

    private val stateCache = SuspendingCache<Map<String,State>>(::fetchUsStates)

    override suspend fun getUsStates(): Map<String, State> {
        return stateCache.get()
    }


    private suspend fun fetchUsStates(): Map<String,State> {

        // TODO change to api call
        delay(500)  // simulate I/O

        return listOf(
            "NY" to "New York",
            "CT" to "Connecticut",
            "CA" to "California"
        )
            .map { State(abbreviation = it.first, displayName = it.second)}
            .associateBy { it.abbreviation }
    }
}