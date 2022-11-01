package audio.samples.part6;

import audio.analysis.SpectrumProvider;
import audio.io.MP3Decoder;
import audio.visualization.PlaybackVisualizer;
import audio.visualization.Plot;

import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Demonstrates the calculation of the spectral flux function
 * hopping fractions of the original 1024 sample window.
 *
 * @author mzechner
 */
public class HoppingSpectralFlux {
    public static final String FILE = "samples/sample.mp3";
    public static final int HOP_SIZE = 512;

    public static void main(String[] argv) throws Exception {
        MP3Decoder decoder = new MP3Decoder(new FileInputStream(FILE));
        SpectrumProvider spectrumProvider = new SpectrumProvider(decoder, 1024, HOP_SIZE, true);
        float[] spectrum = spectrumProvider.nextSpectrum();
        float[] lastSpectrum = new float[spectrum.length];
        ArrayList<Float> spectralFlux = new ArrayList<Float>();

        do {
            float flux = 0;
            for (int i = 0; i < spectrum.length; i++) {
                float value = (spectrum[i] - lastSpectrum[i]);
                flux += value < 0 ? 0 : value;
            }
            spectralFlux.add(flux);

            System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.length);
        }
        while ((spectrum = spectrumProvider.nextSpectrum()) != null);

        Plot plot = new Plot("Spectral Flux", 1024, 512);
        plot.plot(spectralFlux, 1, Color.red);

        new PlaybackVisualizer(plot, HOP_SIZE, new MP3Decoder(new FileInputStream(FILE)));
    }
}
