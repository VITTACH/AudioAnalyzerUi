package audio.samples.part6

import audio.analysis.SpectrumProvider
import audio.io.MP3Decoder
import audio.visualization.PlaybackVisualizer
import audio.visualization.Plot
import java.awt.Color
import java.io.FileInputStream
import java.util.*

/**
 * Demonstrates the calculation of the spectral flux function
 * hopping fractions of the original 1024 sample window.
 *
 * @author VITTACH
 */
object HoppingSpectralFlux {
    private const val FILE = "samples/sample.mp3"
    private const val HOP_SIZE = 512

    @JvmStatic
    fun main(argv: Array<String>) {
        val decoder = MP3Decoder(FileInputStream(FILE))
        val spectrumProvider = SpectrumProvider(decoder, 1024, HOP_SIZE, true)
        var spectrum = spectrumProvider.nextSpectrum()
        val lastSpectrum = FloatArray(spectrum!!.size)
        val spectralFlux = ArrayList<Float>()

        do {
            var flux = 0f
            for (i in spectrum!!.indices) {
                val value = spectrum[i] - lastSpectrum[i]
                flux += if (value < 0) 0f else value
            }
            spectralFlux.add(flux)
            System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.size)
        } while (spectrumProvider.nextSpectrum()?.also { spectrum = it } != null)

        val plot = Plot("Spectral Flux", 1024, 512)
        plot.plot(spectralFlux, 1f, Color.red)
        PlaybackVisualizer(plot, HOP_SIZE, MP3Decoder(FileInputStream(FILE)))
    }
}