package com.robkov.game.pinball.models.gamelevels

import android.graphics.Bitmap
import com.robkov.game.pinball.models.gameobjects.Flicker
import com.robkov.game.pinball.models.gameobjects.complexboundaries.IComplexBoundary
import com.robkov.game.pinball.models.gameobjects.primitiveboundaries.IPrimitiveBoundary
import com.robkov.game.pinball.models.geometry.Point

interface IPinballLevel {
    val complexBoundaries: MutableList<IComplexBoundary>
    val boundaries: MutableList<IPrimitiveBoundary>
    val startPos: Point
    val flickers: MutableList<Flicker>
    val backgroundImage: Bitmap
    var isScaled: Boolean

    fun scale(width: Int, height: Int)
}