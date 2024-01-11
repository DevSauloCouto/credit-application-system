package me.dio.credit.application.system.DTO.customer

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.entities.Address
import me.dio.credit.application.system.entities.Customer
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDTO(
        @field:NotEmpty(message = "Please, this field cannot be empty")
        val firstName: String,

        @field:NotEmpty(message = "Please, this field cannot be empty")
        val lastName: String,

        @field:CPF(message = "CPF Invalid")
        val cpf: String,

        @field:NotNull(message = "Please, this field cannot be empty")
        val income: BigDecimal,

        @field:Email(message = "Sorry, Email Invalid") @field:NotEmpty(message = "Please, this field cannot be empty")
        val email: String,

        @field:NotEmpty(message = "Please, this field cannot be empty")
        val password: String,

        @field:NotEmpty(message = "Please, this field cannot be empty")
        val zipCode: String,

        @field:NotEmpty(message = "Please, this field cannot be empty")
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