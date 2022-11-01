package audio.io.wave;

import java.util.Locale;
import java.util.Objects;

/**
 * Header of a RIFF WAVE audio file containing the format of the sound information
 *
 * @author Rémi Georgiou
 */
public class RIFFWaveHeader {

    static final int RIFF_CHUNK_DESCRIPTOR = 0x46464952;          // "RIFF" in ASCII little endian
    static final int WAVE_CHUNK_DESCRIPTOR = 0x45564157;          // "WAVE" in ASCII little endian
    static final int FORMAT_CHUNK_DESCRIPTOR = 0x20746d66;        // "fmt " in ASCII little endian
    static final int DATA_CHUNK_DESCRIPTOR = 0x61746164;          // "data" in ASCII little endian
    static final int PCM_SUB_CHUNK1_SIZE = 16;

    private final AudioCodingFormat audioCodingFormat;
    private final int channels;
    private final int sampleRate;
    private final int bitsPerSample;
    private final int dataSize;

    private volatile int hashCode;

    public RIFFWaveHeader(AudioCodingFormat audioCodingFormat, int channels, int sampleRate, int bitsPerSample,
                          int dataSize) {
        this.audioCodingFormat = audioCodingFormat;
        this.channels = channels;
        this.sampleRate = sampleRate;
        this.bitsPerSample = bitsPerSample;
        this.dataSize = dataSize;
    }

    /**
     * Returns the audio coding format.
     *
     * @return audio coding format
     */
    public AudioCodingFormat getAudioCodingFormat() {
        return audioCodingFormat;
    }

    /**
     * Returns the number of channels.
     *
     * @return number of channels
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Returns the sample rate.
     *
     * @return sample rate
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Returns the number of bits per sample.
     *
     * @return quantisation bits per sample
     */
    public int getBitsPerSample() {
        return bitsPerSample;
    }

    /**
     * Returns the number of bytes (for all channels) to represent one sample of data.
     * This is sometimes called the block alignment.
     * Definition: bits per sample * channels / 8
     *
     * @return bytes per sample
     */
    public int getBytesPerSample() {
        return bitsPerSample * channels / 8;
    }

    /**
     * Returns the number of bytes per second which is the speed of the data stream in bytes per second.
     * Definition: sample rate * bits per sample * channels / 8
     *
     * @return bytes per second
     */
    public int getBytesPerSecond() {
        return sampleRate * bitsPerSample * channels / 8;
    }

    /**
     * Returns the number of bytes of PCM data that is included in the data section.
     *
     * @return the actual PCM audio data size in bytes
     */
    public int getDataSize() {
        return dataSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RIFFWaveHeader)) {
            return false;
        }

        RIFFWaveHeader riffWaveHeader = (RIFFWaveHeader) o;
        return channels == riffWaveHeader.channels
                && sampleRate == riffWaveHeader.sampleRate
                && bitsPerSample == riffWaveHeader.bitsPerSample
                && dataSize == riffWaveHeader.dataSize
                && audioCodingFormat == riffWaveHeader.audioCodingFormat;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Objects.hash(audioCodingFormat, channels, sampleRate, bitsPerSample, dataSize);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "RIFF WAVE header:  audio coding format=%s  channels=%d  sample rate=%d Hz  bits per sample=%d"
                        + "  block alignment=%d bytes  data size=%d bytes",
                AudioCodingFormat.LINEAR_PCM.name(), channels, sampleRate, bitsPerSample,
                bitsPerSample * channels / 8, dataSize);
    }
}