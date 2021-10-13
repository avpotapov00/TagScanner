package info.potapov.tag.client.vk.impl

import info.potapov.tag.client.rest.RestClient
import info.potapov.tag.client.vk.VkClient
import info.potapov.tag.model.ErrorCodeResponse
import info.potapov.tag.model.ExceptionResponse
import info.potapov.tag.model.VkResponse
import info.potapov.tag.model.VkSuccessResult
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

class VkClientImpl(
    private val restClient: RestClient,
    private val baseUrl: String,
    private val apiVersion: String,
    private val accessToken: String
) : VkClient {

    private val jsonDecoder = Json { ignoreUnknownKeys = true }

    override suspend fun contentCount(query: String, from: ZonedDateTime, to: ZonedDateTime): VkResponse {
        try {
            val response = restClient.request {
                url(baseUrl)
                parameter("start_time", from.toEpochSecond())
                parameter("end_time", to.toEpochSecond())
                parameter("v", apiVersion)
                parameter("access_token", accessToken)
                parameter("q", query)
                parameter("count", 0)
            }
            if (response.status != HttpStatusCode.OK) {
                return ErrorCodeResponse(response.status)
            }
            return try {
                jsonDecoder.decodeFromString<VkSuccessResult>(response.content)
            } catch (e: Exception) {
                println("Failed: ${response.content}")
                ExceptionResponse(e)
            }
        } catch (e: Exception) {
            return ExceptionResponse(e)
        }
    }

}