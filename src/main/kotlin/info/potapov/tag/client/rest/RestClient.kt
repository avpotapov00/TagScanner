package info.potapov.tag.client.rest

import info.potapov.tag.model.RestResponse
import io.ktor.client.request.*

interface RestClient {

    suspend fun request(config: HttpRequestBuilder.() -> Unit): RestResponse

}