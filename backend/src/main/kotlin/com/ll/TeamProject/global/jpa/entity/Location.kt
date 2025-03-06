package com.ll.TeamProject.global.jpa.entity

import jakarta.persistence.Embeddable

@Embeddable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String
) {
    constructor() : this(0.0, 0.0, "")
}

