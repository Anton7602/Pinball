package com.robkov.game.pinball.views

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.robkov.game.pinball.PinballScoreListener
import com.robkov.game.pinball.R
import com.robkov.game.pinball.models.gamelevels.PinballLevel1
import com.robkov.game.pinball.models.gameobjects.Ball
import com.robkov.game.pinball.models.gameobjects.Flicker
import com.robkov.game.pinball.models.geometry.Point
import com.robkov.game.pinball.models.geometry.Vector
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin


class PinballTableView(context: Context, attrs: AttributeSet) : View(context, attrs), View.OnTouchListener {
    private val framesPerSecond = 60
    private val frameTimeSeconds = 1/framesPerSecond.toFloat()
    private val frameTimeMilliseconds = 1000/framesPerSecond.toLong() // milliseconds per frame
    private var gameThread = Thread()
    private val tableAngle = 5// degrees. 0 - table is horizontal, 90 - table is vertical
    private var tableBordersRect= Rect(0, 0, width, height)
    private val gravityVector = Vector(0f,-1f).multiplyBy(9810f* abs(sin(tableAngle* PI/180).toFloat()))
    private var activeBall = Ball(Point(4000f,2000f),30)
    private var level = PinballLevel1()
    private var paint = Paint()
    private var scoreListener: PinballScoreListener? = null
    private var score = 0
    var isGameActive: Boolean = false

    init {
        setOnTouchListener(this)
        level.backgroundImage = BitmapFactory.decodeResource(resources, R.drawable.background1)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val x = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startGame()
                if (x < width / 2) {
                    flickFlicker(level.flickers[1], true)
                } else  {
                    flickFlicker(level.flickers[0], true)
                }
            }
            MotionEvent.ACTION_UP -> {
                level.flickers.forEach { flicker ->
                    flickFlicker(flicker, false)
                }
            }
        }
        return true
    }

    private fun adjustLevelToScreenSize() {
        tableBordersRect = Rect(0, 0, width, height)
        level.scale(width, height)
        activeBall.radius=(0.02*width).toInt()
        resetBall()
    }

    override fun onDraw(canvas: Canvas) {
        if (!isGameActive) {
            adjustLevelToScreenSize()
            resetBall()
        }
        //Drawing background
        canvas.drawBitmap(level.backgroundImage, null, tableBordersRect, null)
        //Boundaries
        //level.boundaries.forEach{ boundary -> boundary.draw(canvas, paint, Color.BLACK, 7f) }
        //Flickers
        level.flickers.forEach{ flicker -> flicker.draw(canvas, paint, Color.BLUE, 25f) }
        //Ball
        activeBall.draw(canvas, paint, Color.BLUE)
        //activeBall.drawBallParameters(canvas, paint)
        //Calling redraw
        postInvalidateDelayed(frameTimeMilliseconds)
    }

    private fun updateView() {
        //Ball
        activeBall.appliedForces.clear()
        activeBall.appliedForces.add(gravityVector.multiplyBy(activeBall.mass))
        val potentialBallPositions = mutableListOf<Ball>()
        //Boundaries
        level.boundaries.forEach { boundary ->
            if (boundary.isCollidingWith(activeBall)) {
                val potentialBall = boundary.collideWith(activeBall)
                if (potentialBall!=null) {
                    potentialBallPositions.add(potentialBall)
                    score += boundary.score
                    scoreListener?.onScoreChange(score)
                }
            }
        }
        //Flickers
        level.flickers.forEach { flicker ->
            flicker.appliedForces.clear()
            flicker.appliedForces.add(gravityVector)
            flicker.rotate(frameTimeSeconds)
            if (flicker.isCollidingWith(activeBall)) {
                val potentialBall = flicker.collideWith(activeBall)
                if (potentialBall!=null) {
                    potentialBallPositions.add(potentialBall)
                }
            }
        }
        if (potentialBallPositions.size>0) {
            var closestBallPosition = activeBall
            var minDistanceCovered = Float.MAX_VALUE
            potentialBallPositions.forEach { ball ->
                if (ball.center.distanceToPoint(activeBall.movementVector.originPoint)<minDistanceCovered) {
                    minDistanceCovered = ball.center.distanceToPoint(activeBall.movementVector.originPoint)
                    closestBallPosition = ball
                }
            }
            activeBall = closestBallPosition
        }
        activeBall.move(frameTimeSeconds)
        if (activeBall.center.yPosition+activeBall.radius < 0) {
            endGame()
        }
    }

    private fun startGame() {
        if (!isGameActive) {
            score = 0
            scoreListener?.onScoreChange(0)
            resetGame()
            resetBall()
            isGameActive = true
            gameThread.start()
        }
    }

    private fun endGame() {
        isGameActive = false
    }

    private fun resetGame() {
        gameThread = Thread{
            while (isGameActive) {
                updateView()
                Thread.sleep(frameTimeMilliseconds)
            }
        }
    }

    private fun resetBall() {
        activeBall.accelerationVector = Vector(0f,0f)
        activeBall.velocityVector = Vector(0f, 0f)
        activeBall.center = level.startPos
    }


    private fun flickFlicker(flicker: Flicker, state:Boolean) {
        flicker.reverseRotation = state
    }

    fun setScoreListener(listener: PinballScoreListener) {
        scoreListener = listener
    }
}