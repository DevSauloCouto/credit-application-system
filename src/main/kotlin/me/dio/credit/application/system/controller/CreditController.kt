package me.dio.credit.application.system.controller

import jakarta.validation.Valid
import me.dio.credit.application.system.DTO.credit.CreditDTO
import me.dio.credit.application.system.DTO.credit.CreditListDTO
import me.dio.credit.application.system.DTO.credit.CreditViewDTO
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.services.implement.CreditService
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
class CreditController(
        private val creditService: CreditService
) {

    @PostMapping
    fun saveCredit(@RequestBody @Valid creditDTO: CreditDTO): ResponseEntity<CreditViewDTO> {
        val credit = this.creditService.save(creditDTO.toEntity());

        return ResponseEntity.status(HttpStatus.CREATED).body(CreditViewDTO(credit));
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<CreditListDTO>> {
        val listCredits: List<CreditListDTO> = this.creditService.findAllByCustomer(customerId)
                .stream()
                .map { credit: Credit -> CreditListDTO(credit) }
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(listCredits);
    }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(@RequestParam(value = "customerId") customerId: Long, @PathVariable creditCode: UUID): ResponseEntity<CreditViewDTO> {
        val credit: Credit = this.creditService.findByCreditCode(customerId, creditCode);

        return ResponseEntity.status(HttpStatus.OK).body(CreditViewDTO(credit));
    }

}