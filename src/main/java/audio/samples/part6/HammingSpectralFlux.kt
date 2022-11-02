package audio.samples.part6

import audio.analysis.FFT
import audio.io.MP3Decoder
import audio.visualization.PlaybackVisualizer
import audio.visualization.Plot
import java.awt.Color
import java.io.FileInputStream
import java.util.*

object HammingSpectralFlux {
    private const val FILE = "samples/sample.mp3"

    @JvmStatic
    fun main(argv: Array<String>) {
        val decoder = MP3Decoder(FileInputStream(FILE))
        val fft = FFT(1024, 44100f)
        fft.window(FFT.HAMMING)
        val samples = FloatArray(1024)
        val spectrum = FloatArray(1024 / 2 + 1)
        val lastSpectrum = FloatArray(1024 / 2 + 1)
        val spectralFlux: MutableList<Float> = ArrayList()

        while (decoder.readSamples(samples) > 0) {
            fft.forward(samples)
            System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.size)
            System.arraycopy(fft.spectrum, 0, spectrum, 0, spectrum.size)
            var flux = 0f
            for (i in spectrum.indices) {
                val value = spectrum[i] - lastSpectrum[i]
                flux += if (value < 0) 0f else value
            }
            spectralFlux.add(flux)
        }

        val plot = Plot("Spectral Flux", 1024, 512)
        plot.plot(spectralFlux, 1f, Color.red)
        PlaybackVisualizer(plot, 1024, MP3Decoder(FileInputStream(FILE)))
    }
}