package me.dio.credit.application.system.controller

import me.dio.credit.application.system.DTO.CustomerDTO
import me.dio.credit.application.system.DTO.CustomerUpdateDTO
import me.dio.credit.application.system.DTO.CustomerViewDTO
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.services.implement.CustomerService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers")
class CustomerController(
        private val customerService: CustomerService
) {

    @PostMapping
    fun saveCustomer(@RequestBody customerDTO: CustomerDTO): CustomerViewDTO {
        val savedCustomer = this.customerService.save(customerDTO.toEntity());

        return CustomerViewDTO(savedCustomer);
    }

    @GetMapping("/{customerId}")
    fun findByIdCustomer(@PathVariable customerId: Long): CustomerViewDTO {
        val customer: Customer = this.customerService.findById(customerId);
        return CustomerViewDTO(customer);
    }

    @DeleteMapping("/{customerId}")
    fun deleteByIdCustomer(@PathVariable customerId: Long): String {
        this.customerService.delete(customerId);
        return "Customer deletado com sucesso";
    }

    @PatchMapping
    fun updateCustomer(@RequestParam(value = "customerId") customerId: Long, @RequestBody customerUpdate: CustomerUpdateDTO): CustomerViewDTO {
        val customer: Customer = this.customerService.findById(customerId);
        val customerToUpdate: Customer = customerUpdate.toEntity(customer);
        val savedCustomerUpdate: Customer = this.customerService.save(customerToUpdate);
        return CustomerViewDTO(savedCustomerUpdate);
    }

}