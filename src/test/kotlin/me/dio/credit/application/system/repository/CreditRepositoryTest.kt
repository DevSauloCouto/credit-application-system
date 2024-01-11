package me.dio.credit.application.system.repository

import me.dio.credit.application.system.entities.Address
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {

    @Autowired
    lateinit var creditRepository: CreditRepository

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer;
    private lateinit var customer2: Customer;
    private lateinit var credit1: Credit;
    private lateinit var credit2: Credit;
    private lateinit var credit3: Credit;
    private lateinit var credit4: Credit;

    @BeforeEach
    fun setup(){
        customer = testEntityManager.merge(buildCustomer())
        customer2 = testEntityManager.merge(buildCustomer(email = "teste.com", cpf = "05336578944", id = 2L))
        credit1 = testEntityManager.persist(buildCredit(customer = customer))
        credit2 = testEntityManager.persist(buildCredit(customer = customer))

        credit3 = testEntityManager.persist(buildCredit(customer = customer2))
        credit4 = testEntityManager.persist(buildCredit(customer = customer2))
    }

    @Test
    fun `should find credit in database by creditCode`(){
        //given
        val creditCode1 = UUID.randomUUID();
        val creditCode2 = UUID.randomUUID();
        credit1.creditCode = creditCode1;
        credit2.creditCode = creditCode2;

        //when
        val fakeCredit1: Credit? = creditRepository.findByCreditCode(creditCode1);
        val fakeCredit2: Credit? = creditRepository.findByCreditCode(creditCode2);

        //then
        Assertions.assertThat(fakeCredit1).isNotNull
        Assertions.assertThat(fakeCredit2).isNotNull

        Assertions.assertThat(fakeCredit1).isSameAs(credit1)
        Assertions.assertThat(fakeCredit2).isSameAs(credit2)

        Assertions.assertThat(fakeCredit1).isExactlyInstanceOf(Credit::class.java)
        Assertions.assertThat(fakeCredit2).isExactlyInstanceOf(Credit::class.java)

        Assertions.assertThat(fakeCredit1?.customer!!.id).isEqualTo(customer.id)
        Assertions.assertThat(fakeCredit2?.customer!!.id).isEqualTo(customer.id)

    }

    @Test
    fun `should find all credits by customerId`(){
        //given
        val customerId: Long = 3L
        //when
        val creditList: List<Credit> = creditRepository.findAllByCustomerId(customerId)
        //then
        Assertions.assertThat(creditList).isNotEmpty
        Assertions.assertThat(creditList.size).isEqualTo(2)
        Assertions.assertThat(creditList).contains(credit1, credit2)
        creditList.stream().forEach {
            Assertions.assertThat(this.customer.id).isEqualTo(3L)
        }
    }

    private fun buildCustomer(
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

    private fun buildCredit(
            creditValue: BigDecimal = BigDecimal.valueOf(521.70),
            dayFirstInstallment: LocalDate = LocalDate.now(),
            numberOfInstallment: Int = 12,
            customer: Customer = this.buildCustomer()
        ): Credit {
            return Credit(
                creditValue = creditValue,
                dayFirstInstallment = dayFirstInstallment,
                numberOfInstallment = numberOfInstallment,
                customer = customer
            )
    }

}