package me.dio.credit.application.system.DTO.customer

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.entities.Customer
import java.math.BigDecimal

data class CustomerUpdateDTO (
    @field:NotEmpty(message = "Please, this field cannot be empty")
    val firstName: String,

    @field:NotEmpty(message = "Please, this field cannot be empty")
    val lastName: String,

    @field:NotNull(message = "Please, this field cannot be empty")
    val income: BigDecimal,

    @field:NotEmpty(message = "Please, this field cannot be empty")
    val zipCode: String,

    @field:NotEmpty(message = "Please, this field cannot be empty")
    val street: String
) {

    fun toEntity(customer: Customer): Customer {

        customer.firstName = this.firstName;
        customer.lastName = this.lastName;
        customer.income = this.income;
        customer.address.zipcode = this.zipCode;
        customer.address.street = this.street;

        return customer;
    }

}