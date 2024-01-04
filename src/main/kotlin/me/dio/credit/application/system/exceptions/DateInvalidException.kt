package me.dio.credit.application.system.exceptions

data class DateInvalidException(override val message: String) : RuntimeException(message);