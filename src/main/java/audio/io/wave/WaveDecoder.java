package audio.io.wave;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WaveDecoder implements AutoCloseable {

    private InputStream audioStream;
    private RIFFWaveHeader riffWaveHeader;

    /**
     * Initialise the decoder with the {@link InputStream} of a RIFF WAVE sound file.<br>
     *
     * @param audioStream the audio stream
     */
    public synchronized void setStream(InputStream audioStream) {
        this.audioStream = audioStream;
    }

    /**
     * Reads a sample block of size N samples or the minimum of available bytes of the audio stream.<br>
     * This method returns N samples <b>including all channels.</b><br>
     * Returns null when the end of the stream is reached.
     *
     * @param samples the number of samples to be read from the audio stream.
     * @return A sample block of size N samples.
     */
    public byte[] readNextNSamples(int samples) {
        try {
            if (audioStream == null || audioStream.available() == 0) {
                return null;
            }
            byte[] sampleBlock = new byte[Math.min(riffWaveHeader.getChannels() * samples, audioStream.available())];
            audioStream.read(sampleBlock);
            return sampleBlock;
        } catch (IOException e) {
        }
        return null;
    }

    public final int readSamples() throws Exception {
        return ((DataInputStream) audioStream).readUnsignedByte();
    }

    /**
     * The decoder attempts to extract the RIFF WAVE file header from the audio stream.
     *
     * @return The RIFF WAVE file header
     */
    public RIFFWaveHeader getRIFFWaveHeader() {
        if (riffWaveHeader == null) {
            riffWaveHeader = extractRIFFWaveHeader();
        }
        return riffWaveHeader;
    }

    /**
     * Close the underlying audio stream of the decoder instance.
     */
    @Override
    public void close() {
        try {
            audioStream.close();
        } catch (IOException e) {
        }
    }

    /**
     * Read first 4 bytes from the byte array and transform it to a 32-bit integer in little endian form.
     *
     * @param bytes a byte array
     * @return integer value
     */
    int getIntLittleEndian(byte[] bytes) {
        return (bytes[0] & 0xff)
            | ((bytes[1] & 0xff) << 8)
            | ((bytes[2] & 0xff) << 16)
            | ((bytes[3] & 0xff) << 24);
    }

    /**
     * Read first 2 bytes from the byte array and transform it to a 16-bit short in little endian form.
     *
     * @param bytes a byte array
     * @return short value
     */
    short getShortLittleEndian(byte[] bytes) {
        return (short) ((bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8));
    }

    /**
     * Extract the RIFF WAVE file header from the audio stream containing the format of the sound information
     *
     * @return the RIFF WAVE file header
     */
    private RIFFWaveHeader extractRIFFWaveHeader() {
        try {
            // Check if file container format is RIFF (Resource Interchange File Format).
            int riffChunkId = getIntLittleEndian(audioStream.readNBytes(4));
            if (riffChunkId != RIFFWaveHeader.RIFF_CHUNK_DESCRIPTOR) {
                throw new DecoderException("Not a RIFF file.");
            }

            // This is the file size minus 8 bytes (RIFF chunk ID and RIFF chunk size) - ignored
            int chunkSize = getIntLittleEndian(audioStream.readNBytes(4));

            // Check if the file format is WAVE.
            int format = getIntLittleEndian(audioStream.readNBytes(4));
            if (format != RIFFWaveHeader.WAVE_CHUNK_DESCRIPTOR) {
                throw new DecoderException("Unknown file format - not WAVE.");
            }

            // Check the format sub-chunk.
            int formatChunkId = getIntLittleEndian(audioStream.readNBytes(4));
            if (formatChunkId != RIFFWaveHeader.FORMAT_CHUNK_DESCRIPTOR) {
                throw new DecoderException("Illegal format sub-chunk.");
            }

            // Size of the PCM sub-chunk - should be 16 bytes.
            int pcmSubChunkSize = getIntLittleEndian(audioStream.readNBytes(4));
            if (pcmSubChunkSize != RIFFWaveHeader.PCM_SUB_CHUNK1_SIZE) {
                throw new DecoderException("Illegal PCM sub-chunk size: " + pcmSubChunkSize);
            }

            // Read two bytes at position 20 and check if the audio coding format is linear PCM.
            short encodingFormat = getShortLittleEndian(audioStream.readNBytes(2));
            if (encodingFormat != AudioCodingFormat.LINEAR_PCM.getCode()) {
                throw new DecoderException("Unsupported audio coding format");
            }

            // Read two bytes at position 22 to get the number of channels.
            short channels = getShortLittleEndian(audioStream.readNBytes(2));

            // Read four bytes at position 24 and to get the sample rate.
            int sampleRate = getIntLittleEndian(audioStream.readNBytes(4));

            /*
                Read four bytes at position 28 to get the byte rate.
                Bytes per second is the speed of the audio data stream:
                sample rate * number of channels * bits per sample / 8
             */
            int bytesPerSecond = getIntLittleEndian(audioStream.readNBytes(4));

            /*
                Read two bytes at position 32 to get the block alignment
                (the number of bytes for one sample including all channels.)
             */
            short blockAlignment = getShortLittleEndian(audioStream.readNBytes(2));

            // Read two bytes at position 34 to get the quantisation bits per sample
            short bitsPerSample = getShortLittleEndian(audioStream.readNBytes(2));

            /*
                In general the RIFF WAVE header size is 44 bytes. There may be additional sub-chunks. If so,
                each will have a 4 bytes SubChunkID, 4 bytes of SubChunkSize and SubChunkSize amount of data.
                The rest is audio data.
                "data" marker should follow at position 36 - skip over any padding/junk data.
             */
            int junkData = 0;
            while (getIntLittleEndian(audioStream.readNBytes(4)) != RIFFWaveHeader.DATA_CHUNK_DESCRIPTOR) {
                int subChunkSize = getIntLittleEndian(audioStream.readNBytes(4));
                junkData += audioStream.skip(subChunkSize);
            }

            int dataSize = getIntLittleEndian(audioStream.readNBytes(4));
            return new RIFFWaveHeader(AudioCodingFormat.from(encodingFormat), channels, sampleRate, bitsPerSample, dataSize);
        } catch (IOException | DecoderException ex) {
            throw new RuntimeException();
        }
    }
}