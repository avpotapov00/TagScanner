package info.potapov.tag.client.rest.impl

import info.potapov.tag.client.rest.RestClient
import info.potapov.tag.model.RestResponse
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max

class DelayedRestClient(
    private val client: RestClient,
    private val delayTimeMillis: Long
) : RestClient {

    override suspend fun request(config: HttpRequestBuilder.() -> Unit): RestResponse {
        awaitOtherRequests()
        return client.request(config)
    }

    private suspend fun awaitOtherRequests() {
        while (true) {
            val currentTime = System.currentTimeMillis()
            val lastStartTime = counter.get()
            val invokeTime = max(currentTime, lastStartTime + delayTimeMillis)
            if (counter.compareAndSet(lastStartTime, invokeTime)) {
                val delta = invokeTime - currentTime
                delay(delta)
                break
            }
        }
    }

    private val counter = AtomicLong(0)
}