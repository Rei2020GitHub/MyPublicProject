package com.sample.hmssample.ml.handkeypointdetectiondemo.ui.main

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoint
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints
import com.sample.hmssample.ml.handkeypointdetectiondemo.model.HandKeyPointData
import com.sample.hmssample.ml.handkeypointdetectiondemo.model.SimilarityUtil
import com.sample.hmssample.ml.handkeypointdetectiondemo.model.getHandKeypointPair


class OverlayView : View {

    companion object {
        private val linePaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 8.0f
        }
        private val pointPaint = Paint().apply {
            color = Color.GREEN
        }
        private val rectPaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 4.0f
        }
        private val comparePaint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 2.0f
            textSize = 50.0f
        }
    }

    private var results: MLAnalyzer.Result<MLHandKeypoints>? = null
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

    fun setResults(results: MLAnalyzer.Result<MLHandKeypoints>?) {
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
            drawFaceResult(canvas, results)
        }
    }

    private fun drawFaceResult(canvas: Canvas, results: MLAnalyzer.Result<MLHandKeypoints>?) {
        val mlList: SparseArray<MLHandKeypoints> = results?.analyseList ?: return

        mlList.forEach { key, value ->
            val wrist = value.getHandKeypoint(MLHandKeypoint.TYPE_WRIST)
            val thumb1 = value.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_FIRST)
            val thumb2 = value.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_SECOND)
            val thumb3 = value.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_THIRD)
            val thumb4 = value.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_FOURTH)
            val forefinger1 = value.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_FIRST)
            val forefinger2 = value.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_SECOND)
            val forefinger3 = value.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_THIRD)
            val forefinger4 = value.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_FOURTH)
            val middleFinger1 = value.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_FIRST)
            val middleFinger2 = value.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_SECOND)
            val middleFinger3 = value.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_THIRD)
            val middleFinger4 = value.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_FOURTH)
            val ringFinger1 = value.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_FIRST)
            val ringFinger2 = value.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_SECOND)
            val ringFinger3 = value.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_THIRD)
            val ringFinger4 = value.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_FOURTH)
            val littleFinger1 = value.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_FIRST)
            val littleFinger2 = value.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_SECOND)
            val littleFinger3 = value.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_THIRD)
            val littleFinger4 = value.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_FOURTH)

            wrist?.let { point1 ->
                thumb1?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            thumb1?.let { point1 ->
                thumb2?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            thumb2?.let { point1 ->
                thumb3?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            thumb3?.let { point1 ->
                thumb4?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            wrist?.let { point1 ->
                forefinger1?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            forefinger1?.let { point1 ->
                forefinger2?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            forefinger2?.let { point1 ->
                forefinger3?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            forefinger3?.let { point1 ->
                forefinger4?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            wrist?.let { point1 ->
                middleFinger1?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            middleFinger1?.let { point1 ->
                middleFinger2?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            middleFinger2?.let { point1 ->
                middleFinger3?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            middleFinger3?.let { point1 ->
                middleFinger4?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            wrist?.let { point1 ->
                ringFinger1?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            ringFinger1?.let { point1 ->
                ringFinger2?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            ringFinger2?.let { point1 ->
                ringFinger3?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            ringFinger3?.let { point1 ->
                ringFinger4?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            wrist?.let { point1 ->
                littleFinger1?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            littleFinger1?.let { point1 ->
                littleFinger2?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            littleFinger2?.let { point1 ->
                littleFinger3?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            littleFinger3?.let { point1 ->
                littleFinger4?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                    }
                }
            }

            value.handKeypoints.forEach { point ->
                if (point.score > 0) {
                    canvas.drawCircle(
                        translateX(canvas, point.pointX),
                        translateY(canvas, point.pointY),
                        10.0f,
                        pointPaint
                    )
                }
            }

            value.rect?.let { rect ->
                canvas.drawRect(
                        Rect(translateX(canvas, rect.left.toFloat()).toInt(),
                                translateY(canvas, rect.top.toFloat()).toInt(),
                                translateX(canvas, rect.right.toFloat()).toInt(),
                                translateY(canvas, rect.bottom.toFloat()).toInt()),
                        rectPaint)

                val x = translateX(canvas, if (lensType == LensEngine.FRONT_LENS) rect.right.toFloat() else rect.left.toFloat())
                var y = translateY(canvas, rect.top.toFloat()) - 5.0f

                val handKeypointPair = value.getHandKeypointPair()
                val rockSimilarity = SimilarityUtil.compare(handKeypointPair.first, HandKeyPointData.rock.first).multiply(SimilarityUtil.compare(handKeypointPair.second, HandKeyPointData.rock.second))
                val scissorsSimilarity = SimilarityUtil.compare(handKeypointPair.first, HandKeyPointData.scissors.first).multiply(SimilarityUtil.compare(handKeypointPair.second, HandKeyPointData.scissors.second))
                val paperSimilarity = SimilarityUtil.compare(handKeypointPair.first, HandKeyPointData.paper.first).multiply(SimilarityUtil.compare(handKeypointPair.second, HandKeyPointData.paper.second))

                val map: MutableMap<String, Point> = mutableMapOf()
                map[String.format("グー:%.3f", rockSimilarity)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())
                map[String.format("チョキ:%.3f", scissorsSimilarity)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())
                map[String.format("パー:%.3f", paperSimilarity)] = Point(x.toInt(), y.apply { y -= 50.0f }.toInt())

                map.forEach { (string, point) ->
                    canvas.drawText(string,
                            point.x.toFloat(),
                            point.y.toFloat(),
                            comparePaint)
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