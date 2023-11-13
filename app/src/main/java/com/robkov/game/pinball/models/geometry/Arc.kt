package com.robkov.game.pinball.models.geometry

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.PI

open class Arc(var center: Point, var radius: Float, var startAngle: Float, var sweepAngle: Float) {
    var startVector = Vector(radius,0f,center).rotate(-startAngle* PI.toFloat()/180)
    var midVector = startVector.rotate(-sweepAngle*PI.toFloat()/360)
    var endVector = startVector.rotate(-sweepAngle*PI.toFloat()/180)

    constructor(originPoint: Point, firstVector: Vector, secondVector: Vector, radius: Float, inner: Boolean): this(originPoint, radius, 0f, 0f) {
        //var direction = firstVector.vectorMultiplyBy(secondVector)
        val midVector = firstVector.rotate(firstVector.angleToVector(secondVector)/2)
        if (midVector.angleToVector(secondVector) > firstVector.angleToVector(secondVector)) {
            startVector = firstVector
            endVector = secondVector
        } else {
            startVector = secondVector
            endVector = firstVector
        }
        val xAxisVector = Vector(1f,0f)
        val direction = xAxisVector.vectorMyltuplyBy(startVector)
        startAngle = if (direction>0) {
            -xAxisVector.angleToVector(startVector)*180/PI.toFloat()
        } else {
            -(360 - xAxisVector.angleToVector(startVector)*180/PI.toFloat())
        }
        sweepAngle = if (inner) {
            startVector.angleToVector(endVector)*180/PI.toFloat()
        } else {
            360- startVector.angleToVector(endVector)*180/PI.toFloat()
        }
    }

    fun isContainsPoint(point: Point): Boolean {
        val baseVector = Vector(center, point)
        return (midVector.angleToVector(baseVector)<midVector.angleToVector(endVector))
    }

    open fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        val left = center.xPosition - radius
        val top = canvas.height - (center.yPosition + radius)
        val right = center.xPosition + radius
        val bottom = canvas.height - (center.yPosition - radius)
        val rectF = RectF(left, top, right, bottom)
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
        //drawDebug(canvas, paint, color, strokeWidth)
    }

    private fun drawDebug(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        startVector.draw(canvas,paint,Color.MAGENTA,strokeWidth)
        midVector.draw(canvas,paint,Color.MAGENTA,strokeWidth)
        endVector.draw(canvas,paint,Color.MAGENTA,strokeWidth)
    }
}

