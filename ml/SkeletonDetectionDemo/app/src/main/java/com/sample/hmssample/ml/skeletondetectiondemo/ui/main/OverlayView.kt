package com.sample.hmssample.ml.skeletondetectiondemo.ui.main

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
import com.huawei.hms.mlsdk.skeleton.MLJoint
import com.huawei.hms.mlsdk.skeleton.MLSkeleton


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
    }

    private var skeletonResults: MLAnalyzer.Result<MLSkeleton>? = null
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

    fun setSkeletonResults(results: MLAnalyzer.Result<MLSkeleton>?) {
        skeletonResults = results
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
            drawSkeletonResult(canvas, skeletonResults)
        }
    }

    private fun drawSkeletonResult(canvas: Canvas, results: MLAnalyzer.Result<MLSkeleton>?) {
        val sparseArray: SparseArray<MLSkeleton> = results?.analyseList ?: return

        sparseArray.forEach { key, value ->
            val headTop = value.getJointPoint(MLJoint.TYPE_HEAD_TOP)
            val neck = value.getJointPoint(MLJoint.TYPE_NECK)
            val leftShoulder = value.getJointPoint(MLJoint.TYPE_LEFT_SHOULDER)
            val rightShoulder = value.getJointPoint(MLJoint.TYPE_RIGHT_SHOULDER)
            val leftElbow = value.getJointPoint(MLJoint.TYPE_LEFT_ELBOW)
            val rightElbow = value.getJointPoint(MLJoint.TYPE_RIGHT_ELBOW)
            val leftWrist = value.getJointPoint(MLJoint.TYPE_LEFT_WRIST)
            val rightWrist = value.getJointPoint(MLJoint.TYPE_RIGHT_WRIST)
            val leftHip = value.getJointPoint(MLJoint.TYPE_LEFT_HIP)
            val rightHip = value.getJointPoint(MLJoint.TYPE_RIGHT_HIP)
            val leftKnee = value.getJointPoint(MLJoint.TYPE_LEFT_KNEE)
            val rightKnee = value.getJointPoint(MLJoint.TYPE_RIGHT_KNEE)
            val leftAnkle = value.getJointPoint(MLJoint.TYPE_LEFT_ANKLE)
            val rightAnkle = value.getJointPoint(MLJoint.TYPE_RIGHT_ANKLE)

            headTop?.let { point1 ->
                neck?.let { point2 ->
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

            neck?.let { point1 ->
                leftShoulder?.let { point2 ->
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

            neck?.let { point1 ->
                rightShoulder?.let { point2 ->
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

            leftShoulder?.let { point1 ->
                leftElbow?.let { point2 ->
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

            rightShoulder?.let { point1 ->
                rightElbow?.let { point2 ->
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

            leftElbow?.let { point1 ->
                leftWrist?.let { point2 ->
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

            rightElbow?.let { point1 ->
                rightWrist?.let { point2 ->
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

            leftHip?.let { point1 ->
                rightHip?.let { point2 ->
                    if (point1.score > 0 && point2.score > 0) {
                        canvas.drawLine(
                            translateX(canvas, point1.pointX),
                            translateY(canvas, point1.pointY),
                            translateX(canvas, point2.pointX),
                            translateY(canvas, point2.pointY),
                            linePaint
                        )
                        neck?.let { point3 ->
                            if (point3.score > 0) {
                                canvas.drawLine(
                                    translateX(canvas, (point1.pointX + point2.pointX) / 2),
                                    translateY(canvas, (point1.pointY + point2.pointY) / 2),
                                    translateX(canvas, point3.pointX),
                                    translateY(canvas, point3.pointY),
                                    linePaint
                                )
                            }
                        }
                    }
                }
            }

            leftHip?.let { point1 ->
                leftKnee?.let { point2 ->
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

            rightHip?.let { point1 ->
                rightKnee?.let { point2 ->
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

            leftKnee?.let { point1 ->
                leftAnkle?.let { point2 ->
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

            rightKnee?.let { point1 ->
                rightAnkle?.let { point2 ->
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

            value.joints.forEach { point ->
                if (point.score > 0) {
                    canvas.drawCircle(
                        translateX(canvas, point.pointX),
                        translateY(canvas, point.pointY),
                        10.0f,
                        pointPaint
                    )
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