package audio.visualization

import audio.io.AudioDevice
import audio.io.Decoder
import java.awt.Color

/**
 * Takes a plot and a decoder and plays back the com.badlogic.audio
 * form the decoder as well as setting the marker in the
 * plot accordingly.
 *
 * @author VITTACH
 */
class PlaybackVisualizer(plot: Plot, samplesPerPixel: Int, decoder: Decoder) {
    /**
     * Consturctor, plays back the com.badlogic.audio form the decoder and
     * sets the marker of the plot accordingly. This will return
     * when the playback is done.
     *
     * @param plot            The plot.
     * @param samplesPerPixel the numbe of samples per pixel.
     * @param decoder         The decoder.
     * @throws Exception
     */
    init {
        val device = AudioDevice()
        val samples = FloatArray(1024)
        var startTime: Long = 0
        while (decoder.readSamples(samples) > 0) {
            device.writeSamples(samples)
            if (startTime == 0L) startTime = System.nanoTime()
            val elapsedTime = (System.nanoTime() - startTime) / 1000000000.0f
            val position = (elapsedTime * (44100 / samplesPerPixel)).toInt()
            plot.setMarker(position, Color.white)
            Thread.sleep(20) // this is needed or else swing has no chance repainting the plot!
        }
    }
}