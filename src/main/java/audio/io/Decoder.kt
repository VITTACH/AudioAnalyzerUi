package audio.io

/**
 * Interface for com.badlogic.audio decoders that return successive
 * amplitude frames.
 *
 * @author VITTACH
 */
interface Decoder {
    fun readSamples(samples: FloatArray?): Int
}