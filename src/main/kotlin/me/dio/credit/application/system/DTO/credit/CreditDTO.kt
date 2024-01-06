package me.dio.credit.application.system.DTO.credit

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.exceptions.DateInvalidException
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.services.implement.CustomerService
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDTO(
        @field:NotNull(message = "Please, this field cannot be empty") val creditValue: BigDecimal,
        @field:Future(message = "Please provide a future date") val dayFirstInstallment: LocalDate,
        @field:NotNull @field:Max(48) @field:Min(1) val numberOfInstallment: Int,
        @field:NotNull(message = "Please, this field cannot be empty") val customerId: Long

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