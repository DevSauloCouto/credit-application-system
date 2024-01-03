package me.dio.credit.application.system.DTO.credit

import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.enums.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreditViewDTO(
        val creditCode: UUID,
        val creditValue: BigDecimal,
        val dayFirstInstallment: LocalDate,
        val numberOfInstallment: Int,
        val status: Status,
        val firstNamecustomer: String,
        val emailCustomer: String
) {

    constructor(credit: Credit) : this (
        creditCode = credit.creditCode,
        creditValue = credit.creditValue,
        dayFirstInstallment = credit.dayFirstInstallment,
        numberOfInstallment = credit.numberOfInstallment,
        status = credit.status,
        firstNamecustomer = credit.customer?.firstName!!,
        emailCustomer = credit.customer?.email!!
    )

}