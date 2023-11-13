package com.robkov.game.pinball.models.gameobjects.complexboundaries

import android.graphics.Canvas
import android.graphics.Paint
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.IPrimitiveBoundary

interface IComplexBoundary {
    val boundaries: MutableList<IPrimitiveBoundary>
    fun scale(width: Int, height: Int)
    fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float)
}