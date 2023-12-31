package me.dio.credit.application.system.services.implement

import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.services.ICustomerService
import org.springframework.stereotype.Service

@Service
class CustomerService(
        private val customerRepository: CustomerRepository
): ICustomerService {

    override fun save(customer: Customer): Customer {
        return this.customerRepository.save(customer);
    }

    override fun findById(customerId: Long): Customer {
        return this.customerRepository.findById(customerId).orElseThrow {
                throw RuntimeException("Id $customerId Not Found")
        }
    }

    override fun delete(customerId: Long) = this.customerRepository.deleteById(customerId)

}