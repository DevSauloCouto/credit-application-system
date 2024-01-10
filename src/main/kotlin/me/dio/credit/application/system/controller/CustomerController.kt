package me.dio.credit.application.system.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import me.dio.credit.application.system.DTO.customer.CustomerDTO
import me.dio.credit.application.system.DTO.customer.CustomerUpdateDTO
import me.dio.credit.application.system.DTO.customer.CustomerViewDTO
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.exceptions.ExceptionDetails
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
@Tag(name = "Customers", description = "Controller to Customers Entity")
class CustomerController(
        private val customerService: CustomerService
) {
    @Operation(
        description = "Esta rota é responsável por salvar um Customer na base de dados. Necessário preencher todos os campos, a API também possui uma validação inteligente de CPF e email.",
        summary = "Rota para salvar uma entidade Customer na base de dados"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "CREATED - Customer salvo com sucesso", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = CustomerViewDTO::class))
                        ))
            ]),
            ApiResponse(responseCode = "400", description = "BAD REQUEST - Ocorreu algum erro na validação", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = ExceptionDetails::class))
                        ))
            ])
        ]
    )
    @PostMapping
    fun saveCustomer(@RequestBody @Valid customerDTO: CustomerDTO): ResponseEntity<CustomerViewDTO> {
        val savedCustomer = this.customerService.save(customerDTO.toEntity());

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerViewDTO(savedCustomer));
    }

    @Operation(
        description = "Esta rota retorna informações de um Customer específico, é necessário passar um ID válido e existente na base de dados, se caso o ID for inválido a API retornará um erro com mais detlahes sobre o ocorrido",
        summary = "Rota para consultar informações de um Customer com base no ID passado no path da requisição"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK - retornou o Customer com sucesso", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = CustomerViewDTO::class))
                        ))
            ]),
            ApiResponse(responseCode = "404", description = "NOT FOUND - não existe nenhum Customer com o mesmo ID passado no path", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = ExceptionDetails::class))
                        ))
            ])
        ]
    )
    @GetMapping("/{customerId}")
    fun findByIdCustomer(@PathVariable customerId: Long): ResponseEntity<CustomerViewDTO> {
        val customer: Customer = this.customerService.findById(customerId);

        return ResponseEntity.status(HttpStatus.OK).body(CustomerViewDTO(customer));
    }

    @Operation(
        description = "Esta rota é responsável por deletar um Customer da base de dados, é necessário passar um ID válido no path da requisição para o Custmoer ser deletado com sucesso, caso contrário a API retornará um erro com mais detalhes",
        summary = "Rota para deletar um Customer da base de dados"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK - Customer deletado com sucesso", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = String::class))
                        ))
            ]),
            ApiResponse(responseCode = "404", description = "NOT FOUND - Customer com o mesmo ID não existe na base de dados", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = ExceptionDetails::class))
                        ))
            ])
        ]
    )
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

    @Operation(
        description = "Essa rota é utilizada para atualizar informações de um Customer, ela recebe um QueryParam 'customer' com o valor do ID de um Customer que o usuário deseja ser atualizado e no corpo da requisição um CustomerUpdateDTO composto por campos como: 'firstName', 'lastName', 'income', 'zipCode', 'street'. Nenhum dos campos deve ser enviado com valor null ou em branco",
        summary = "Rota para atualizar informações de um Customer"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "CREATED - isso significa que o Customer foi atualizado com sucesso", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = CustomerViewDTO::class))
                        ))
            ]),
            ApiResponse(responseCode = "404", description = "NOT FOUND - não existe um Customer com o mesmo ID passado no QueryParam", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = ExceptionDetails::class))
                        ))
            ]),
            ApiResponse(responseCode = "400", description = "BAD REQUEST - ocorreu algum erro, ou algum campo esteja inválido", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = ExceptionDetails::class))
                        ))
            ])
        ]
    )
    @PatchMapping
    fun updateCustomer(@RequestParam(value = "customer") customerId: Long, @RequestBody @Valid customerUpdateDto: CustomerUpdateDTO): ResponseEntity<CustomerViewDTO> {
        val customer: Customer = this.customerService.findById(customerId);

        val customerToUpdate: Customer = customerUpdateDto.toEntity(customer);

        val savedCustomerUpdate: Customer = this.customerService.save(customerToUpdate);

        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerViewDTO(savedCustomerUpdate));
    }

}