package audio.samples.part7

import audio.analysis.SpectrumProvider
import audio.analysis.ThresholdFunction
import audio.io.MP3Decoder
import audio.visualization.PlaybackVisualizer
import audio.visualization.Plot
import java.awt.Color
import java.io.FileInputStream
import java.util.*

object MultiBandThreshold {
    private const val FILE = "samples/sample.mp3"
    private const val HOP_SIZE = 512
    private const val HISTORY_SIZE = 50
    private val multipliers = floatArrayOf(2f, 2f, 2f)
    private val bands = floatArrayOf(80f, 4000f, 4000f, 10000f, 10000f, 16000f)

    @JvmStatic
    fun main(argv: Array<String>) {
        val decoder =
            MP3Decoder(FileInputStream(FILE))
        val spectrumProvider = SpectrumProvider(
            decoder,
            1024,
            HOP_SIZE,
            true
        )
        var spectrum = spectrumProvider.nextSpectrum()
        val lastSpectrum = FloatArray(spectrum.size)
        val spectralFlux: MutableList<MutableList<Float>> = ArrayList()
        for (i in 0 until bands.size / 2) spectralFlux.add(ArrayList())
        do {
            var i = 0
            while (i < bands.size) {
                val startFreq = spectrumProvider.fft.freqToIndex(bands[i])
                val endFreq = spectrumProvider.fft.freqToIndex(bands[i + 1])
                var flux = 0f
                for (j in startFreq..endFreq) {
                    var value = spectrum[j] - lastSpectrum[j]
                    value = (value + Math.abs(value)) / 2
                    flux += value
                }
                spectralFlux[i / 2].add(flux)
                i += 2
            }
            System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.size)
        } while (spectrumProvider.nextSpectrum().also { spectrum = it } != null)
        val thresholds: MutableList<List<Float>> = ArrayList()
        for (i in 0 until bands.size / 2) {
            val threshold = ThresholdFunction(
                HISTORY_SIZE,
                multipliers[i]
            ).calculate(spectralFlux[i])
            thresholds.add(threshold)
        }
        val plot = Plot("Spectral Flux", 1024, 512)
        for (i in 0 until bands.size / 2) {
            plot.plot(spectralFlux[i], 1f, -0.6f * (bands.size / 2 - 2) + i, false, Color.red)
            plot.plot(thresholds[i], 1f, -0.6f * (bands.size / 2 - 2) + i, true, Color.green)
        }
        PlaybackVisualizer(
            plot,
            HOP_SIZE,
            MP3Decoder(FileInputStream(FILE))
        )
    }
}