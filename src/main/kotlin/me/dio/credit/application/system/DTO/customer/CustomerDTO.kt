package me.dio.credit.application.system.DTO.customer

import me.dio.credit.application.system.entities.Address
import me.dio.credit.application.system.entities.Customer
import java.math.BigDecimal

data class CustomerDTO(
        val firstName: String,
        val lastName: String,
        val cpf: String,
        val income: BigDecimal,
        val email: String,
        val password: String,
        val zipCode: String,
        val street: String
) {

    fun toEntity(): Customer {
        return Customer(
                firstName = this.firstName,
                lastName = this.lastName,
                cpf = this.cpf,
                income = this.income,
                email = this.email,
                password = this.password,
                address = Address(this.zipCode, this.street)
        )
    }

}