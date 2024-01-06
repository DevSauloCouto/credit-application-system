package me.dio.credit.application.system.services.implement

import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.exceptions.DateInvalidException
import me.dio.credit.application.system.exceptions.NotFoundException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.services.ICreditService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class CreditService(
        private val creditRepository: CreditRepository,
        private val customerService: CustomerService
): ICreditService {

    override fun save(credit: Credit): Credit {
        this.validFirstDayInstallment(credit.dayFirstInstallment)
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> {
        return this.creditRepository.findAllByCustomerId(customerId);
    }

    override fun findByCreditCode(creditCustomerId: Long, creditCode: UUID): Credit {
        val credit: Credit = this.creditRepository.findByCreditCode(creditCode)
                ?: throw NotFoundException("Credit code $creditCode not found");
        return if(credit.customer?.id == creditCustomerId)
                   credit
               else
                   throw IllegalAccessException("Contact administrator");
    }

    private fun validFirstDayInstallment(dayFirstInstallment: LocalDate): Boolean {
        return if(dayFirstInstallment.isBefore(LocalDate.now().plusMonths(3)))
                    true
                else
            throw DateInvalidException("It is only possible to request a loan if the payment date of the first installment is 3 months after the requested date")
    }

}