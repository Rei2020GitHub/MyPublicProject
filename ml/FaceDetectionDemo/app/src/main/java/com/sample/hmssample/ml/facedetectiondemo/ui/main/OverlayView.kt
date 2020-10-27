package com.sample.hmssample.ml.facedetectiondemo.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.face.MLFace
import com.huawei.hms.mlsdk.face.MLFaceShape


class OverlayView : View {

    companion object {
        private const val BOX_STROKE_WIDTH = 6.0f
        private val borderPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }

        private val facePaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }

        private val eyePaint = Paint().apply {
            color = Color.rgb(128, 0, 128)
            style = Paint.Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }

        private val eyebrowPaint = Paint().apply {
            color = Color.rgb(255, 0, 255)
            style = Paint.Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }

        private val lipPaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }

        private val nosePaint = Paint().apply {
            color = Color.YELLOW
            style = Paint.Style.STROKE
            strokeWidth = BOX_STROKE_WIDTH
        }

        private val emotionsPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2.0f
            textSize = 50.0f
        }

        private val faceFeaturePaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2.0f
            textSize = 50.0f
        }

    }

    private var faceResults: MLAnalyzer.Result<MLFace>? = null
    private var lensType = LensEngine.FRONT_LENS
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

    fun setFaceResults(results: MLAnalyzer.Result<MLFace>?) {
        faceResults = results
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
            drawFaceResult(canvas, faceResults)
        }
    }

    private fun drawFaceResult(canvas: Canvas, results: MLAnalyzer.Result<MLFace>?) {
        val mlFaceList: SparseArray<MLFace> = results?.analyseList ?: return

        mlFaceList.forEach { key, value ->
//            value.faceShapeList.forEach { mlFaceShape ->
//                mlFaceShape.points.forEach { point ->
//                    canvas.drawPoint(translateX(canvas, point.x), translateY(canvas, point.y), pointPaint)
//                }
//            }

            value.border?.let { border ->
                canvas.drawRect(
                    translateX(canvas, border.left.toFloat()),
                    translateY(canvas, border.top.toFloat()),
                    translateX(canvas, border.right.toFloat()),
                    translateY(canvas, border.bottom.toFloat()),
                    borderPaint)

                val x = translateX(canvas, if (lensType == LensEngine.FRONT_LENS) border.right.toFloat() else border.left.toFloat())
                var y = translateY(canvas, border.top.toFloat()) - 5.0f

                value.emotions?.let { mlFaceEmotion ->
                    val map: MutableMap<String, Point> = mutableMapOf()
                    map[String.format("怒る:%.3f", mlFaceEmotion.angryProbability)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())
                    map[String.format("嫌がる:%.3f", mlFaceEmotion.disgustProbability)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())
                    map[String.format("怖がる:%.3f", mlFaceEmotion.fearProbability)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())
                    map[String.format("無表情:%.3f", mlFaceEmotion.neutralProbability)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())
                    map[String.format("悲しむ:%.3f", mlFaceEmotion.sadProbability)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())
                    map[String.format("笑顔:%.3f", mlFaceEmotion.smilingProbability)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())
                    map[String.format("驚く:%.3f", mlFaceEmotion.surpriseProbability)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())

                    map.forEach { (string, point) ->
                        canvas.drawText(string,
                            point.x.toFloat(),
                            point.y.toFloat(),
                            emotionsPaint)
                    }
                }

                value.features?.let { mlFaceFeature ->
                    if (mlFaceFeature.age >= 0) {
                        canvas.drawText(String.format("年齢:%d", mlFaceFeature.age),
                            x.toFloat(),
                            y.apply { y -= 50.0f },
                            faceFeaturePaint)
                    }
                    if (mlFaceFeature.hatProbability >= 0.0f) {
                        canvas.drawText(String.format("帽子:%.3f", mlFaceFeature.hatProbability),
                            x.toFloat(),
                            y.apply { y -= 50.0f },
                            faceFeaturePaint)
                    }
                    if (mlFaceFeature.leftEyeOpenProbability >= 0.0f) {
                        canvas.drawText(String.format("左目:%.3f", mlFaceFeature.leftEyeOpenProbability),
                            x.toFloat(),
                            y.apply { y -= 50.0f },
                            faceFeaturePaint)
                    }
                    if (mlFaceFeature.rightEyeOpenProbability >= 0.0f) {
                        canvas.drawText(String.format("右目:%.3f", mlFaceFeature.rightEyeOpenProbability),
                            x.toFloat(),
                            y.apply { y -= 50.0f },
                            faceFeaturePaint)
                    }
                    if (mlFaceFeature.moustacheProbability >= 0.0f) {
                        canvas.drawText(String.format("ひげ:%.3f", mlFaceFeature.moustacheProbability),
                            x.toFloat(),
                            y.apply { y -= 50.0f },
                            faceFeaturePaint)
                    }
                    if (mlFaceFeature.sexProbability >= 0.0f) {
                        canvas.drawText(String.format("性別:%s、%.3f", if (mlFaceFeature.moustacheProbability <= 0.5f) "男" else "女", mlFaceFeature.moustacheProbability),
                            x.toFloat(),
                            y.apply { y -= 50.0f },
                            faceFeaturePaint)
                    }
                    if (mlFaceFeature.sunGlassProbability >= 0.0f) {
                        canvas.drawText(String.format("メガネ:%.3f", mlFaceFeature.sunGlassProbability),
                            x.toFloat(),
                            y.apply { y -= 50.0f },
                            faceFeaturePaint)
                    }
                }
            }

            value.faceShapeList.filter {
                it.faceShapeType == MLFaceShape.TYPE_FACE
            }.forEach { mlFaceShape ->
                mlFaceShape.points.forEach { point ->
                    canvas.drawPoint(translateX(canvas, point.x), translateY(canvas, point.y), facePaint)
                }
            }

            value.faceShapeList.filter {
                it.faceShapeType == MLFaceShape.TYPE_LEFT_EYE
                        || it.faceShapeType == MLFaceShape.TYPE_RIGHT_EYE
            }.forEach { mlFaceShape ->
                mlFaceShape.points.forEach { point ->
                    canvas.drawPoint(translateX(canvas, point.x), translateY(canvas, point.y), eyePaint)
                }
            }

            value.faceShapeList.filter {
                it.faceShapeType == MLFaceShape.TYPE_TOP_OF_LEFT_EYEBROW
                        || it.faceShapeType == MLFaceShape.TYPE_BOTTOM_OF_LEFT_EYEBROW
                        || it.faceShapeType == MLFaceShape.TYPE_TOP_OF_RIGHT_EYEBROW
                        || it.faceShapeType == MLFaceShape.TYPE_BOTTOM_OF_RIGHT_EYEBROW
            }.forEach { mlFaceShape ->
                mlFaceShape.points.forEach { point ->
                    canvas.drawPoint(translateX(canvas, point.x), translateY(canvas, point.y), eyebrowPaint)
                }
            }

            value.faceShapeList.filter {
                it.faceShapeType == MLFaceShape.TYPE_TOP_OF_UPPER_LIP
                        || it.faceShapeType == MLFaceShape.TYPE_BOTTOM_OF_UPPER_LIP
                        || it.faceShapeType == MLFaceShape.TYPE_TOP_OF_LOWER_LIP
                        || it.faceShapeType == MLFaceShape.TYPE_BOTTOM_OF_LOWER_LIP
            }.forEach { mlFaceShape ->
                mlFaceShape.points.forEach { point ->
                    canvas.drawPoint(translateX(canvas, point.x), translateY(canvas, point.y), lipPaint)
                }
            }

            value.faceShapeList.filter {
                it.faceShapeType == MLFaceShape.TYPE_BRIDGE_OF_NOSE
                        || it.faceShapeType == MLFaceShape.TYPE_BOTTOM_OF_NOSE
            }.forEach { mlFaceShape ->
                mlFaceShape.points.forEach { point ->
                    canvas.drawPoint(translateX(canvas, point.x), translateY(canvas, point.y), nosePaint)
                }
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