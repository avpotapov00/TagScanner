package info.potapov.tag

import info.potapov.tag.client.rest.RestClient
import info.potapov.tag.client.rest.impl.DelayedRestClient
import info.potapov.tag.client.rest.impl.KtorRestClient
import info.potapov.tag.client.vk.VkClient
import info.potapov.tag.client.vk.impl.VkClientImpl
import info.potapov.tag.model.ErrorCodeResponse
import info.potapov.tag.model.ExceptionResponse
import info.potapov.tag.model.VkResponse
import info.potapov.tag.model.VkSuccessResult
import info.potapov.tag.service.VkTagCounterService
import info.potapov.tag.service.impl.VkTagCounterServiceImpl
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.fileProperties

private val applicationModule = module {
    single { KtorRestClient(getProperty("ktor.timeout").toLong()) }
    single<RestClient> { DelayedRestClient(get<KtorRestClient>(), 300L) }
    single<VkClient> {
        VkClientImpl(
            get(),
            getProperty("vk.api.base_url"),
            getProperty("vk.api.version"),
            getProperty("vk.api.key")
        )
    }
    single<VkTagCounterService> { VkTagCounterServiceImpl(get(), getProperty("vk.time.zone.offset")) }
}

fun main() = runBlocking {
    val application = startKoin {
        fileProperties("/public.properties")
        fileProperties("/private.properties")
        modules(applicationModule)
    }
    val service = application.koin.get<VkTagCounterService>()
    displayResponses(service.findFrequency(16, "Hello"))
}

private fun displayResponses(responses: List<VkResponse>) {
    responses.forEach { response ->
        when (response) {
            is ExceptionResponse -> println("Exception: $response")
            is ErrorCodeResponse -> println("Status code: ${response.status}")
            is VkSuccessResult -> println("OK: ${response.response.totalCount}")
        }
    }
}

