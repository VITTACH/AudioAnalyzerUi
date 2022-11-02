package audio.samples.part4;

import audio.io.AudioDevice;
import audio.io.MP3Decoder;

import java.io.FileInputStream;

/**
 * Simple example that shows how to decode an mp3 file.
 *
 * @author VITTACH
 */
public class MP3Output {
    public static void main(String[] argv) throws Exception {
        MP3Decoder decoder = new MP3Decoder(new FileInputStream("samples/sample.mp3"));
        AudioDevice device = new AudioDevice((int) decoder.getBaseFormat().getSampleRate());
        decoder.setSpeed(1.6);
        decoder.setPitch(1.4);
        float[] samples = new float[1024];

        while (decoder.readSamples(samples) > 0) {
            device.writeSamples(samples);
        }
    }
}
