package com.robkov.game.pinball.models.gameobjects

import com.robkov.game.pinball.models.geometry.Point
import com.robkov.game.pinball.models.geometry.Vector

abstract class MovingObject(var mass: Float, var center: Point) {
    var maxSpeed = 3000f
    var restitution = 0.9f //value between zero and one. Measure of elasticity in collision
        set(newValue) {
            if (newValue>1 || newValue<0) { throw IllegalArgumentException("Restitution value of moving object must be between 0 and 1") }
            field = newValue
        }
    var friction = 0f //
    var movementVector: Vector = Vector(0f, 0f, center)
    var velocityVector = Vector(0f, 0f, center)
    var accelerationVector = Vector(0f, 0f, center)
    var forceVector = Vector(0f, 0f, center)
    var appliedForces = mutableListOf<Vector>()

    open fun move(frameTimeSeconds: Float) {
        forceVector = calculateForceVector()
        accelerationVector = forceVector.divideBy(mass)
        if (velocityVector.getLength()>maxSpeed) {
            velocityVector.scaleTo(maxSpeed)
        }
        velocityVector = accelerationVector.multiplyBy(frameTimeSeconds).addVector(velocityVector)
        if (velocityVector.getLength()>maxSpeed) {velocityVector = velocityVector.scaleTo(maxSpeed)}
        movementVector = velocityVector.multiplyBy(frameTimeSeconds)
        moveTo(movementVector.getEndPoint())
    }

    open fun moveTo(point: Point) { center = point }

    protected fun calculateForceVector() : Vector {
        var tempVector = Vector(0f,0f, center)
        appliedForces.forEach { vector -> tempVector = tempVector.addVector(vector) }
        return tempVector
    }
}