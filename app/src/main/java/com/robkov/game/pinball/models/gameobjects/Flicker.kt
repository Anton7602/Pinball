package com.robkov.game.pinball.models.gameobjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.robkov.game.pinball.models.gameobjects.complexboundaries.IComplexBoundary
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.IPrimitiveBoundary
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.Line
import com.robkov.game.pinball.models.geometry.Circle
import com.robkov.game.pinball.models.geometry.Point
import com.robkov.game.pinball.models.geometry.Vector
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.sin

class Flicker(vector: Vector): RotatingObject(0.05f, vector), IComplexBoundary {
    override val boundaries = mutableListOf<IPrimitiveBoundary>()
    var reverseRotation = false
    var maxAngle = 30
    var minAngle = -30

    init {
        updateBoundariesPositions()
    }

    constructor(vector: Vector, tAngle: Int, bAngle: Int, mass: Float): this(vector) {
       maxAngle = tAngle
       minAngle = bAngle
    }

    private fun updateBoundariesPositions() {
        boundaries.clear()
        boundaries.add(Line(baseVector.originPoint, baseVector.getEndPoint()))
    }

    fun isCollidingWith(ball: Ball): Boolean {
        return (collisionPointWith(ball)!=null)
    }

    fun collideWith(collidingBall: Ball): Ball? {
        var collisionPoint = Point(-1f,-1f)
        var ball: Ball = collidingBall.copy()
        boundaries.forEach { boundary ->
            collisionPoint = boundary.collisionPointWith(ball) ?: collisionPoint
            ball = boundary.collideWith(ball) ?: ball
        }
        if (collisionPoint.xPosition>=0) {
            ball.velocityVector = ball.velocityVector.addVector(getLinearVelocityVector(collisionPoint))
            angularVelocity = 0f
            return ball
        }
        return null
    }

    fun collisionPointWith(ball: Ball): Point? {
        //Checking if ball's movement vector crosses obstacle and if obstacle's base vector is crossing the ball
        var potentialCrossingPoint = baseVector.crossingPointWith(ball.movementVector)?: baseVector.crossingPointWith(
            Circle(ball.center, ball.radius.toFloat()), true)
        if (potentialCrossingPoint!= null) {
            return potentialCrossingPoint
        } else {
            //Copy and move movement vector to top and bottom points of a ball to check if top or bottom of the ball is colliding with obstacle
            val collisionRayTop = ball.movementVector.move(ball.movementVector.scaleTo(ball.radius).rotate(PI.toFloat()/2).getEndPoint())
            val collisionRayBottom = ball.movementVector.move(ball.movementVector.scaleTo(ball.radius).rotate(-PI.toFloat()/2).getEndPoint())
            potentialCrossingPoint = baseVector.crossingPointWith(collisionRayTop) ?: baseVector.crossingPointWith(collisionRayBottom)
            if (potentialCrossingPoint!=null) return potentialCrossingPoint
        }
        return null
    }

    override fun rotate(frameTimeSeconds: Float) {
        val preRotatedVector = baseVector
        if (reverseRotation) {
            forceVector = calculateForceVector()
            appliedForces.clear()
            appliedForces.add(forceVector.reverse())
        }
        super.rotate(frameTimeSeconds)
        if(sin(atan(baseVector.k))<sin(minAngle* PI/180) || sin(atan(baseVector.k))>sin(maxAngle* PI/180)) {
            baseVector = preRotatedVector
            velocityVector = Vector(0f,0f, center)
            angularAcceleration = 0f
            angularVelocity = 0f
        }
        updateBoundariesPositions()
    }

    override fun move(frameTimeSeconds: Float) {
        super.move(frameTimeSeconds)
        updateBoundariesPositions()
    }

    override fun scale(width: Int, height: Int) {
        boundaries.forEach { boundary ->
            boundary.scale(width,height)
        }
    }

    override fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        boundaries.forEach { boundary ->
            boundary.draw(canvas, paint, color, strokeWidth)
        }
        paint.color = Color.LTGRAY
        canvas.drawCircle(baseVector.originPoint.xPosition,canvas.height-baseVector.originPoint.yPosition,strokeWidth/3, paint)
    }

    fun drawFlickerParameters(canvas: Canvas, paint: Paint) {
        velocityVector.draw(canvas, paint, Color.MAGENTA, 5f)
        accelerationVector.draw(canvas, paint, Color.CYAN, 5f)
        getLinearVelocityVector(baseVector.getMiddlePoint()).draw(canvas, paint, Color.MAGENTA, 5f)
    }

    fun logFlickerParameters() {
        Log.d("Debug", " ")
        Log.d("Debug", "Force: $forceVector")
        Log.d("Debug", "Acceleration: $accelerationVector")
        Log.d("Debug", "AngularVelocity: $angularVelocity")
        Log.d("Debug", "Position: x: ${center.xPosition} y: ${center.yPosition}")
    }
}