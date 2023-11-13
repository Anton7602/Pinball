package com.robkov.game.pinball.models.gamelevels

import android.graphics.Bitmap
import com.robkov.game.pinball.models.gameobjects.Flicker
import com.robkov.game.pinball.models.gameobjects.complexboundaries.IComplexBoundary
import com.robkov.game.pinball.models.gameobjects.complexboundaries.Rectangle
import com.robkov.game.pinball.models.gameobjects.complexboundaries.RoundedAngle
import com.robkov.game.pinball.models.gameobjects.complexboundaries.RoundedRectangle
import com.robkov.game.pinball.models.gameobjects.complexboundaries.Spring
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.Disk
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.IPrimitiveBoundary
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.Line
import com.robkov.game.pinball.models.geometry.Point
import com.robkov.game.pinball.models.geometry.Vector
import kotlin.random.Random

class PinballLevel1 : IPinballLevel {
    override var backgroundImage= Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888)
    override val complexBoundaries = mutableListOf<IComplexBoundary>()
    override val boundaries = mutableListOf<IPrimitiveBoundary>()
    override val startPos = Point(0.955f, Random.nextFloat()*0.1f+0.4f)
    override val flickers = mutableListOf<Flicker>()
    override var isScaled = false

    init
    {
        //ComplexBoundaries
        complexBoundaries.add(Rectangle(Point(0f, 1f),1f, 1.2f)) // Screen Border
        complexBoundaries.add(RoundedRectangle(1f, 0.5f, 0.219f, 0.981f, true, false, false, false )) // Top screen border curve right
        complexBoundaries.add(RoundedRectangle(1f, 0.5f, 0.019f, 0.781f, false, false, false, true )) // Top screen border curve right
        complexBoundaries.add(Spring(Point(0.93f,0.3f), 0.05f, 0.1f)) // Launching Spring
        complexBoundaries.add(RoundedRectangle(0.41f, 0.23f, 0.819f, 1.039f, false, false, false, true )) // Bottom exit from starting tube
        complexBoundaries.add(RoundedRectangle(0.65f, 0.45f, 0.818f, 1.039f, false, false, true, false )) // Top exit from starting tube
        complexBoundaries.add(RoundedRectangle(0.9f, 0.6f, 0.481f, 0.929f, true, false, false, false )) // First Curve Inner
        complexBoundaries.add(RoundedAngle(Point(0.705f, 0.7925f), 0.18f, Vector(-0.224f,0f), Vector(0f, 0.1075f)))
        complexBoundaries.add(RoundedRectangle(0.685f, 0.415f, 0.48f, 0.818f, true, false, false, false )) //First curve on right wall
        complexBoundaries.add(RoundedRectangle(0.9f, 0.685f, 0.481f, 0.819f, false, false, true, false )) // Second curve on right wall

        complexBoundaries.add(RoundedAngle(Point(0.019f, 0.68f), 0.08f, Vector(0.08f,0f), Vector(0f, 0.08f)))

        complexBoundaries.add(RoundedRectangle(0.6f, 0.4f, 0.019f, 0.343f, false, false, true, true )) // Middle left curve
        complexBoundaries.add(RoundedRectangle(0.68f, 0.52f, 0.019f, 0.181f, true, false, false, false )) // Top left curve

        complexBoundaries.add(RoundedAngle(Point(0.4f, 0.05f), 0.05f, Vector(-0.4f,0.1f), Vector(0f, -0.3f)))
        complexBoundaries.add(RoundedAngle(Point(0.6f, 0.05f), 0.05f, Vector(0.4f,0.1f), Vector(0f, -0.3f)))

        complexBoundaries.add(RoundedAngle(Point(0.181f, 0.230f), 0.1f, Vector(0.0f,0.17f), Vector(0.104f, -0.03f)))
        complexBoundaries.add(RoundedAngle(Point(0.819f, 0.230f), 0.1f, Vector(0.0f,0.09f), Vector(-0.104f, -0.03f)))


        //PrimitiveBoundaries
        boundaries.add(Line(Point(0.929f,0.2f), Point(0.929f, 0.41f))) // Left border of start tube lower
        boundaries.add(Line(Point(0.929f,0.45f), Point(0.929f, 0.75f))) //Left border of start tube top
        boundaries.add(Line(Point(0.981f,0.2f), Point(0.981f, 0.75f))) // Right Border of Starter Tube

        boundaries.add(Disk(Point(0.481f, 0.9f), 0.075f,200)) // Out of starting tube disk
        boundaries.add(Disk(Point(0.650f, 0.6f), 0.075f,100)) //Right wall disk
        boundaries.add(Disk(Point(0.162f, 0.5f), 0.075f,300)) // Left wall disk

        flickers.add(Flicker(Vector(Point(0.715f,0.200f), Point(0.515f, 0.200f))))
        flickers.add(Flicker(Vector(Point(0.285f,0.200f), Point(0.485f, 0.200f))))
    }

    override fun scale(width: Int, height: Int) {
        if (isScaled) return
        startPos.xPosition *= width
        startPos.yPosition *= height
        boundaries.forEach { boundary ->
            boundary.scale(width, height)
        }
        complexBoundaries.forEach { boundary ->
            boundary.scale(width, height)
            boundary.boundaries.forEach { primitiveBoundary ->
                boundaries.add(primitiveBoundary)
            }
        }
        flickers.forEach { flicker ->
            flicker.baseVector.originPoint.xPosition *=width
            flicker.baseVector.originPoint.yPosition *=height
            flicker.baseVector.xCoordinate *=width
        }
        isScaled = true
    }
}