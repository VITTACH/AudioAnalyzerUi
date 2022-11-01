package audio.visualization;

import audio.io.AudioDevice;
import audio.io.Decoder;

import java.awt.*;

/**
 * Takes a plot and a decoder and plays back the com.badlogic.audio
 * form the decoder as well as setting the marker in the
 * plot accordingly.
 *
 * @author mzechner
 */
public class PlaybackVisualizer {
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
    public PlaybackVisualizer(Plot plot, int samplesPerPixel, Decoder decoder) throws Exception {
        AudioDevice device = new AudioDevice();
        float[] samples = new float[1024];

        long startTime = 0;
        while (decoder.readSamples(samples) > 0) {
            device.writeSamples(samples);
            if (startTime == 0)
                startTime = System.nanoTime();
            float elapsedTime = (System.nanoTime() - startTime) / 1000000000.0f;
            int position = (int) (elapsedTime * (44100 / samplesPerPixel));
            plot.setMarker(position, Color.white);
            Thread.sleep(20); // this is needed or else swing has no chance repainting the plot!
        }
    }
}
