package me.dio.credit.application.system.DTO.credit

import me.dio.credit.application.system.entities.Credit
import java.math.BigDecimal
import java.util.*

data class CreditListDTO(
        val creditCode: UUID,
        val creditValue: BigDecimal,
        val numberOfInstallment: Int
) {

    constructor(credit: Credit) : this (
            creditCode = credit.creditCode,
            creditValue = credit.creditValue,
            numberOfInstallment = credit.numberOfInstallment
    )

}