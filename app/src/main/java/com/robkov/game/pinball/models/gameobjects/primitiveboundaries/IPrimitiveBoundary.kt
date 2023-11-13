package com.robkov.game.pinball.models.gameobjects.primitiveboundaries

import android.graphics.Canvas
import android.graphics.Paint
import com.robkov.game.pinball.models.gameobjects.Ball
import com.robkov.game.pinball.models.geometry.Point

interface IPrimitiveBoundary {
    val baseFigure: Any
    val restitution: Float
    var score: Int

    fun isCollidingWith(ball: Ball): Boolean
    fun collideWith(ball: Ball): Ball?
    fun collisionPointWith(ball: Ball): Point?
    fun scale(width: Int, height: Int)
    fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float)
}