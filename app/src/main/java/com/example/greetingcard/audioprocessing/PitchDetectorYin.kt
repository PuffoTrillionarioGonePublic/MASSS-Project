package com.example.greetingcard.audioprocessing


const val DEFAULT_THRESHOLD = 0.20f

fun getPitch(audioBuffer: FloatArray, sampleRate: Float, threshold: Float = DEFAULT_THRESHOLD): Float? {
    val yinBuffer = difference(audioBuffer)
    cumulativeMeanNormalizedDifference(yinBuffer)
    val tauEstimate = absoluteThreshold(yinBuffer, threshold)
    return if (tauEstimate != 0) {
        val betterTau = parabolicInterpolation(yinBuffer, tauEstimate)
        sampleRate / betterTau
    } else {
        null
    }
}

fun difference(buffer: FloatArray): FloatArray {
    val bufferHalfCount = buffer.size / 2
    val yinBuffer = FloatArray(bufferHalfCount)

    for (tau in 0 until bufferHalfCount) {
        for (i in 0 until bufferHalfCount) {
            val delta = buffer[i] - buffer[i + tau]
            yinBuffer[tau] += delta * delta
        }
    }

    return yinBuffer
}


private fun cumulativeMeanNormalizedDifference(yinBuffer: FloatArray) {
    yinBuffer[0] = 1f
    var runningSum = 0f
    yinBuffer.mapIndexed { index, value ->
        if (index > 0) {
            runningSum += value
            yinBuffer[index] = value * index / runningSum
        }
    }
}


private fun absoluteThreshold(yinBuffer: FloatArray, threshold: Float): Int {
    var tau = 2
    var minTau = 0
    var minVal = 1000.0f

    while (tau < yinBuffer.size) {
        if (yinBuffer[tau] < threshold) {
            while (tau + 1 < yinBuffer.size && yinBuffer[tau + 1] < yinBuffer[tau]) {
                tau++
            }
            return tau
        } else {
            if (yinBuffer[tau] < minVal) {
                minVal = yinBuffer[tau]
                minTau = tau
            }
        }
        tau++
    }

    if (minTau > 0) {
        return -minTau
    }

    return 0
}
private fun parabolicInterpolation(yinBuffer: FloatArray, tauEstimate: Int): Float {
    val betterTau: Float
    val x0 = if (tauEstimate < 1) {
        tauEstimate
    } else {
        tauEstimate - 1
    }
    val x2 = if (tauEstimate + 1 < yinBuffer.size) {
        tauEstimate + 1
    } else {
        tauEstimate
    }
    if (x0 == tauEstimate) {
        betterTau = if (yinBuffer[tauEstimate] <= yinBuffer[x2]) {
            tauEstimate.toFloat()
        } else {
            x2.toFloat()
        }
    } else if (x2 == tauEstimate) {
        betterTau = if (yinBuffer[tauEstimate] <= yinBuffer[x0]) {
            tauEstimate.toFloat()
        } else {
            x0.toFloat()
        }
    } else {
        val s0 = yinBuffer[x0]
        val s1 = yinBuffer[tauEstimate]
        val  s2 = yinBuffer[x2]
        val adjustment = (s2 - s0) / (2 * (2 * s1 - s2 - s0))
        betterTau = tauEstimate + adjustment
    }
    return betterTau
}
