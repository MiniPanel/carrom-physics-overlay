package com.example.carromoverlay

import org.junit.Assert.assertTrue
import org.junit.Test

class CarromPhysicsTest {

    @Test
    fun `coin trajectory reaches board boundary`() {
        val board = Bounds(0f, 0f, 1000f, 1000f)
        val trajectory = CarromPhysics.computeTrajectory(
            strikerStart = Vec2(500f, 900f),
            coinCenter = Vec2(500f, 500f),
            aimPoint = Vec2(500f, 540f),
            board = board
        )

        val onBoundary = trajectory.coinEnd.x == board.left ||
            trajectory.coinEnd.x == board.right ||
            trajectory.coinEnd.y == board.top ||
            trajectory.coinEnd.y == board.bottom

        assertTrue(onBoundary)
    }
}
