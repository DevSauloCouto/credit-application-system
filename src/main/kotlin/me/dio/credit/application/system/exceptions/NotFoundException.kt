package me.dio.credit.application.system.exceptions

data class NotFoundException(override val message: String) : RuntimeException(message)