package me.dio.credit.application.system.controller

import me.dio.credit.application.system.DTO.customer.CustomerDTO
import me.dio.credit.application.system.DTO.customer.CustomerUpdateDTO
import me.dio.credit.application.system.DTO.customer.CustomerViewDTO
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.services.implement.CustomerService
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.StringBuilder

@RestController
@RequestMapping("/api/customers")
class CustomerController(
        private val customerService: CustomerService
) {

    @PostMapping
    fun saveCustomer(@RequestBody customerDTO: CustomerDTO): ResponseEntity<CustomerViewDTO> {
        val savedCustomer = this.customerService.save(customerDTO.toEntity());

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerViewDTO(savedCustomer));
    }

    @GetMapping("/{customerId}")
    fun findByIdCustomer(@PathVariable customerId: Long): ResponseEntity<CustomerViewDTO> {
        val customer: Customer = this.customerService.findById(customerId);

        return ResponseEntity.status(HttpStatus.OK).body(CustomerViewDTO(customer));
    }

    @DeleteMapping("/{customerId}")
    fun deleteByIdCustomer(@PathVariable customerId: Long): ResponseEntity<String> {
        val customer: Customer = this.customerService.findById(customerId);
        this.customerService.delete(customerId);

        val strBuilder = StringBuilder().append(CustomerViewDTO(customer).firstName)
                .append(" ")
                .append(CustomerViewDTO(customer).lastName)
                .append(" deletado com sucesso!")
                .toString();

        return ResponseEntity.status(HttpStatus.OK).body(strBuilder);
    }

    @PatchMapping
    fun updateCustomer(@RequestParam(value = "customerId") customerId: Long, @RequestBody customerUpdate: CustomerUpdateDTO): ResponseEntity<CustomerViewDTO> {
        val customer: Customer = this.customerService.findById(customerId);

        val customerToUpdate: Customer = customerUpdate.toEntity(customer);

        val savedCustomerUpdate: Customer = this.customerService.save(customerToUpdate);

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerViewDTO(savedCustomerUpdate));
    }

}