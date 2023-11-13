package com.robkov.game.pinball.models.gameobjects.primitiveboundaries

import android.graphics.Canvas
import android.graphics.Paint
import com.robkov.game.pinball.models.gameobjects.Ball
import com.robkov.game.pinball.models.geometry.Circle
import com.robkov.game.pinball.models.geometry.Point
import com.robkov.game.pinball.models.geometry.Vector
import kotlin.math.PI
import kotlin.math.sin

class Line(startPoint: Point, endPoint: Point): IPrimitiveBoundary {
    override var baseFigure = Vector(startPoint, endPoint)
    override var restitution = 1f
    override var score = 0
    constructor(startPoint: Point, endPoint: Point, restitutionSet: Float): this(startPoint, endPoint) {
        restitution = restitutionSet
    }

    constructor(vector: Vector): this(vector.originPoint, vector.getEndPoint()) { }
    constructor(vector: Vector, restitutionSet: Float): this(vector.originPoint, vector.getEndPoint()) {
        restitution=restitutionSet
    }

    override fun isCollidingWith(ball: Ball): Boolean { return true }

    //Method collides Line with ball, changing ball's parameters and returning a score that is rewarded for hitting this particular obstacle
    override fun collideWith(collidingBall: Ball): Ball? {
        val collisionPoint = collisionPointWith(collidingBall)
        if (collisionPoint!=null && collidingBall.velocityVector.getLength()>0) {
            val ball = collidingBall.copy()
            //Constructing a perpendicular to the point of impact with length equal to a ball's velocity vector on to it
            var helpingVector = baseFigure.orthoVectorToPoint(ball.movementVector.originPoint).move(collisionPoint).scaleTo(ball.velocityVector.getLength())
            //New velocity vector is the result o vector sum of itself with projection of ball's speed on a perpendicular to the point of impact doubled
            // and multiplied to restitution to accommodate to energy loss
            ball.velocityVector = ball.velocityVector
                .addVector(helpingVector.multiplyBy(2 * sin(ball.velocityVector.angleToVector(baseFigure))))
                .multiplyBy(ball.restitution*restitution)
            //Highly unlikely tha frame gets ball in the right position in the moment of impact, so fixing ball's position to a correct one
            var newCenterPoint = helpingVector.scaleTo(ball.radius).getEndPoint()
            helpingVector = Vector(baseFigure.move(newCenterPoint).getEndPoint(), baseFigure.move(newCenterPoint).reverse().getEndPoint())
            ball.moveTo(ball.movementVector.crossingPointWith(helpingVector) ?: ball.movementVector.originPoint)
            return ball
        }
        return null
    }

    override fun collisionPointWith(ball: Ball): Point? {
        //Checking if ball's movement vector crosses obstacle and if obstacle's base vector is crossing the ball
        var potentialCrossingPoint = baseFigure.crossingPointWith(ball.movementVector)?: baseFigure.crossingPointWith(
            Circle(ball.center, ball.radius.toFloat()), true)
        if (potentialCrossingPoint!= null) {
            return potentialCrossingPoint
        } else {
            //Copy and move movement vector to top and bottom points of a ball to check if top or bottom of the ball is colliding with obstacle
            val collisionRayTop = ball.movementVector.move(ball.movementVector.scaleTo(ball.radius).rotate(PI.toFloat()/2).getEndPoint())
            val collisionRayBottom = ball.movementVector.move(ball.movementVector.scaleTo(ball.radius).rotate(-PI.toFloat()/2).getEndPoint())
            potentialCrossingPoint = baseFigure.crossingPointWith(collisionRayTop) ?: baseFigure.crossingPointWith(collisionRayBottom)
            if (potentialCrossingPoint!=null) return potentialCrossingPoint
        }
        return null
    }

    override fun scale(width: Int, height: Int) {
        baseFigure = baseFigure.scaleTo(width.toFloat(), height.toFloat())
    }

    override fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        baseFigure.draw(canvas, paint, color, strokeWidth)
    }
}