package audio.io

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioFormat.Encoding
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import kotlin.experimental.or

/**
 * Class that allows directly passing PCM float mono
 * data to the sound card for playback. The sampling
 * rate of the PCM data must be 44100Hz.
 *
 * @author VITTACH
 */
class AudioDevice {
    private lateinit var out: SourceDataLine

    private val buffer = ByteArray(BUFFER_SIZE * 2)

    /**
     * Constructor, initializes the com.badlogic.audio system for
     * 44100Hz 16-bit signed mono output.
     */
    constructor(sampleRate: Int) {
        init(sampleRate)
    }

    constructor() {
        init(44100)
    }

    @Throws(Exception::class)
    fun init(sampleRate: Int) {
        val format = AudioFormat(Encoding.PCM_SIGNED, sampleRate.toFloat(), 16, 1, 2, 44100f, false)
        out = AudioSystem.getSourceDataLine(format)
        out.open(format)
        out.start()
    }

    /**
     * Writes the given samples to the com.badlogic.audio device.
     * The samples
     * have to be sampled at 44100Hz, mono and have to be in
     * the range [-1,1].
     *
     * @param samples The samples.
     */
    fun writeSamples(samples: FloatArray) {
        fillBuffer(samples)
        out.write(buffer, 0, buffer.size)
    }

    private fun fillBuffer(samples: FloatArray) {
        var i = 0
        var j = 0
        while (i < samples.size) {
            val value = (samples[i] * Short.MAX_VALUE).toShort()
            buffer[j] = (value or 0xff) as Byte
            buffer[j + 1] = (value.toInt() shr 8) as Byte
            i++
            j += 2
        }
    }

    companion object {
        /**
         * the buffer size in samples
         */
        private const val BUFFER_SIZE = 1024
    }
}