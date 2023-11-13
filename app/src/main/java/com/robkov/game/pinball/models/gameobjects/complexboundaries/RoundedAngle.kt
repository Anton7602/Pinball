package com.robkov.game.pinball.models.gameobjects.complexboundaries

import android.graphics.Canvas
import android.graphics.Paint
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.Arch
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.IPrimitiveBoundary
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.Line
import com.robkov.game.pinball.models.geometry.Arc
import com.robkov.game.pinball.models.geometry.Circle
import com.robkov.game.pinball.models.geometry.Point
import com.robkov.game.pinball.models.geometry.Vector
import kotlin.math.sin

class RoundedAngle(var originPoint: Point, var radius: Float, var vectorStart: Vector, var vectorEnd: Vector) :IComplexBoundary {
    override val boundaries = mutableListOf<IPrimitiveBoundary>()

    init {
        setUpBoundaries()
    }

    private fun setUpBoundaries() {
        vectorStart = vectorStart.move(originPoint)
        vectorEnd = vectorEnd.move(originPoint)
        val vectorMid: Vector =
            if (vectorStart.rotate(vectorStart.angleToVector(vectorEnd)/2).getEndPoint().distanceToPoint(vectorEnd.getEndPoint()) <
            vectorStart.rotate(-vectorStart.angleToVector(vectorEnd)/2).getEndPoint().distanceToPoint(vectorEnd.getEndPoint())) {
            vectorStart.rotate(vectorStart.angleToVector(vectorEnd)/2)
        } else {
            vectorStart.rotate(-vectorStart.angleToVector(vectorEnd)/2)
        }
        val circle = Circle(vectorMid.scaleTo(radius/sin(vectorStart.angleToVector(vectorEnd)/2)).getEndPoint(), radius)
        val pointStart = vectorStart.crossingPointWith(vectorStart.move(circle.center).orthoVectorToPoint(vectorStart.originPoint).scaleTo(radius*2))
        val pointEnd = vectorEnd.crossingPointWith(vectorEnd.move(circle.center).orthoVectorToPoint(vectorEnd.originPoint).scaleTo(radius*2))
        if (pointStart!= null && pointEnd!=null) {
            //vectorStart = Vector(pointStart, vectorStart.getEndPoint())
            //vectorEnd = Vector(pointEnd, vectorEnd.getEndPoint())
            boundaries.add(Arch(Arc(circle.center, Vector(circle.center, pointStart), Vector(circle.center, pointEnd), radius, true)))
            boundaries.add(Line(pointStart, vectorStart.getEndPoint()))
            boundaries.add(Line(pointEnd, vectorEnd.getEndPoint()))
        } else {
            boundaries.add(Line(vectorStart))
            boundaries.add(Line(vectorEnd))
        }
    }

    override fun scale(width: Int, height: Int) {
        val newAngle = RoundedAngle(Point(originPoint.xPosition*width,originPoint.yPosition*height),
            radius*width, Vector(originPoint, vectorStart.getEndPoint()).scaleTo(width.toFloat(), height.toFloat()),
            Vector(originPoint, vectorEnd.getEndPoint()).scaleTo(width.toFloat(), height.toFloat()))
        originPoint = newAngle.originPoint
        radius = newAngle.radius
        vectorStart = newAngle.vectorStart
        vectorEnd = newAngle.vectorEnd
        boundaries.clear()
        setUpBoundaries()
    }

    override fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        boundaries.forEach { boundary ->
            boundary.draw(canvas,paint, color, strokeWidth)
        }
    }
}