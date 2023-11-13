package com.robkov.game.pinball.models.gameobjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.robkov.game.pinball.models.geometry.Circle
import com.robkov.game.pinball.models.geometry.Point

class Ball(ballCenter: Point, var radius: Int) : MovingObject(0.1f, ballCenter) {
    constructor(ballBody: Circle): this(ballBody.center, ballBody.radius.toInt())
    constructor(ballCenter: Point, ballRadius: Int, ballMass: Float, ballRestitution: Float) : this(ballCenter, ballRadius) {
        mass=ballMass
        restitution = ballRestitution
        friction = 0.2f
    }

    fun copy() : Ball {
        val newBall = Ball(center,radius, mass, restitution)
        newBall.appliedForces = appliedForces
        newBall.accelerationVector = accelerationVector
        newBall.velocityVector = velocityVector
        newBall.movementVector = movementVector
        return newBall
    }
    fun draw(canvas: Canvas, paint: Paint, color: Int) {
        paint.color = color
        paint.style = Paint.Style.FILL
        canvas.drawCircle(center.xPosition, canvas.height-center.yPosition, radius.toFloat(), paint)
    }

    fun drawBallParameters(canvas: Canvas, paint: Paint) {
        accelerationVector.draw(canvas, paint, Color.GREEN, 2f)
        velocityVector.draw(canvas, paint, Color.BLUE, 2f)
        movementVector.draw(canvas, paint, Color.RED, 5f)
    }

    private fun logBallParameters() {
        Log.d("Debug", " ")
        Log.d("Debug", "Ball Parameters:")
        Log.d("Debug", "Force: $forceVector")
        Log.d("Debug", "Acceleration: $accelerationVector")
        Log.d("Debug", "Velocity: $velocityVector")
        Log.d("Debug", "Movement: $movementVector")
        Log.d("Debug", "Position: x: ${center.xPosition} y: ${center.yPosition}")
    }
}