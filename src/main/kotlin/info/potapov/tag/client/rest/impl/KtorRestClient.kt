package info.potapov.tag.client.rest.impl

import info.potapov.tag.client.rest.RestClient
import info.potapov.tag.model.RestResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class KtorRestClient(
    private val timeoutTime: Long
) : RestClient {

    private val ktorClient = HttpClient {
        install(HttpTimeout)
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })
        }
    }

    init {
        ktorClient.attributes
    }

    override suspend fun request(config: HttpRequestBuilder.() -> Unit): RestResponse {
        val response = ktorClient.request<HttpResponse> {
            config.invoke(this)
            timeout { requestTimeoutMillis = timeoutTime }
        }
        val content = response.receive<String>()
        return RestResponse(response.status, content)
    }

}
