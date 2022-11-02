package audio.analysis

import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Calculates a threshold function based on the spectral flux.
 * Consturctor, sets the history size in number of spectra
 * to take into account to calculate the average spectral flux
 * at a specific position. Also sets the multiplier to
 * multiply the average with.
 *
 * @param historySize The history size.
 * @param multiplier  The average multiplier.
 *
 * @author VITTACH
 */
class ThresholdFunction(
    private val historySize: Int,
    private val multiplier: Float
) {

    /**
     * Returns the threshold function for a given
     * spectral flux function.
     *
     * @return The threshold function.
     */
    fun calculate(spectralFlux: List<Float>): List<Float> {
        val thresholds = ArrayList<Float>(spectralFlux.size)
        for (i in spectralFlux.indices) {
            var sum = 0f
            val start = max(0, i - historySize / 2)
            val end = min(spectralFlux.size - 1, i + historySize / 2)
            for (j in start..end) {
                sum += spectralFlux[j]
            }
            sum /= (end - start).toFloat()
            sum *= multiplier
            thresholds.add(sum)
        }
        return thresholds
    }

}