package com.sample.hmssample.ml.handkeypointdetectiondemo.model

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.min

private val SQRT_DIG = BigDecimal(150)
private val SQRT_PRE = BigDecimal(10).pow(SQRT_DIG.toInt())
private const val BIGDECIMAL_SCALE = 10
private const val BIGDECIMAL_ROUNDINGMODE = BigDecimal.ROUND_DOWN

object SimilarityUtil {
    fun compare(array1: Array<Float>, array2: Array<Float>): BigDecimal {
        val size = min(array1.size, array2.size) - 1

        var numerator = BigDecimal.ZERO
        for (i in 0..size) {
            numerator = numerator.add(BigDecimal.valueOf(array1[i].toDouble() * array2[i].toDouble()))
        }

        var denominator1 = BigDecimal.ZERO
        array1.forEach { value ->
            val bigValue = BigDecimal.valueOf(value.toDouble())
            denominator1 = denominator1.add(bigValue.multiply(bigValue))
        }
        denominator1 = denominator1.sqrt().setScale(BIGDECIMAL_SCALE, BIGDECIMAL_ROUNDINGMODE)

        var denominator2 = BigDecimal.ZERO
        array2.forEach { value ->
            val bigValue = BigDecimal.valueOf(value.toDouble())
            denominator2 = denominator2.add(bigValue.multiply(bigValue))
        }
        denominator2 = denominator2.sqrt().setScale(BIGDECIMAL_SCALE, BIGDECIMAL_ROUNDINGMODE)

        return numerator.divide(denominator1, BIGDECIMAL_SCALE, BIGDECIMAL_ROUNDINGMODE).divide(denominator2, BIGDECIMAL_SCALE, BIGDECIMAL_ROUNDINGMODE)
    }
}

private fun sqrtNewtonRaphson(c: BigDecimal, xn: BigDecimal, precision: BigDecimal): BigDecimal {
    val fx = xn.pow(2).add(c.negate())
    val fpx = xn.multiply(BigDecimal(2))
    var xn1: BigDecimal = fx.divide(fpx, 2 * SQRT_DIG.toInt(), RoundingMode.HALF_DOWN)
    xn1 = xn.add(xn1.negate())
    val currentSquare = xn1.pow(2)
    var currentPrecision = currentSquare.subtract(c)
    currentPrecision = currentPrecision.abs()
    return if (currentPrecision.compareTo(precision) <= -1) {
        xn1
    } else sqrtNewtonRaphson(c, xn1, precision)
}

fun BigDecimal.sqrt(): BigDecimal {
    return sqrtNewtonRaphson(this, BigDecimal(1), BigDecimal(1).divide(SQRT_PRE))
}