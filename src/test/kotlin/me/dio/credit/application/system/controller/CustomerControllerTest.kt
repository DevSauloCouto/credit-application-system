package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.DTO.customer.CustomerDTO
import me.dio.credit.application.system.DTO.customer.CustomerUpdateDTO
import me.dio.credit.application.system.entities.Customer
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
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerControllerTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository;

    @Autowired
    private lateinit var mockMvc: MockMvc;

    @Autowired
    private lateinit var objectMapper: ObjectMapper;

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach
    fun setup(){
        this.customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown(){
        this.customerRepository.deleteAll()
    }

    @Test
    fun `should created a customer and return status 201`(){
        //given
        val customerDto: CustomerDTO = this.buildCustomerDTO();
        val customerDtoString: String = this.objectMapper.writeValueAsString(customerDto)
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerDtoString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Saulo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Couto"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("slcouto@teste"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("08316540584"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(521.70))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("C"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("325612000"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(7L))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't save a customer with same fields cpf and email and return 409 status`(){
        //given
        this.customerRepository.save(buildCustomerDTO().toEntity())
        val customerDto: CustomerDTO = this.buildCustomerDTO();
        val valueAsString: String = this.objectMapper.writeValueAsString(customerDto);
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Not possible created two customers with fields 'email' and 'cpf' equals"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.dao.DataIntegrityViolationException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't save a customer with fields empty`(){
        //given
        val customerDto: CustomerDTO = this.buildCustomerDTO(firstName = "", password = "");
        val valueAsString: String = this.objectMapper.writeValueAsString(customerDto);
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
    }

    @Test
    fun `should find a customer by id and return status 200`(){
        //given
        val customer: Customer = this.customerRepository.save(buildCustomerDTO().toEntity())
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Saulo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Couto"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("slcouto@teste"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("08316540584"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(521.70))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("C"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("325612000"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should don't find a customer by invalid id and return status 404`(){
        //given
        val invalidId: Long = 1L;
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidId")
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
    fun `should delete a customer by id and return status 200`(){
        //given
        val customer: Customer = this.customerRepository.save(buildCustomerDTO().toEntity())
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("${customer.firstName} ${customer.lastName} deletado com sucesso!"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't anywhere customer by invalid id and return status 404`(){
        //given
        val invalidId: Long = 1L;
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/$invalidId")
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
    fun `should update a customer by ID and return status 201`(){
        //given
        val customer: Customer = this.customerRepository.save(buildCustomerDTO().toEntity())
        val updateCustomerDto: CustomerUpdateDTO = this.buildCustomerUpdateDto()
        val valueAsString: String = this.objectMapper.writeValueAsString(updateCustomerDto)
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customer=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Saulo"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Rocha Couto"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("slcouto@teste"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("08316540584"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(2800))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua A"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("458986555"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should don't update a customer by invalid ID and return status 404`(){
        //given
        val invalidId: Long = Random().nextLong();
        val updateCustomerDto: CustomerUpdateDTO = this.buildCustomerUpdateDto()
        val valueAsSring: String = this.objectMapper.writeValueAsString(updateCustomerDto)
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customer=$invalidId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsSring)
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
    fun `should don't update with some field empty and return status 400`(){
        //given
        val customer: Customer = this.customerRepository.save(buildCustomerDTO().toEntity())
        val customerUpdateDto: CustomerUpdateDTO = this.buildCustomerUpdateDto(firstName = "", street = "")
        val valueAsString: String = this.objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        this.mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customer=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
    }

    fun buildCustomerDTO(
        firstName: String = "Saulo",
        lastName: String = "Couto",
        email: String = "slcouto@teste",
        cpf: String = "08316540584",
        password: String = "!@#123",
        income: BigDecimal = BigDecimal.valueOf(521.70),
        street: String = "C",
        zipCode: String = "325612000"
    ): CustomerDTO {
        return CustomerDTO(
            firstName = firstName,
            lastName = lastName,
            email = email,
            cpf = cpf,
            password = password,
            income = income,
            street = street,
            zipCode = zipCode
        )
    }

    private fun buildCustomerUpdateDto(
        firstName: String = "Saulo",
        lastName: String = "Rocha Couto",
        income: BigDecimal = BigDecimal.valueOf(2800),
        zipCode: String = "458986555",
        street: String = "Rua A"
    ): CustomerUpdateDTO {
        return CustomerUpdateDTO(
            firstName = firstName,
            lastName = lastName,
            income = income,
            zipCode = zipCode,
            street = street
        )
    }

}