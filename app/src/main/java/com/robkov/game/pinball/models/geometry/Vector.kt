package com.robkov.game.pinball.models.geometry

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Vector(var xCoordinate: Float, var yCoordinate: Float) {
    var originPoint = Point(0.0f, 0.0f)
    //Ax+By+C=0
    var A = yCoordinate
    var B = -xCoordinate
    var C = 0f
    //y=kx+d
    var k = yCoordinate/xCoordinate
    var d = 0f


    constructor(xValue: Float, yValue: Float, oPoint: Point) : this(xValue, yValue) {
        originPoint = oPoint
        initialize()
    }

    constructor(point1: Point, point2: Point) : this(point2.xPosition-point1.xPosition, point2.yPosition-point1.yPosition) {
        originPoint = point1
        initialize()
    }

    private fun initialize() {
        C = -yCoordinate*originPoint.xPosition+xCoordinate*originPoint.yPosition
        d = (xCoordinate*originPoint.yPosition-yCoordinate*originPoint.xPosition)/xCoordinate
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector) return false
        return xCoordinate == other.xCoordinate && yCoordinate == other.yCoordinate && originPoint==other.originPoint
    }

    override fun hashCode(): Int { return 31 * xCoordinate.hashCode()+ yCoordinate.hashCode()}
    override fun toString(): String { return "x: $xCoordinate, y: $yCoordinate, origin point: ${originPoint}, end point: ${getEndPoint()}, module: ${getLength()}"  }
    fun getEndPoint(): Point {return Point(originPoint.xPosition+xCoordinate, originPoint.yPosition+yCoordinate)  }
    fun getMiddlePoint(): Point { return Point((originPoint.xPosition+getEndPoint().xPosition)/2, (originPoint.yPosition+getEndPoint().yPosition)/2) }
    fun getLength(): Float {return sqrt((xCoordinate.pow(2) + yCoordinate.pow(2)))}
    fun scaleTo(length: Int): Vector {return multiplyBy(length/getLength())}
    fun scaleTo(length: Float): Vector {return multiplyBy(length/getLength())}
    fun scaleTo(width: Float, height: Float): Vector {return Vector(xCoordinate*width, yCoordinate*height, Point(originPoint.xPosition*width, originPoint.yPosition*height)) }
    fun reverse(): Vector { return Vector(-xCoordinate, -yCoordinate, originPoint) }
    fun flip(): Vector {return Vector(-xCoordinate, -yCoordinate, getEndPoint()) }
    fun move(point: Point): Vector {return Vector(xCoordinate, yCoordinate, point)
    }
    fun sqrt(): Vector { return Vector(sqrt(xCoordinate), sqrt(yCoordinate), originPoint) }
    fun pow(power: Int): Vector {return Vector(xCoordinate.pow(power), yCoordinate.pow(power), originPoint) }
    fun divideBy(number: Float) : Vector { return Vector(xCoordinate/number, yCoordinate/number, originPoint) }
    fun divideBy(number: Int) : Vector { return Vector(xCoordinate/number, yCoordinate/number, originPoint)  }
    fun multiplyBy(number: Float) : Vector { return Vector(xCoordinate*number,yCoordinate*number,originPoint) }
    fun multiplyBy(number: Int) : Vector { return Vector(xCoordinate*number,yCoordinate*number,originPoint) }
    fun multiplyBy(number: Long) : Vector { return Vector(xCoordinate*number,yCoordinate*number,originPoint) }
    fun scalarMultiplyBy(vector: Vector): Float {return xCoordinate*vector.xCoordinate+yCoordinate*vector.yCoordinate }
    fun vectorMyltuplyBy(vector: Vector): Float {return xCoordinate*vector.yCoordinate-vector.xCoordinate*yCoordinate}
    fun addVector(vector: Vector): Vector { return Vector(xCoordinate+vector.xCoordinate, yCoordinate+vector.yCoordinate,originPoint) }
    fun angleToVector(vector: Vector): Float { return acos(scalarMultiplyBy(vector)/(getLength()*vector.getLength())) }
    fun rotate(angleRadians: Float): Vector { return Vector(xCoordinate* cos(angleRadians) -yCoordinate* sin(angleRadians),yCoordinate* cos(angleRadians) +xCoordinate* sin(angleRadians), originPoint) }
    fun orthoVectorToPoint(point: Point): Vector { return if(rotate(PI.toFloat()/2).getEndPoint().distanceToPoint(point)<rotate(-PI.toFloat()/2).getEndPoint().distanceToPoint(point)) rotate(PI.toFloat()/2) else rotate(-PI.toFloat()/2)   }
    fun distanceToPoint(point: Point): Float { return abs(A*point.xPosition+B*point.yPosition+C)/(sqrt(A.pow(2)+B.pow(2))) }
    fun isContainsPoint(point: Point): Boolean { return abs(getLength()-(point.distanceToPoint(originPoint)+point.distanceToPoint(getEndPoint())))<0.1}
    fun isCrossesVector(vector: Vector): Boolean { return vectorMyltuplyBy(Vector(originPoint,vector.originPoint))*vectorMyltuplyBy(
        Vector(originPoint, vector.getEndPoint())
    )<0 }
    fun isCrossesCircle (circle: Circle): Boolean { return (distanceToPoint(circle.center)<=circle.radius)}
    fun crossingPointWith(vector: Vector): Point? {
        val x: Float
        val y: Float
        if (A==0f) {
            if (B==0f) { return null }
            y=-C/B
            if (vector.A ==0f) { return null }
            x=(-vector.C-vector.B*y)/vector.A
        } else if (B==0f) {
            x=-C/A
            if (vector.B==0f) {return null}
            y=(-vector.C-vector.A*x)/vector.B
        } else if (vector.A==0f) {
            if (vector.B==0f) {return null}
            y=-vector.C/vector.B
            x=(-C-B*y)/A
        }
        else if (vector.B==0f) {
            x=-vector.C/vector.A
            y=(-C-A*x)/B
        }
        else {
            y = (vector.A*C/A-vector.C)/(vector.B-vector.A*B/A)
            x = (-C-B*y)/A
        }
        val potentialCrossingPoint = Point(x,y)
        if (isContainsPoint(potentialCrossingPoint) && vector.isContainsPoint(potentialCrossingPoint)) {
            return potentialCrossingPoint
        }
        return null
    }

    //Returns crossing point with circle. If average is false - returns closest point, if average is true - returns middle point between collision points
    fun crossingPointWith(circle: Circle, average: Boolean): Point? {
        val vector = Vector(xCoordinate, yCoordinate, Point(originPoint.xPosition-circle.center.xPosition, originPoint.yPosition-circle.center.yPosition))
        val x0 = -vector.A * vector.C / (vector.A.pow(2) + vector.B.pow(2))
        val y0 = -vector.B * vector.C / (vector.A.pow(2)+ vector.B.pow(2))
        var collisionPoint: Point
        if (vector.C.pow(2) > circle.radius.pow(2) * (vector.A.pow(2) + vector.B.pow(2)) + 0.01) {
            return null
        } else if (abs(vector.C.pow(2) - circle.radius.pow(2) * (vector.A.pow(2) + vector.B.pow(2))) < 0.01) {
            collisionPoint = Point(x0+circle.center.xPosition, y0+circle.center.yPosition)
            return if (isContainsPoint(collisionPoint)) collisionPoint else null
        } else {
            val d = circle.radius.pow(2) - vector.C.pow(2) / (vector.A.pow(2) + vector.B.pow(2))
            val mult = sqrt(d / (vector.A.pow(2) + vector.B.pow(2)))
            val ax = x0 + vector.B * mult
            val bx = x0 - vector.B * mult
            val ay = y0 - vector.A * mult
            val by = y0 + vector.A * mult
            if (average) {
                collisionPoint = Vector(Point(ax,ay), Point(bx,by)).getMiddlePoint()
                if (isContainsPoint(collisionPoint)) {
                    return collisionPoint
                }
            }
            collisionPoint = if (vector.originPoint.distanceToPoint(Point(ax,ay))<vector.originPoint.distanceToPoint(
                    Point(bx,by)
                )) Point(ax+circle.center.xPosition, ay+circle.center.yPosition)
            else Point(bx+circle.center.xPosition,by+circle.center.yPosition)
            return if (isContainsPoint(collisionPoint)) collisionPoint else null
        }
    }

    fun crossingPointWith(arc: Arc, average: Boolean): Point? {
        val crossingPoint = crossingPointWith(Circle(arc.center, arc.radius), average) ?: return null
        val baseVector = Vector(arc.center, crossingPoint)
        return if (arc.midVector.angleToVector(baseVector)<arc.midVector.angleToVector(arc.endVector)) crossingPoint else null
    }

    fun draw(canvas: Canvas, paint: Paint, color: Int, strokeWidth: Float) {
        paint.color = color
        paint.style = Paint.Style.FILL
        paint.strokeWidth = strokeWidth
        canvas.drawLine(originPoint.xPosition, canvas.height-originPoint.yPosition,
            getEndPoint().xPosition, canvas.height-getEndPoint().yPosition, paint)
    }
}