package audio.samples.part5;

import audio.analysis.FFT;
import audio.visualization.Plot;

import java.awt.*;

/**
 * Simple example that generates a 1024 samples sine wave at 440Hz
 * and plots the resulting spectrum.
 *
 * @author mzechner
 */
public class FourierTransformPlot {
    public static void main(String[] argv) {
        final float frequency1 = 440; // 440Hz
        final float frequency2 = 1320; // 880Hz
        int sampleRate = 44100;
        int len = 1024;

        float signal1[] = new float[len];
        float signal2[] = new float[len];

        float angle1 = 0;
        float angle2 = 0;
        for (int i = 0; i < len; i++) {
            signal1[i] = 2f * (float) Math.sin(angle1 += getIncrement(frequency1, sampleRate));
            signal2[i] = 1f * (float) Math.sin(angle2 += getIncrement(frequency2, sampleRate));
        }

        FFT fft1 = new FFT(len, sampleRate);
        FFT fft2 = new FFT(len, sampleRate);
        fft1.forward(signal1);
        fft2.forward(signal2);
        Plot notesSpectrum = new Plot("Notes Spectrum", 512, 512);
        notesSpectrum.plot(fft1.getSpectrum(), 1, Color.red);
        notesSpectrum.plot(fft2.getSpectrum(), 1, Color.blue);

        Plot notesPlot = new Plot("Notes Plot", 512, 512);
        notesPlot.plot(signal1, 1, Color.red);
        notesPlot.plot(signal2, 1, Color.blue);

    }

    private static float getIncrement(float frequency, int sampleRate) {
        return (float) (2 * Math.PI) * frequency / sampleRate;
    }
}
