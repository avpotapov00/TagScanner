package info.potapov.tag.client.rest.impl

import com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import com.xebialabs.restito.semantics.Action.stringContent
import com.xebialabs.restito.semantics.Condition.method
import com.xebialabs.restito.semantics.Condition.startsWithUri
import com.xebialabs.restito.server.StubServer
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.glassfish.grizzly.http.Method
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class KtorRestClientTest {

    private val serverPort = 8888

    @Test
    fun shouldResponse(): Unit = runBlocking {
        val client = KtorRestClient(1000)
        withMockServer { server ->
            whenHttp(server)
                .match(method(Method.GET), startsWithUri("/check"))
                .then(stringContent("ok"))

            val response = client.request { url("http://localhost:$serverPort/check") }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("ok", response.content)
        }
    }

    @Test
    fun shouldFailWithTimeout() {
        val client = KtorRestClient(100)
        assertThrows<HttpRequestTimeoutException> {
            runBlocking {
                client.request { url("http://localhost:$serverPort/ping") }
            }
        }
    }

    private fun withMockServer(block: suspend (StubServer) -> Unit) = runBlocking {
        val server = StubServer(serverPort)
        server.start()
        try {
            block.invoke(server)
        } catch (e: Exception) {
            throw e
        } finally {
            server.stop()
        }
    }
}