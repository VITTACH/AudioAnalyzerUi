package audio.samples.part2

import audio.io.AudioDevice
import audio.io.wave.WaveDecoder
import audio.utils.toFloatArray
import java.io.FileInputStream
import kotlin.math.ln
import kotlin.math.pow

/**
 * @author VITTACH
 */
object WaveOutput {
    private const val SPEED = 1.0
    private const val PITCH = 1.8
    private const val BYTES_IN_FLOAT = java.lang.Float.SIZE / java.lang.Byte.SIZE

    @JvmStatic
    fun main(argv: Array<String>) {
        val decoder = WaveDecoder()
        decoder.setStream(FileInputStream("samples/sample.wav"))
        val riffWaveHeader = decoder.riffWaveHeader
        val sampleRate = riffWaveHeader.sampleRate
        val channelsCount = riffWaveHeader.channels

        val duration = riffWaveHeader.dataSize / riffWaveHeader.bytesPerSecond
        println(duration)

        val device = AudioDevice(sampleRate)
        do {
            val samplesCount = (2048 * SPEED).roundToPowerTwo()
            val samples = decoder.readNextNSamples(samplesCount)?.also {
                device.writeSamples(toFloatArray(channelsCount, it, PITCH))
            }
        } while (samples != null)
    }

    private fun Double.roundToPowerTwo() = 2.0.pow((ln(this) / ln(2.0))).toInt()
}