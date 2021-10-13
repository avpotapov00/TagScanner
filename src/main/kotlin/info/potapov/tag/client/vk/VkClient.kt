package info.potapov.tag.client.vk

import info.potapov.tag.model.VkResponse
import java.time.ZonedDateTime

interface VkClient {

    suspend fun contentCount(query: String, from: ZonedDateTime, to: ZonedDateTime): VkResponse

}