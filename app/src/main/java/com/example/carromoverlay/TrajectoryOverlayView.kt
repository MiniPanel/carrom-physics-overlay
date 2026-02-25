package com.example.carromoverlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View

class TrajectoryOverlayView(context: Context) : View(context) {

    private val boardMargin = 48f
    private val markerRadius = 18f

    private val boardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(160, 30, 30, 30)
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val guidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(220, 0, 255, 180)
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }

    private val coinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(240, 255, 180, 0)
        style = Paint.Style.FILL
    }

    private val strikerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(255, 80, 170, 255)
        style = Paint.Style.FILL
    }

    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(255, 255, 90, 120)
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 36f
    }

    private var strikerPoint = Vec2(400f, 1100f)
    private var coinPoint = Vec2(540f, 700f)
    private var impactPoint = Vec2(520f, 740f)

    private enum class EditMode { STRIKER, COIN, IMPACT }

    private var mode = EditMode.STRIKER

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val boardRect = RectF(
            boardMargin,
            boardMargin * 2f,
            width - boardMargin,
            height - boardMargin * 2f
        )

        canvas.drawRect(boardRect, boardPaint)

        val trajectory = CarromPhysics.computeTrajectory(
            strikerStart = strikerPoint,
            coinCenter = coinPoint,
            aimPoint = impactPoint,
            board = Bounds(boardRect.left, boardRect.top, boardRect.right, boardRect.bottom)
        )

        canvas.drawLine(
            trajectory.strikerStart.x,
            trajectory.strikerStart.y,
            trajectory.impactPoint.x,
            trajectory.impactPoint.y,
            guidePaint
        )

        canvas.drawLine(
            trajectory.impactPoint.x,
            trajectory.impactPoint.y,
            trajectory.strikerExitEnd.x,
            trajectory.strikerExitEnd.y,
            guidePaint
        )

        canvas.drawLine(
            trajectory.coinStart.x,
            trajectory.coinStart.y,
            trajectory.coinEnd.x,
            trajectory.coinEnd.y,
            guidePaint
        )

        canvas.drawCircle(strikerPoint.x, strikerPoint.y, markerRadius, strikerPaint)
        canvas.drawCircle(coinPoint.x, coinPoint.y, markerRadius, coinPaint)
        canvas.drawCircle(impactPoint.x, impactPoint.y, markerRadius, targetPaint)

        canvas.drawText(
            "Tap to place: ${mode.name.lowercase()} (quick tap cycles mode)",
            boardMargin,
            boardMargin,
            textPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                setPoint(event.x, event.y)
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (event.eventTime - event.downTime < 120) {
                    mode = when (mode) {
                        EditMode.STRIKER -> EditMode.COIN
                        EditMode.COIN -> EditMode.IMPACT
                        EditMode.IMPACT -> EditMode.STRIKER
                    }
                }
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun setPoint(x: Float, y: Float) {
        when (mode) {
            EditMode.STRIKER -> strikerPoint = Vec2(x, y)
            EditMode.COIN -> coinPoint = Vec2(x, y)
            EditMode.IMPACT -> impactPoint = Vec2(x, y)
        }
    }
}
