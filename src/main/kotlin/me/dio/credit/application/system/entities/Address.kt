package me.dio.credit.application.system.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Address(
    @Column(nullable = false) var zipcode: String = "",
    @Column(nullable = false) var street: String = ""
)