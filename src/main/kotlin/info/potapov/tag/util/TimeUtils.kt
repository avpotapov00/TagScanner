package info.potapov.tag.util

import java.time.LocalDateTime
import java.time.ZoneOffset

data class TimeInterval(
    val start: LocalDateTime,
    val end: LocalDateTime
)

fun intervalsBack(n: Int): List<TimeInterval> {
    check(n > 0)
    val currentDateTime = LocalDateTime.now()
    return (1..n).map { currentDateTime.minusHours((n - it).toLong()) }
        .windowed(2)
        .map { (start, end) -> TimeInterval(start, end) }
}

private val zone: ZoneOffset = ZoneOffset.of("+03:00")
fun LocalDateTime.toUnixTime(): Long {
    return this.toEpochSecond(zone)
}
