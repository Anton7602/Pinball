package com.robkov.game.pinball.models.gameobjects

import com.robkov.game.pinball.models.geometry.Point
import com.robkov.game.pinball.models.geometry.Vector
import kotlin.math.abs

abstract class RotatingObject(mass: Float, center: Point, axisPoint: Point): MovingObject(mass, center) {
    var baseVector = Vector(axisPoint, center).multiplyBy(2)
    var angularAcceleration =0f
    var angularVelocity = 0f

    constructor(mass: Float, vector: Vector): this(mass, vector.getMiddlePoint(), vector.originPoint) {
        baseVector = vector
    }

    open fun rotate(frameTimeSeconds: Float) {
        forceVector = calculateForceVector()
        accelerationVector = forceVector
        accelerationVector = accelerationVector.divideBy(mass)
        angularAcceleration = (accelerationVector.getLength()/baseVector.originPoint.distanceToPoint(center))*rotationDirection()
        angularVelocity += angularAcceleration * frameTimeSeconds
        baseVector = baseVector.rotate(angularVelocity*frameTimeSeconds)
    }

    //Through vector multiplication returns 1 if clockwise and -1 if counterclockwise
    private fun rotationDirection(): Int {
        val vector = baseVector.addVector(accelerationVector)
        val direction = baseVector.vectorMyltuplyBy(vector)
        //val direction = baseVector.xCoordinate*vector.yCoordinate-baseVector.yCoordinate*vector.xCoordinate
        return (direction/abs(direction)).toInt()
    }

    fun getLinearVelocityVector(point: Point): Vector {
    return baseVector.orthoVectorToPoint(baseVector.rotate(angularVelocity/abs(angularVelocity)).getMiddlePoint())
        .scaleTo(abs(angularVelocity)* Vector(baseVector.originPoint, point).getLength()).move(point)
    }
}