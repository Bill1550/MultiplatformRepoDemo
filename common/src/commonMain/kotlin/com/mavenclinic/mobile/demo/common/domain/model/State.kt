package com.mavenclinic.mobile.demo.common.domain.model


data class State(val displayName: String,
                 val abbreviation: String = ""
) {

    @Suppress("unused")
    val stringRepresentation =
        if (abbreviation.isEmpty()) {
            displayName
        } else {
            "$displayName ($abbreviation)"
        }
}