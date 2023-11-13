package com.robkov.game.pinball.models.gameobjects.primitiveboundaries

import android.graphics.Canvas
import android.graphics.Paint
import com.robkov.game.pinball.models.gameobjects.Ball
import com.robkov.game.pinball.models.geometry.Circle
import com.robkov.game.pinball.models.geometry.Point
import com.robkov.game.pinball.models.geometry.Vector
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

class Disk(center: Point, radius: Float): IPrimitiveBoundary {
    override val baseFigure = Circle(center, radius)
    override val restitution =1f
    override var score =0
    override fun isCollidingWith(ball: Ball): Boolean {
        return (collisionPointWith(ball)!=null)
    }

    constructor(circle: Circle): this(circle.center, circle.radius) {}
    constructor(center: Point, radius: Float, score: Int): this(center, radius) {
        this.score = score
    }

    override fun collideWith(collidingBall: Ball): Ball? {
        val collisionPoint = collisionPointWith(collidingBall)
        if (collisionPoint!=null && collidingBall.velocityVector.getLength()>0) {
            var ball = collidingBall.copy()
            var helpingVector = Vector(baseFigure.center, ball.center).move(collisionPoint)
            ball.velocityVector = ball.velocityVector
                .addVector(helpingVector.scaleTo(ball.velocityVector.getLength()).multiplyBy(2*sin(ball.velocityVector.angleToVector(helpingVector))))
                .multiplyBy(ball.restitution*restitution)
            val newCenterPoint = ball.movementVector.crossingPointWith(Circle(baseFigure.center,baseFigure.radius+ball.radius), false)
            ball.moveTo(newCenterPoint ?: ball.movementVector.originPoint)
            return ball
        }
        return null
    }

    override fun collisionPointWith(ball: Ball): Point? {
        val distanceBetweenCenters =Vector(baseFigure.center, ball.center).getLength()
        if (distanceBetweenCenters<ball.radius+baseFigure.radius && distanceBetweenCenters> abs(baseFigure.radius-ball.radius)) {
            return Vector(baseFigure.center, ball.center).scaleTo(baseFigure.radius).getEndPoint()
        }
        var potentialCrossingPoint = ball.movementVector.crossingPointWith(baseFigure, false)
        if (potentialCrossingPoint!=null) {
            return potentialCrossingPoint
        }
        val helpingVector = ball.movementVector.rotate(PI.toFloat()/2).scaleTo(ball.radius)
        potentialCrossingPoint = ball.movementVector.move(helpingVector.getEndPoint()).crossingPointWith(baseFigure, false)
        if (potentialCrossingPoint!=null) {
            return potentialCrossingPoint
        }
        potentialCrossingPoint = ball.movementVector.move(helpingVector.reverse().getEndPoint()).crossingPointWith(baseFigure, false)
        if (potentialCrossingPoint!=null) {
            return potentialCrossingPoint
        }
        return null
    }

    override fun scale(width: Int, height: Int) {
        baseFigure.center.xPosition *= width
        baseFigure.center.yPosition *= height
        baseFigure.radius *= width
    }

    override fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        baseFigure.draw(canvas, paint, color, strokeWidth)
    }



}