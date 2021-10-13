package info.potapov.tag.service

import info.potapov.tag.model.VkResponse

interface VkTagCounterService {

    suspend fun findFrequency(n: Int, tag: String): List<VkResponse>

}