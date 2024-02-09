package it.unipi.puffotuner.audioprocessing

import kotlin.math.absoluteValue
import kotlin.math.pow


const val DEFAULT_THRESHOLD = 0.20f


fun parabolicInterpolation(yinBuffer: FloatArray, tau: Int): Float {
    if (tau == yinBuffer.size) {
        return tau.toFloat()
    }


    val betterTau = if (0 < tau && tau < yinBuffer.size - 1) {
        val s0 = yinBuffer[tau - 1]
        val s1 = yinBuffer[tau]
        val s2 = yinBuffer[tau + 1]

        var adjustment = (s2 - s0) / (2.0f * (2.0f * s1 - s2 - s0))

        if (adjustment.absoluteValue > 1) {
            adjustment = 0.0f
        }

        tau + adjustment
    } else {
        tau.toFloat()
    }

    return betterTau.absoluteValue
}


fun cumulativeMeanNormalizedDifference(yinBuffer: FloatArray): FloatArray {
    val yinBufferCopy = yinBuffer.copyOf()
    yinBufferCopy[0] = 1.0f
    var runningSum = 0.0f
    for (index in 1 until yinBufferCopy.size) {
        runningSum += yinBufferCopy[index]
        if (runningSum == 0.0f) {
            yinBufferCopy[index] = 0.0f
        } else {
            yinBufferCopy[index] *= index / runningSum
        }
    }
    return yinBufferCopy
}


fun absoluteThreshold(yinBuffer: FloatArray, threshold: Float): Int {
    var tau = 2
    var minTau = 0
    var minVal = 1000.0f

    while (tau < yinBuffer.size) {
        if (yinBuffer[tau] < threshold) {
            while ((tau + 1) < yinBuffer.size && yinBuffer[tau + 1] < yinBuffer[tau]) {
                tau += 1
            }
            return tau
        } else {
            if (yinBuffer[tau] < minVal) {
                minVal = yinBuffer[tau]
                minTau = tau
            }
        }
        tau += 1
    }

    if (minTau > 0) {
        return -minTau
    }

    return 0
}


fun yinPitchDetection(buffer: FloatArray, sr: Int, threshold: Float = DEFAULT_THRESHOLD): Float {
    val wLen = buffer.size
    val tauRange = (sr * 0.02).toInt()

    val diff = FloatArray(tauRange)
    for (tau in 0 until tauRange) {
        var sum = 0.0f
        for (i in 0 until wLen - tau) {
            sum += (buffer[i] - buffer[i + tau]).pow(2)
        }
        diff[tau] = sum
    }

    val cmndf = cumulativeMeanNormalizedDifference(diff)

    val tau = absoluteThreshold(cmndf, threshold)

    val betterTau = parabolicInterpolation(cmndf, tau)

    return sr.toFloat() / betterTau
}



