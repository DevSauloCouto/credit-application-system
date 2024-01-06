package me.dio.credit.application.system.services

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.exceptions.DateInvalidException
import me.dio.credit.application.system.exceptions.NotFoundException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.services.implement.CreditService
import me.dio.credit.application.system.services.implement.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK
    lateinit var creditRepository: CreditRepository

    @MockK
    lateinit var customerService: CustomerService

    @InjectMockKs
    lateinit var creditService: CreditService

    @Test
    fun `should save a credit`(){
        //given
        val fakeCredit: Credit = buildCredit();
        val customerId: Long = 1L;

        every { customerService.findById(customerId) } returns fakeCredit.customer!!;
        every { creditRepository.save(any()) } returns fakeCredit;

        //when
        val actual: Credit = creditService.save(fakeCredit);

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isExactlyInstanceOf(Credit::class.java)
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { customerService.findById(customerId) }
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun `not should save a credit, invalid date`(){
        //given
        val fakeCredit: Credit = buildCredit(dayFirstInstallment = LocalDate.now().plusMonths(5));

        every { creditRepository.save(fakeCredit) } answers { fakeCredit }

        //when

        //then
        Assertions.assertThatThrownBy { creditService.save(fakeCredit) }
            .isInstanceOf(DateInvalidException::class.java)
            .hasMessage("It is only possible to request a loan if the payment date of the first installment is 3 months before the requested date")
        verify(exactly = 0) { creditRepository.save(any()) }
    }

    @Test
    fun `should find all credits by customer id of a customer`(){
        //given
        val fakeCustomerId: Long = 1L;
        val fakeCredits: List<Credit> = listOf(buildCredit(), buildCredit(), buildCredit())
        every { creditRepository.findAllByCustomerId(fakeCustomerId) } returns fakeCredits;

        //when
        val actual: List<Credit> = creditService.findAllByCustomer(fakeCustomerId);

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isNotEmpty
        Assertions.assertThat(actual).isSameAs(fakeCredits)
        verify(exactly = 1) { creditRepository.findAllByCustomerId(fakeCustomerId) }
    }

    @Test
    fun `should find credit by creditCode`(){
        //given
        val fakeCustomerId: Long = 1L;
        val fakeCredit: Credit = buildCredit();

        every { customerService.findById(fakeCustomerId) } returns fakeCredit.customer!!
        every { creditRepository.findByCreditCode(fakeCredit.creditCode) } returns fakeCredit

        //when
        val actual: Credit = creditService.findByCreditCode(fakeCustomerId, fakeCredit.creditCode);

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        Assertions.assertThat(actual).isExactlyInstanceOf(Credit::class.java)
        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCredit.creditCode) }
    }

    @Test
    fun `should don't find credit by creditCode and throw NotFoundException`(){
        //given
        val fakeCustomerId: Long = 1L;
        val fakeCreditCode: UUID = UUID.randomUUID();

        every { creditRepository.findByCreditCode(fakeCreditCode) } returns null
        //when

        //then
        Assertions.assertThatThrownBy { creditService.findByCreditCode(fakeCustomerId, fakeCreditCode) }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("Credit code $fakeCreditCode not found")
        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }
    }

    @Test
    fun `should don't find credit by customerId different`(){
        //given
        val fakeCustomerId: Long = 2L;
        val fakeCredit: Credit = buildCredit();

        every { customerService.findById(fakeCredit.customer?.id!!) } returns fakeCredit.customer!!
        every { creditRepository.findByCreditCode(fakeCredit.creditCode) } returns fakeCredit

        //when

        //then
        Assertions.assertThatThrownBy { creditService.findByCreditCode(fakeCustomerId, fakeCredit.creditCode) }
            .isInstanceOf(IllegalAccessException::class.java)
            .hasMessage("Contact administrator")
        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCredit.creditCode) }
    }

    fun buildCredit(
        creditValue: BigDecimal = BigDecimal.valueOf(521.70),
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallment: Int = 12,
        customer: Customer = CustomerServiceTest().buildCustomer(),
    ): Credit {
        return Credit(
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
            numberOfInstallment = numberOfInstallment,
            customer = customer
        )
    }

}