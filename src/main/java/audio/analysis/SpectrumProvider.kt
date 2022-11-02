package audio.analysis

import audio.io.Decoder

/**
 * Provides float[] arrays of successive spectrum frames retrieved via
 * FFT from a Decoder. The frames might overlapp by n samples also called
 * the hop size. Using a hop size smaller than the spectrum size is beneficial
 * in most cases as it smears out the spectra of successive frames somewhat.
 *
 * @author VITTACH
 */
class SpectrumProvider(decoder: Decoder?, sampleWindowSize: Int, hopSize: Int, useHamming: Boolean) {

    private val decoder: Decoder

    private var samples: FloatArray

    private var nextSamples: FloatArray

    private val tempSamples: FloatArray

    private var currentSample = 0

    private val hopSize: Int

    val fft: FFT

    /**
     * Returns the next spectrum or null if there's no more data.
     *
     * @return The next spectrum or null.
     */
    fun nextSpectrum(): FloatArray? {
        if (currentSample >= samples.size) {
            val tmp = nextSamples
            nextSamples = samples
            samples = tmp
            if (decoder.readSamples(nextSamples) == 0) return null
            currentSample -= samples.size
        }
        System.arraycopy(samples, currentSample, tempSamples, 0, samples.size - currentSample)
        System.arraycopy(nextSamples, 0, tempSamples, samples.size - currentSample, currentSample)
        fft.forward(tempSamples)
        currentSample += hopSize
        return fft.getSpectrum()
    }

    /**
     * Constructor, sets the [Decoder], the sample window size and the
     * hop size for the spectra returned. Say the sample window size is 1024
     * samples. To get an overlapp of 50% you specify a hop size of 512 samples,
     * for 25% overlap you specify a hopsize of 256 and so on. Hop sizes are of
     * course not limited to powers of 2.
     *
     * @param decoder          The decoder to get the samples from.
     * @param sampleWindowSize The sample window size.
     * @param hopSize          The hop size.
     * @param useHamming       Wheter to use hamming smoothing or not.
     */
    init {
        requireNotNull(decoder) { "Decoder must be != null" }
        require(sampleWindowSize > 0) { "Sample window size must be > 0" }
        require(hopSize > 0) { "Hop size must be > 0" }
        require(sampleWindowSize >= hopSize) { "Hop size must be <= sampleSize" }
        this.decoder = decoder
        samples = FloatArray(sampleWindowSize)
        nextSamples = FloatArray(sampleWindowSize)
        tempSamples = FloatArray(sampleWindowSize)
        this.hopSize = hopSize
        fft = FFT(sampleWindowSize, 44100f)
        if (useHamming) fft.window(FFT.HAMMING)
        decoder.readSamples(samples)
        decoder.readSamples(nextSamples)
    }
}