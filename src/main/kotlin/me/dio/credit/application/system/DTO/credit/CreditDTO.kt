package me.dio.credit.application.system.DTO.credit

import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.services.implement.CustomerService
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDTO(
        val creditValue: BigDecimal,
        val dayFirstInstallment: LocalDate,
        val numberOfInstallment: Int,
        val customerId: Long

) {

    fun toEntity(): Credit {
        return Credit(
                creditValue = this.creditValue,
                dayFirstInstallment = this.dayFirstInstallment,
                numberOfInstallment = this.numberOfInstallment,
                customer = Customer(id = this.customerId)
        )
    }

}