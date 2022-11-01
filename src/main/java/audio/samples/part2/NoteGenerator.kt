package audio.samples.part2

import audio.analysis.FFT
import audio.domain.WavFile
import audio.domain.WavFile.Sample.buildManyFromBytes
import audio.domain.WavFile.SamplingInfo.Instances
import audio.io.AudioDevice
import audio.visualization.Plot
import java.awt.Color
import java.util.*
import kotlin.experimental.or
import kotlin.math.*

/**
 * A simple generator that outputs a sinewave at some
 * frequency (here 440Hz = Note A) in mono to an [AudioDevice].
 *
 * @author VITTACH
 */

private const val rate = 44100
private const val duration = 250 // ms
private const val arraySize = rate * duration / 1000

private val frequencies = listOf(294f, 349f, 440f, 523f) // Hz
private val waves = frequencies.map { FloatArray(arraySize) }.toMutableList()
private val angles = frequencies.map { 0f }.toMutableList()

object NoteGenerator {

    @JvmStatic
    fun main(argv: Array<String>) {
        val soundArray = FloatArray(arraySize)

        create(soundArray)
        play(soundArray)
        saveToFile(soundArray)
        drawPlots(soundArray)
    }
}

private fun create(soundArray: FloatArray) {
    for (i in 0 until arraySize) {
        for (j in frequencies.indices) {
            waves[j][i] = amplitude(-0.015, frequencies[j], i, arraySize) * sin(angles[j].toDouble()).toFloat()
            angles[j] += getAngleIncrement(frequencies[j], rate)
        }

        soundArray[i] =
            summarySignal(waves.map { it[i] })
    }
}

private fun drawPlots(soundArray: FloatArray) {

    val bufferLength = 2.0.pow((ln(arraySize.toDouble()) / ln(2.0)).toInt().toDouble()).toInt()
    val buffer = FloatArray(bufferLength)
    for (i in 0 until bufferLength) {
        var value = 0f
        if (i < arraySize) {
            value = soundArray[i]
        }
        buffer[i] = value
    }

    val fft1 = FFT(
        bufferLength,
        rate.toFloat()
    )
    fft1.forward(buffer)
    val notesSpectrum = Plot("Notes Spectrum", 512, 512)
    notesSpectrum.plot(fft1.spectrum, 1f, Color.red)

    val notesPlot = Plot("Notes Plot", 512, 512)
    notesPlot.plot(soundArray, 1f, -1f, false, Color.red)

    waves.forEach { signal: FloatArray ->
        notesPlot.plot(signal, 1f, 0f, true, Color((0..255).random(), (0..255).random(), (0..255).random()))
    }
}

private fun summarySignal(signals: List<Float>): Float {
    val max = Collections.max(signals)
    val min = Collections.min(signals)
    var result = signals.stream().reduce { a: Float, b: Float -> a + b }.get()
    if (result > max) {
        result = max
    } else if (result < min) {
        result = min
    }
    return result
}

private fun amplitude(compressor: Double, frequency: Float, position: Int, length: Int) =
    exp(compressor * frequency * position / length).toFloat()

private fun getAngleIncrement(frequency: Float, rate: Int) = (2 * Math.PI).toFloat() * frequency / rate

private fun saveToFile(floatBuffer: FloatArray) {
    val bytes = fillBuffer(floatBuffer)
    val wavFile = WavFile(
        "samples/test1.wav",
        Instances.Default,
        buildManyFromBytes(Instances.Default, bytes)
    )
    wavFile.writeToFilePath()
}

private fun play(soundArray: FloatArray) {
    val device = AudioDevice()
    val soundBuffer = FloatArray(1024)
    for (i in soundArray.indices step 1024) {
        val length = min(soundArray.size - i, soundBuffer.size)
        System.arraycopy(soundArray, i, soundBuffer, 0, length)
        device.writeSamples(soundBuffer)
    }
}

private fun fillBuffer(soundArray: FloatArray): ByteArray {
    val ret = ByteArray(soundArray.size * 2)
    var j = 0
    for (i in soundArray.indices) {
        val value = (soundArray[i] * Short.MAX_VALUE).toShort()
        ret[j] = (value or 0xff).toByte()
        ret[j + 1] = (value.toInt() shr 8).toByte()
        j += 2
    }
    return ret
}