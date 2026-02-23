package com.codemeric.musicplayer.ui.waveform

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private var progressRatio = 0f

    fun updateProgress(progress: Int, duration: Int) {
        progressRatio =
            if (duration == 0) 0f
            else progress.toFloat() / duration

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val barWidth = 20
        val spacing = 30

        var x = 0

        while (x < width) {

            val randomHeight =
                (20..height).random().toFloat()

            val top = centerY - randomHeight / 2
            val bottom = centerY + randomHeight / 2

            // Change color based on progress
            paint.color =
                if (x < width * progressRatio)
                    Color.GREEN
                else
                    Color.WHITE

            canvas.drawLine(
                x.toFloat(),
                top,
                x.toFloat(),
                bottom,
                paint
            )

            x += spacing
        }
    }
}