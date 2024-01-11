package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.DTO.credit.CreditDTO
import me.dio.credit.application.system.entities.Credit
import me.dio.credit.application.system.entities.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditControllerTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository;

    @Autowired
    private lateinit var creditRepository: CreditRepository;

    @Autowired
    private lateinit var mockMvc: MockMvc;

    @Autowired
    private lateinit var objectMapper: ObjectMapper;

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup(){
        customerRepository.deleteAll();
        creditRepository.deleteAll();
    }

    @AfterEach
    fun tearDown(){
        customerRepository.deleteAll();
        creditRepository.deleteAll();
    }

    @Test
    fun `should save a credit and return status 201`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val creditDto: CreditDTO = this.buildCreditDto(customerId = customer.id);
        val valueAsString: String = this.objectMapper.writeValueAsString(creditDto);
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(creditDto.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dayFirstInstallment").value(creditDto.dayFirstInstallment.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(creditDto.numberOfInstallment))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(creditDto.toEntity().status.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstNamecustomer").value(customer.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't save a credit with field 'dayFirstInstallment' date invalid and return status 400`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val creditDTO: CreditDTO = this.buildCreditDto(dayFirstInstallment = LocalDate.now().plusMonths(5), customerId = customer.id)
        val valueAsString: String = this.objectMapper.writeValueAsString(creditDTO)
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("BAD REQUEST! Date Invalid"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class me.dio.credit.application.system.exceptions.DateInvalidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't save a credit with 'dayFirstInstallment' date past and return status 400`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val creditDTO: CreditDTO = this.buildCreditDto(dayFirstInstallment = LocalDate.now().minusDays(1), customerId = customer.id)
        val valueAsString: String = this.objectMapper.writeValueAsString(creditDTO)
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't save a credit with field 'numberOfInstallment' bigger then 48 and return status 400`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val creditDTO: CreditDTO = this.buildCreditDto(numberOfInstallment = 49, customerId = customer.id)
        val valueAsString: String = this.objectMapper.writeValueAsString(creditDTO)
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should dont't save a credit with field 'numberOfInstallment' less than 1 and return status 400`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val creditDTO: CreditDTO = this.buildCreditDto(numberOfInstallment = 0, customerId = customer.id)
        val valueAsString: String = this.objectMapper.writeValueAsString(creditDTO)
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find all credits by id customer and return status 200`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val credit1: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity());
        val credit2: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity());
        val credit3: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity());
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customer=${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't find all credits by invalid id customer and return status 404`(){
        //given
        val idFake: Long = Random().nextLong();
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val credit1: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity())
        val credit2: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity())
        val credit3: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity())
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customer=$idFake")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Not Found Resource"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class me.dio.credit.application.system.exceptions.NotFoundException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't find a credit if not exists credit and return status 200 and no content`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customer=${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("*").isEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find a credit by creditCode and id customer and return status 200`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val credit1: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity())
        val credit2: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity())
        val credit3: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity())
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit1.creditCode}?customer=${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(credit1.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dayFirstInstallment").value(credit1.dayFirstInstallment.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(credit1.numberOfInstallment))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(credit1.status.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstNamecustomer").value(customer.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't find a credit by creditCode belonging to another id customer and return status 400`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val customer2: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO(email = "teste@testando", cpf = "32649975504").toEntity())
        val creditCustomer1: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity())
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${creditCustomer1.creditCode}?customer=${customer2.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("You can't access a loan request that doesn't belong to you"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class java.lang.IllegalAccessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should dont' find a credit by invalid creditCode but valid id customer and return status 404`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val credit: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity())
        val invalidCreditCode: UUID = UUID.randomUUID();
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidCreditCode?customer=${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Not Found Resource"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class me.dio.credit.application.system.exceptions.NotFoundException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't find a credit by valid creditCode but invalid id customer and return status 404`(){
        //given
        val customer: Customer = this.customerRepository.save(CustomerControllerTest().buildCustomerDTO().toEntity())
        val credit: Credit = this.creditRepository.save(this.buildCreditDto(customerId = customer.id).toEntity())
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${credit.creditCode}?customer=${Random().nextLong()}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Not Found Resource"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class me.dio.credit.application.system.exceptions.NotFoundException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }


    private fun buildCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(12500.0),
        dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2),
        numberOfInstallment: Int = 48,
        customerId: Long? = 1L
    ): CreditDTO {
        return CreditDTO(
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
             numberOfInstallment = numberOfInstallment,
            customerId = customerId!!
        )
    }

}