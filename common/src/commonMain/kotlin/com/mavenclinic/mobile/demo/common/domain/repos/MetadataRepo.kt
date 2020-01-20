package com.mavenclinic.mobile.demo.common.domain.repos

import com.mavenclinic.mobile.demo.common.domain.model.State

interface MetadataRepo {

    /**
     * Returns a map of the US States, indexed by state abbreviations
     */
    suspend fun getUsStates(): Map<String, State>
}