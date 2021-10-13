package info.potapov.tag.client.vk.impl

import info.potapov.tag.client.rest.RestClient
import info.potapov.tag.model.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.MockKMatcherScope
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class VkClientImplTest {

    private val baseUrlStub = "http://localhost/api"
    private val apiVersionStub = "5.131"
    private val accessTokenStub = "abc"
    private val fromDateStub = ZonedDateTime.of(
        LocalDateTime.of(2020, 1, 1, 1, 1), ZoneOffset.of("+03:00")
    )
    private val toDateStub = ZonedDateTime.of(
        LocalDateTime.of(2021, 2, 2, 2, 2), ZoneOffset.of("+03:00")
    )
    private val queryStub = "Tag"


    @Test
    fun shouldReturnValidResponses() = runBlocking {
        val correctResponseStub = """{ "response": { "total_count": 10 } }"""

        val expectedUrl =
            makeExpectedUrl(baseUrlStub, fromDateStub, toDateStub, apiVersionStub, accessTokenStub, queryStub)
        val restClient = makeRestClientMock(expectedUrl, RestResponse(HttpStatusCode.OK, correctResponseStub))

        val vkClient = VkClientImpl(restClient, "http://localhost/api", "5.131", "abc")
        val result = vkClient.contentCount(queryStub, fromDateStub, toDateStub)

        val expected = VkSuccessResult(Response(10))
        assertEquals(expected, result)
    }

    @Test
    fun shouldReturnExceptionResponse(): Unit = runBlocking {
        val invalidJsonStub = "[[ERROR]]"

        val expectedUrl =
            makeExpectedUrl(baseUrlStub, fromDateStub, toDateStub, apiVersionStub, accessTokenStub, queryStub)
        val restClient = makeRestClientMock(expectedUrl, RestResponse(HttpStatusCode.OK, invalidJsonStub))

        val vkClient = VkClientImpl(restClient, "http://localhost/api", "5.131", "abc")
        val result = vkClient.contentCount(queryStub, fromDateStub, toDateStub)

        assertTrue(result is ExceptionResponse)
    }

    @Test
    fun shouldReturnErrorCodeResponse() = runBlocking {
        val expectedUrl =
            makeExpectedUrl(baseUrlStub, fromDateStub, toDateStub, apiVersionStub, accessTokenStub, queryStub)
        val restClient = makeRestClientMock(expectedUrl, RestResponse(HttpStatusCode.BadRequest, "_"))

        val vkClient = VkClientImpl(restClient, "http://localhost/api", "5.131", "abc")
        val result = vkClient.contentCount(queryStub, fromDateStub, toDateStub)

        assertTrue(result is ErrorCodeResponse)
        assertEquals(HttpStatusCode.BadRequest, result.status)
    }

    private fun makeRestClientMock(
        expectedUrl: String,
        response: RestResponse
    ) = mockk<RestClient> {
        coEvery {
            request(urlMatcher(expectedUrl))
        } returns response
    }

    private fun makeExpectedUrl(
        baseUrl: String,
        from: ZonedDateTime,
        to: ZonedDateTime,
        apiVersion: String,
        accessToken: String,
        query: String
    ) =
        "${baseUrl}?start_time=${from.toEpochSecond()}&end_time=${to.toEpochSecond()}&v=$apiVersion&access_token=$accessToken&q=$query&count=0"

    private fun MockKMatcherScope.urlMatcher(
        expectedUrl: String
    ): HttpRequestBuilder.() -> Unit = match { block ->
        val builder = HttpRequestBuilder()
        block(builder)
        val data = builder.build()
        expectedUrl == data.url.toString()
    }


}