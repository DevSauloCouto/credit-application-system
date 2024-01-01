package me.dio.credit.application.system.services

import me.dio.credit.application.system.entities.Customer

interface ICustomerService {

    fun save(customer: Customer): Customer
    fun findById(customerId: Long): Customer
    fun delete(customerId: Long)

}