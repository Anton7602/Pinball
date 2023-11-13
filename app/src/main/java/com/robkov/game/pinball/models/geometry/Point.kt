package com.robkov.game.pinball.models.geometry

import kotlin.math.pow
import kotlin.math.sqrt

class Point(var xPosition: Float, var yPosition: Float) {
    override fun toString(): String { return "x: $xPosition, y: $yPosition" }
    override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Point) return false
            return xPosition == other.xPosition && yPosition == other.yPosition
        }

    override fun hashCode(): Int {
        val result = 31*xPosition*yPosition
        return result.toInt()
    }

    fun distanceToPoint(point: Point): Float { return sqrt((point.xPosition-xPosition).pow(2)+(point.yPosition-yPosition).pow(2)) }
}