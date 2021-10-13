package info.potapov.tag.service.impl

import info.potapov.tag.client.vk.VkClient
import info.potapov.tag.model.VkResponse
import info.potapov.tag.service.VkTagCounterService
import info.potapov.tag.util.intervalsBack
import java.time.ZoneOffset

class VkTagCounterServiceImpl(
    private val vkClient: VkClient,
    offset: String
) : VkTagCounterService {

    private val zoneOffset = ZoneOffset.of(offset)

    override suspend fun findFrequency(n: Int, tag: String): List<VkResponse> {
        return intervalsBack(n).map {
            vkClient.contentCount(tag, it.start.atZone(zoneOffset), it.end.atZone(zoneOffset))
        }
    }

}