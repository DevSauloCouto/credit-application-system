package me.dio.credit.application.system.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import me.dio.credit.application.system.DTO.credit.CreditDTO
import me.dio.credit.application.system.DTO.credit.CreditListDTO
import me.dio.credit.application.system.DTO.credit.CreditViewDTO
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.exceptions.ExceptionDetails
import me.dio.credit.application.system.services.implement.CreditService
import me.dio.credit.application.system.services.implement.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
@Tag(name = "Credits", description = "Controller for Credit Entity")
class CreditController(
        private val creditService: CreditService,
        private val customerService: CustomerService
) {

    @Operation(
        description = "Utilizado para criar uma nova solicitação de crédito. O campo 'numberOfInstallment' deve ser preenchido com um valor entre 1 e 48. O campo 'dayFirstInstallment' deve ser preenchido com uma data após 3 meses da data solicitada o crédito. Nenhum campo deve ser enviado com valor null.",
        summary = "Salva um Entity Credit no Database - Usado para solicitar um empréstimo de crédito"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Created - Todos os campos corretamente preenchidos", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = CreditDTO::class))
                        ))
            ]),
            ApiResponse(responseCode = "400", description = "Bad Request - Campos inválidos, possíveis: 'numberOfInstallment', 'dayFirstInstallment'. Nenhum dos campos deve ser enviado com valor null", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = ExceptionDetails::class))
                        ))
            ])
        ]
    )
    @PostMapping
    fun saveCredit(@RequestBody @Valid creditDTO: CreditDTO): ResponseEntity<CreditViewDTO> {
        val credit = this.creditService.save(creditDTO.toEntity());

        return ResponseEntity.status(HttpStatus.CREATED).body(CreditViewDTO(credit));
    }

    @Operation(
        description = "Utilizado para consultar uma lista de Credit que pertencem a um Customer. Necessário passar um QueryParam 'customerId' com um valor numérico, que representa o ID (PK) de um Customer no Database",
        summary = "Retorna uma lista de CreditListDto pertencentes a um Customer com base na QueryParam(customerId)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK - Consulta realizada com sucesso (obs: pode retornar nenhuma informação, caso o Customer não possua nenhum Credit relacionado a ele)", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = CreditListDTO::class))
                        ))
            ]),
            ApiResponse(responseCode = "404", description = "Not Found - Não existe nenhum Customer com o mesmo ID passado na QueryParam", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = ExceptionDetails::class))
                        ))
            ])
        ]
    )
    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<CreditListDTO>> {
        val customer: Customer = this.customerService.findById(customerId);
        val listCredits: List<CreditListDTO> = this.creditService.findAllByCustomer(customer.id!!)
                .stream()
                .map { credit: Credit -> CreditListDTO(credit) }
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(listCredits);
    }

    @Operation(
        description = "Usado para consultar informações de um determinado Credit, necessário passar o creditCode do Credit no path da requisição e um QueryParam - 'customerId' com um valor numérico que representa o ID (PK) de um Customer no Database. (obs: Apenas é retornado se o Credit baseado no creditCode pertencer ao Customer baseado no customerId)",
        summary = "Retorna detalhadamente informações de um Credit específico com base no creditCode passado no path da requisição e um QueryParam(customerId) pertencente ao ID de um Customer"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "OK - Retorna um determinado Credit de um Customer com mais informações", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = CreditViewDTO::class))
                        ))
            ]),
            ApiResponse(responseCode = "404", description = "Not Found - quando não existe um Customer com o mesmo ID passado no QueryParam da requisição OU quando não existe um Credit com o mesmo creditCode passado no path da requisição", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = ExceptionDetails::class))
                        ))
            ]),
            ApiResponse(responseCode = "400", description = "Bad Request - Quando um Credit não pertence ao Customer referenciado pelo ID passado no QueryParam", content = [
                Content(mediaType = "application/json", array = (
                        ArraySchema(schema = Schema(implementation = ExceptionDetails::class))
                        ))
            ])
        ]
    )
    @GetMapping("/{creditCode}")
    fun findByCreditCode(@RequestParam(value = "customerId") customerId: Long, @PathVariable creditCode: UUID): ResponseEntity<CreditViewDTO> {
        val customer: Customer = this.customerService.findById(customerId);
        val credit: Credit = this.creditService.findByCreditCode(customer.id!!, creditCode);

        return ResponseEntity.status(HttpStatus.OK).body(CreditViewDTO(credit));
    }

}