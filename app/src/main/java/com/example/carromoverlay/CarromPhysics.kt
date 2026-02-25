package com.example.carromoverlay

import kotlin.math.abs
import kotlin.math.sqrt

data class Vec2(val x: Float, val y: Float)

data class Bounds(val left: Float, val top: Float, val right: Float, val bottom: Float)

data class Trajectory(
    val strikerStart: Vec2,
    val impactPoint: Vec2,
    val strikerExitEnd: Vec2,
    val coinStart: Vec2,
    val coinEnd: Vec2
)

object CarromPhysics {

    fun computeTrajectory(
        strikerStart: Vec2,
        coinCenter: Vec2,
        aimPoint: Vec2,
        board: Bounds
    ): Trajectory {
        val strikerDir = normalize(aimPoint - strikerStart)
        val collisionNormal = normalize(coinCenter - aimPoint)

        val coinDirection = collisionNormal
        val strikerExitDirection = normalize(strikerDir - (collisionNormal * dot(strikerDir, collisionNormal)))

        val impactPoint = aimPoint
        val coinEnd = rayToRectEdge(coinCenter, coinDirection, board)
        val strikerExitEnd = rayToRectEdge(impactPoint, strikerExitDirection, board)

        return Trajectory(strikerStart, impactPoint, strikerExitEnd, coinCenter, coinEnd)
    }

    private fun rayToRectEdge(start: Vec2, direction: Vec2, bounds: Bounds): Vec2 {
        if (direction.x == 0f && direction.y == 0f) return start

        var tMin = Float.POSITIVE_INFINITY

        fun testT(t: Float, axisValue: Float, within: Boolean) {
            if (t > 0f && t < tMin && within && axisValue.isFinite()) {
                tMin = t
            }
        }

        if (abs(direction.x) > 1e-6f) {
            val tLeft = (bounds.left - start.x) / direction.x
            val yLeft = start.y + tLeft * direction.y
            testT(tLeft, yLeft, yLeft in bounds.top..bounds.bottom)

            val tRight = (bounds.right - start.x) / direction.x
            val yRight = start.y + tRight * direction.y
            testT(tRight, yRight, yRight in bounds.top..bounds.bottom)
        }

        if (abs(direction.y) > 1e-6f) {
            val tTop = (bounds.top - start.y) / direction.y
            val xTop = start.x + tTop * direction.x
            testT(tTop, xTop, xTop in bounds.left..bounds.right)

            val tBottom = (bounds.bottom - start.y) / direction.y
            val xBottom = start.x + tBottom * direction.x
            testT(tBottom, xBottom, xBottom in bounds.left..bounds.right)
        }

        return if (tMin.isFinite()) {
            Vec2(start.x + direction.x * tMin, start.y + direction.y * tMin)
        } else {
            start
        }
    }

    private fun dot(a: Vec2, b: Vec2): Float = a.x * b.x + a.y * b.y

    private fun normalize(v: Vec2): Vec2 {
        val mag = sqrt((v.x * v.x + v.y * v.y).toDouble()).toFloat()
        return if (mag < 1e-6f) Vec2(0f, 0f) else Vec2(v.x / mag, v.y / mag)
    }

    private operator fun Vec2.minus(other: Vec2): Vec2 = Vec2(x - other.x, y - other.y)
    private operator fun Vec2.times(scale: Float): Vec2 = Vec2(x * scale, y * scale)
}
