package com.zamolodchikov.github.utils

class UnauthorizedException(override val message: String = "Unauthorized") : Exception()

class ForbiddenException(override val message: String = "Forbidden") : Exception()

class NotFoundException(override val message: String = "Not found") : Exception()

class InternalException(override val message: String = "Internal server error") : Exception()

class ConnectionException(override val message: String = "No connection") : Exception()