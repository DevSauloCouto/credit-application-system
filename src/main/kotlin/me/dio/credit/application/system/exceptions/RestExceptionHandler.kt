package me.dio.credit.application.system.exceptions

import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ExceptionDetails> {
        val errors: MutableMap<String, String?> = HashMap();
        e.bindingResult.allErrors.stream().forEach {
            err: ObjectError ->
            val fieldName: String = (err as FieldError).field
            val messageError: String? = err.defaultMessage
            errors[fieldName] = messageError;
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ExceptionDetails(
                title="Bad Request! Consult the documentation",
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = e.javaClass.toString(),
                details = errors
            )
        )

    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(e: DataAccessException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ExceptionDetails(
                title = "Not possible created two customers with fields 'email' and 'cpf' equals",
                timestamp = LocalDateTime.now(),
                status = HttpStatus.CONFLICT.value(),
                exception = e.javaClass.toString(),
                details = mutableMapOf(e.cause.toString() to e.message)
            )
        )

    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ExceptionDetails(
                title = "Not Found Resource",
                timestamp = LocalDateTime.now(),
                status = HttpStatus.NOT_FOUND.value(),
                exception = e.javaClass.toString(),
                details = mutableMapOf(e.cause.toString() to e.message)
            )
        )
    }

    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccess(e: IllegalAccessException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ExceptionDetails(
                title = "You can't access a loan request that doesn't belong to you",
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = e.javaClass.toString(),
                details = mutableMapOf(e.cause.toString() to e.message)
            )
        )
    }

    @ExceptionHandler(DateInvalidException::class)
    fun handleDateInvalid(e: DateInvalidException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ExceptionDetails(
                title = "BAD REQUEST! Date Invalid",
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = e.javaClass.toString(),
                details = mutableMapOf(e.cause.toString() to e.message)
            )
        )
    }

}