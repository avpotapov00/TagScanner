package info.potapov.tag.service.impl

import info.potapov.tag.client.vk.VkClient
import info.potapov.tag.model.Response
import info.potapov.tag.model.VkSuccessResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class VkTagCounterServiceImplTest {

    @Test
    fun shouldProcessAll() = runBlocking {
        val tag = "TAG"

        var counter = 1
        val client = mockk<VkClient> {
            coEvery { contentCount(tag, any(), any()) } returns VkSuccessResult(Response(counter++))
        }

        val service = VkTagCounterServiceImpl(client, "+03:00")
        val frequency = service.findFrequency(4, tag)

        assertEquals(3, frequency.size)
        frequency.forEach {
            assertEquals(VkSuccessResult(Response(1)), it)
        }
    }

}