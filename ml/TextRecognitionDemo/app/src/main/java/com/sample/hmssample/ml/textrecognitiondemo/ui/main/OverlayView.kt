package com.sample.hmssample.ml.textrecognitiondemo.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.text.MLText


class OverlayView : View {

    companion object {
        private const val BOX_STROKE_WIDTH = 2.0f
        private val borderPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }
    }

    private var results: MLAnalyzer.Result<MLText.Block>? = null
    private var lensType = LensEngine.BACK_LENS
    private var cameraWidth = 1
    private var cameraHeight = 1

    constructor(context: Context?): super(context)

    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    fun setResults(results: MLAnalyzer.Result<MLText.Block>?) {
        this.results = results
        invalidate()
    }

    fun setLensType(lensType: Int) {
        this.lensType = lensType
    }

    fun setCameraWidth(width: Int) {
        cameraWidth = width
    }

    fun setCameraHeight(height: Int) {
        cameraHeight = height
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let { canvas ->
            drawResult(canvas, results)
        }
    }

    private fun drawResult(canvas: Canvas, results: MLAnalyzer.Result<MLText.Block>?) {
        val mlList: SparseArray<MLText.Block> = results?.analyseList ?: return

        mlList.forEach { key, value ->
            value.contents.forEach { content ->
                canvas.drawRect(
                    translateX(canvas, content.border.left.toFloat()),
                    translateY(canvas, content.border.top.toFloat()),
                    translateX(canvas, content.border.right.toFloat()),
                    translateY(canvas, content.border.bottom.toFloat()),
                    borderPaint)

                canvas.drawText(content.stringValue,
                    content.border.left.toFloat(),
                    content.border.bottom.toFloat(),
                    Paint().apply {
                        color = Color.BLUE
                        textSize = content.border.width().toFloat() / content.stringValue.encodeToByteArray().size * 2
                    }
                )
            }
        }
    }

    private fun translateX(canvas: Canvas, x: Float): Float {
        return if (lensType == LensEngine.FRONT_LENS) {
            width - x * canvas.width / cameraWidth
        } else {
            x * canvas.width / cameraWidth
        }
    }

    private fun translateY(canvas: Canvas, y: Float): Float {
        return y * canvas.height / cameraHeight
    }
}