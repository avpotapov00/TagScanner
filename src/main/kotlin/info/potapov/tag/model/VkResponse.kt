package info.potapov.tag.model

import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface VkResponse

data class ExceptionResponse(
    val exception: Exception
) : VkResponse

data class ErrorCodeResponse(
    val status: HttpStatusCode
) : VkResponse

@Serializable
data class VkSuccessResult(
    val response: Response
) : VkResponse

@Serializable
data class Response(
    @SerialName("total_count")
    val totalCount: Int
)

