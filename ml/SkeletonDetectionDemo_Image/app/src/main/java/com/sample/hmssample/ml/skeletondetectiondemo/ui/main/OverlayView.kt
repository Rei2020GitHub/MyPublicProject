package com.sample.hmssample.ml.skeletondetectiondemo.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.huawei.hms.mlsdk.skeleton.MLJoint
import com.huawei.hms.mlsdk.skeleton.MLSkeleton
import java.lang.Float.min


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

    private var skeletonResults: List<MLSkeleton>? = null
    private var imageWidth = 1
    private var imageHeight = 1
    private var scale = 1.0f
    private var offsetX = 0.0f
    private var offsetY = 0.0f

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

    fun setSkeletonResults(results: List<MLSkeleton>?) {
        skeletonResults = results
        invalidate()
    }

    fun setImageSize(width: Int, height: Int) {
        imageWidth = width
        imageHeight = height
        updateScale()
    }

    private fun updateScale() {
        val scaleX = width.toFloat() / imageWidth.toFloat()
        val scaleY = height.toFloat() / imageHeight.toFloat()
        scale = min(scaleX, scaleY)

        if (scaleX > scaleY) {
            offsetX = (width.toFloat() - imageWidth.toFloat() * scale) / 2.0f
            offsetY = 0.0f
        } else {
            offsetX = 0.0f
            offsetY = (height.toFloat() - imageHeight.toFloat() * scale) / 2.0f
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let { canvas ->
            drawSkeletonResult(canvas, skeletonResults)
        }
    }

    private fun drawSkeletonResult(canvas: Canvas, results: List<MLSkeleton>?) {
        results?.forEach { value ->
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
        return (x) * scale + offsetX
    }

    private fun translateY(canvas: Canvas, y: Float): Float {
        return (y) * scale + offsetY
    }
}