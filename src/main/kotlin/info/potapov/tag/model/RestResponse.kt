package info.potapov.tag.model

import io.ktor.http.*

data class RestResponse(
    val status: HttpStatusCode,
    val content: String
)