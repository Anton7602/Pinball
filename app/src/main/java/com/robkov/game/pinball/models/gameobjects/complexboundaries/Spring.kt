package com.robkov.game.pinball.models.gameobjects.complexboundaries

import android.graphics.Canvas
import android.graphics.Paint
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.IPrimitiveBoundary
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.Line
import com.robkov.game.pinball.models.geometry.Point
import com.robkov.game.pinball.models.geometry.Vector

class Spring(topLeftCorner: Point, width: Float, height: Float): IComplexBoundary {
    override val boundaries= mutableListOf<IPrimitiveBoundary>()

    var baseVector = Vector(0f, height, topLeftCorner)

    init {
        boundaries.add(Line(topLeftCorner,Point(topLeftCorner.xPosition, topLeftCorner.yPosition-height)))
        boundaries.add(Line(Point(topLeftCorner.xPosition, topLeftCorner.yPosition), Point(topLeftCorner.xPosition+width, topLeftCorner.yPosition),4f))
        boundaries.add(Line(Point(topLeftCorner.xPosition+width, topLeftCorner.yPosition), Point(topLeftCorner.xPosition+width, topLeftCorner.yPosition-height)))
        boundaries.add(Line(Point(topLeftCorner.xPosition, topLeftCorner.yPosition-height), Point(topLeftCorner.xPosition+width, topLeftCorner.yPosition-height)))
    }

    override fun scale(width: Int, height: Int) {
        boundaries.forEach { boundary ->
            boundary.scale(width,height)
        }
    }

    override fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        boundaries.forEach { boundary -> boundary.draw(canvas, paint, color, strokeWidth)}
    }

}