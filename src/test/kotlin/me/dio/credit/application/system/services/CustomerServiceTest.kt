package me.dio.credit.application.system.services

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import me.dio.credit.application.system.entities.Address
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.exceptions.NotFoundException
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.services.implement.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
    @MockK
    lateinit var customerRepository: CustomerRepository;

    @InjectMockKs
    lateinit var customerService: CustomerService;

    @Test
    fun `should created customer`(){
        //given
        val fakeCustomer: Customer = buildCustomer();
        every { customerRepository.save(any()) } returns fakeCustomer;

        //when
        val actual: Customer = customerService.save(fakeCustomer);

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCustomer);
        verify(exactly = 1) { customerRepository.save(fakeCustomer) }

    }

    @Test
    fun `should find customer by id`(){
        //given
        val fakeId: Long = Random().nextLong();
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)

        //when
        val actual: Customer = customerService.findById(fakeId);

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isExactlyInstanceOf(Customer::class.java)
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.findById(fakeId) }

    }

    @Test
    fun `should not find customer by id and throw NotFoundException`(){
        //given
        val fakeId: Long = Random().nextLong();
        every { customerRepository.findById(fakeId) } returns Optional.empty()

        //when
        //then
        Assertions.assertThatExceptionOfType(NotFoundException::class.java)
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("Id $fakeId Not Found")
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should delete customer by id`(){
        //given
        val fakeId: Long = Random().nextLong();
        val fakeCustomer: Customer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        every { customerRepository.delete(fakeCustomer) } just runs

        //when
        customerService.delete(fakeId);

        every { customerRepository.findById(fakeId) } returns Optional.empty()

        //then
        Assertions.assertThatExceptionOfType(NotFoundException::class.java)
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("Id $fakeId Not Found")
        verify(exactly = 2) { customerRepository.findById(fakeId) }
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }

    fun buildCustomer(
        firstName: String = "Saulo",
        lastName: String = "Couto",
        cpf: String = "08316540584",
        email: String = "sl@teste",
        password: String = "!@sl123",
        zipCode: String = "491658890",
        street: String = "C",
        income: BigDecimal = BigDecimal.valueOf(521.70),
        id: Long = 1L
    ): Customer {
        return Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                zipcode = zipCode,
                street = street
            ),
            income = income,
            id = id
        )
    }

}

