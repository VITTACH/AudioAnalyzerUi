package audio.samples.part5

import audio.analysis.FFT
import audio.visualization.Plot
import java.awt.Color
import kotlin.math.sin

/**
 * Simple example that generates a 1024 samples sine wave at 440Hz
 * and plots the resulting spectrum.
 *
 * @author VITTACH
 */
object FourierTransformPlot {
    @JvmStatic
    fun main(argv: Array<String>) {
        val frequency1 = 440f // 440Hz
        val frequency2 = 1320f // 880Hz
        val sampleRate = 44100
        val len = 1024
        val signal1 = FloatArray(len)
        val signal2 = FloatArray(len)
        var angle1 = 0f
        var angle2 = 0f

        for (i in 0 until len) {
            signal1[i] = 2f * sin(getIncrement(frequency1, sampleRate).let { angle1 += it; angle1 }.toDouble()).toFloat()
            signal2[i] = 1f * sin(getIncrement(frequency2, sampleRate).let { angle2 += it; angle2 }.toDouble()).toFloat()
        }

        val fft1 = FFT(len, sampleRate.toFloat())
        val fft2 = FFT(len, sampleRate.toFloat())

        fft1.forward(signal1)
        fft2.forward(signal2)

        val notesSpectrum = Plot("Notes Spectrum", 512, 512)
        notesSpectrum.plot(fft1.spectrum, 1f, Color.red)
        notesSpectrum.plot(fft2.spectrum, 1f, Color.blue)

        val notesPlot = Plot("Notes Plot", 512, 512)
        notesPlot.plot(signal1, 1f, Color.red)
        notesPlot.plot(signal2, 1f, Color.blue)
    }

    private fun getIncrement(frequency: Float, sampleRate: Int): Float {
        return (2 * Math.PI).toFloat() * frequency / sampleRate
    }
}