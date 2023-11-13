package com.robkov.game.pinball.models.gameobjects.complexboundaries

import android.graphics.Canvas
import android.graphics.Paint
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.Arch
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.IPrimitiveBoundary
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.Line
import com.robkov.game.pinball.models.geometry.Point

class RoundedRectangle(var top: Float, var bottom: Float, var left: Float, var right: Float, var topRight: Boolean, var bottomRight: Boolean, var bottomLeft: Boolean, var topLeft: Boolean ): IComplexBoundary {
    var cornerRadius = (right-left)/2
    var width = cornerRadius
    var height = top-bottom
    override val boundaries = mutableListOf<IPrimitiveBoundary>()

    init {
        setUpBoundaries()
    }

    private fun setUpBoundaries() {
        if (topRight) {
            boundaries.add(Arch(Point(right-cornerRadius, top-cornerRadius),cornerRadius, 270f,90f))
            boundaries.add(Line(Point(right,top-cornerRadius), Point(right,top-height/2)))
        }
        if (bottomRight) {
            boundaries.add(Arch(Point(right-cornerRadius, bottom+cornerRadius),cornerRadius, 0f,90f))
            boundaries.add(Line(Point(right,bottom+cornerRadius), Point(right,bottom+height/2)))
        }
        if (bottomLeft) {
            boundaries.add(Arch(Point(left+cornerRadius, bottom+cornerRadius),cornerRadius, 90f,90f))
            boundaries.add(Line(Point(left,bottom+cornerRadius), Point(left,bottom+height/2)))
        }
        if (topLeft) {
            boundaries.add(Arch(Point(left+cornerRadius, top-cornerRadius),cornerRadius, 180f,90f))
            boundaries.add(Line(Point(left,top-cornerRadius), Point(left,top-height/2)))
        }
    }

    override fun scale(width: Int, height: Int) {
        val newRoundedRectangle = RoundedRectangle(top*height, bottom*height, left*width, right*width, topRight, bottomRight, bottomLeft, topLeft)
        top = newRoundedRectangle.top
        bottom = newRoundedRectangle.bottom
        left = newRoundedRectangle.left
        right = newRoundedRectangle.right
        topRight = newRoundedRectangle.topRight
        bottomRight = newRoundedRectangle.bottomRight
        bottomLeft = newRoundedRectangle.bottomLeft
        topLeft = newRoundedRectangle.topLeft
        cornerRadius = (right-left)/2
        this.width = cornerRadius
        this.height = top-bottom
        boundaries.clear()
        setUpBoundaries()
    }

    override fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        boundaries.forEach { boundary ->
            boundary.draw(canvas,paint, color, strokeWidth)
        }
    }
}