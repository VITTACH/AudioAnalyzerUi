package audio.samples.part3

import audio.io.wave.WaveDecoder
import audio.utils.toFloatArray
import audio.visualization.Plot
import java.awt.Color
import java.io.FileInputStream
import java.util.*

/**
 * A simple example that shows how to use the [Plot] class.
 * Note that the plots will not be entirely synchronous to the
 * music playback. This is just an example, you should not do
 * real-time plotting with the Plot class it is just not made for
 * this.
 *
 * @author mzechner
 */
object PlotExample {

    @JvmStatic
    fun main(argv: Array<String>) {
        val decoder = WaveDecoder()
        decoder.setStream(FileInputStream("samples/sample.wav"))
        decoder.riffWaveHeader
        val allSamples = ArrayList<Float>()

        do {
            val samples = decoder.readNextNSamples(2048)?.also {
                allSamples.addAll(toFloatArray(2, it).toList())
            }
        } while (samples != null)

        val plot = Plot("Wave Plot", 512, 512)
        plot.plot(allSamples, 44100 / 1000f, Color.red)
    }
}