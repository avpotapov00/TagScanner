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

    @Test
    fun shouldResponse() {
        val client = KtorRestClient(1000)
        withMockServer { server ->
            whenHttp(server)
                .match(method(Method.GET), startsWithUri("/check"))
                .then(stringContent("ok"))

            val response = client.request { url("http://localhost:$port/ping") }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("ok", response.content)
        }
    }

    @Test
    fun shouldFailWithTimeout() {
        val client = KtorRestClient(100)
        assertThrows<HttpRequestTimeoutException> {
            runBlocking {
                client.request { url("http://localhost:$port/ping") }
            }
        }
    }

    private fun withMockServer(block: suspend (StubServer) -> Unit) = runBlocking {
        val port = 8888
        val server = StubServer(port)
        server.start()
        try {
            block.invoke(server)
        } catch (e: Exception) {
            server.stop()
        }
    }
}