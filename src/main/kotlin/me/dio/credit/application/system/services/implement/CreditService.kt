package me.dio.credit.application.system.services.implement

import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.services.ICreditService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreditService(
        private val creditRepository: CreditRepository,
        private val customerService: CustomerService
): ICreditService {

    override fun save(credit: Credit): Credit {
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
                ?: throw RuntimeException("Credit code $creditCode not found");
        return if(credit.customer?.id == creditCustomerId)
                   credit
               else
                   throw RuntimeException("Contact administrator");
    }

}