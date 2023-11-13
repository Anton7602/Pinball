package com.robkov.game.pinball.models.geometry

import android.graphics.Canvas
import android.graphics.Paint

class Circle(center: Point, radius: Float): Arc(center, radius, 0f, 360f) {

    override fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        canvas.drawCircle(center.xPosition, canvas.height-center.yPosition, radius, paint)
    }


}