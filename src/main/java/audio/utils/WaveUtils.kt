package audio.utils


fun toFloatArray(channelsCount: Int, array: ByteArray, pitch: Double = 1.0): FloatArray {
    val result = FloatArray(1024)
    for (i in result.indices) {
        result[i] = 0f
    }
    var j = 1.0
    var sourceIndex = 1
    for (i in result.indices) {
        try {
            var sample = 0f
            for (channel in 0 until channelsCount) {
                val shortValue = array[sourceIndex + channel * channelsCount].readShortLittleEndian()
                sample += (shortValue * 1.0f / Short.MAX_VALUE)
            }

            sample /= channelsCount
            result[i] = sample
            j += 2 * channelsCount * pitch
            if (j.toInt() % 2 != 0) {
                sourceIndex = j.toInt()
            }
        } catch (e: Exception) {

        }
    }
    return result
}

fun Byte.readShortLittleEndian() = (this.toInt() shl 8).toShort()
