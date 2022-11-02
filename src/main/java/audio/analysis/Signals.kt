package audio.analysis

/**
 * Some signal metric functions like energy, power etc.
 *
 * @author VITTACH
 */
object Signals {
    fun mean(signal: FloatArray): Float {
        var mean = 0f
        for (i in signal.indices) mean += signal[i]
        mean /= signal.size.toFloat()
        return mean
    }

    fun energy(signal: FloatArray): Float {
        var totalEnergy = 0f
        for (i in signal.indices) totalEnergy += signal[i] * signal[i]
        return totalEnergy
    }

    fun power(signal: FloatArray): Float {
        return energy(signal) / signal.size
    }

    fun norm(signal: FloatArray): Float {
        return Math.sqrt(energy(signal).toDouble()).toFloat()
    }

    fun minimum(signal: FloatArray): Float {
        var min = Float.POSITIVE_INFINITY
        for (i in signal.indices) min = Math.min(min, signal[i])
        return min
    }

    fun maximum(signal: FloatArray): Float {
        var max = Float.NEGATIVE_INFINITY
        for (i in signal.indices) max = Math.max(max, signal[i])
        return max
    }

    fun scale(signal: FloatArray, scale: Float) {
        for (i in signal.indices) signal[i] *= scale
    }
}