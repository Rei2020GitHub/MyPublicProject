package com.sample.hmssample.ml.handkeypointdetectiondemo.model

import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoint
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints

fun MLHandKeypoints.getHandKeypointPair(): Pair<Array<Float>, Array<Float>> {
    val wrist = this.getHandKeypoint(MLHandKeypoint.TYPE_WRIST)
    val thumb1 = this.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_FIRST)
    val thumb2 = this.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_SECOND)
    val thumb3 = this.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_THIRD)
    val thumb4 = this.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_FOURTH)
    val forefinger1 = this.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_FIRST)
    val forefinger2 = this.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_SECOND)
    val forefinger3 = this.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_THIRD)
    val forefinger4 = this.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_FOURTH)
    val middleFinger1 = this.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_FIRST)
    val middleFinger2 = this.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_SECOND)
    val middleFinger3 = this.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_THIRD)
    val middleFinger4 = this.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_FOURTH)
    val ringFinger1 = this.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_FIRST)
    val ringFinger2 = this.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_SECOND)
    val ringFinger3 = this.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_THIRD)
    val ringFinger4 = this.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_FOURTH)
    val littleFinger1 = this.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_FIRST)
    val littleFinger2 = this.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_SECOND)
    val littleFinger3 = this.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_THIRD)
    val littleFinger4 = this.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_FOURTH)

    return Pair(
            arrayOf(
                    wrist.pointX - rect.left,
                    thumb1.pointX - rect.left, thumb2.pointX - rect.left, thumb3.pointX - rect.left, thumb4.pointX - rect.left,
                    forefinger1.pointX - rect.left, forefinger2.pointX - rect.left, forefinger3.pointX - rect.left, forefinger4.pointX - rect.left,
                    middleFinger1.pointX - rect.left, middleFinger2.pointX - rect.left, middleFinger3.pointX - rect.left, middleFinger4.pointX - rect.left,
                    ringFinger1.pointX - rect.left, ringFinger2.pointX - rect.left, ringFinger3.pointX - rect.left, ringFinger4.pointX - rect.left,
                    littleFinger1.pointX - rect.left, littleFinger2.pointX - rect.left, littleFinger3.pointX - rect.left, littleFinger4.pointX - rect.left
            ),
            arrayOf(
                    wrist.pointY - rect.top,
                    thumb1.pointY - rect.top, thumb2.pointY - rect.top, thumb3.pointY - rect.top, thumb4.pointY - rect.top,
                    forefinger1.pointY - rect.top, forefinger2.pointY - rect.top, forefinger3.pointY - rect.top, forefinger4.pointY - rect.top,
                    middleFinger1.pointY - rect.top, middleFinger2.pointY - rect.top, middleFinger3.pointY - rect.top, middleFinger4.pointY - rect.top,
                    ringFinger1.pointY - rect.top, ringFinger2.pointY - rect.top, ringFinger3.pointY - rect.top, ringFinger4.pointY - rect.top,
                    littleFinger1.pointY - rect.top, littleFinger2.pointY - rect.top, littleFinger3.pointY - rect.top, littleFinger4.pointY - rect.top
            )
    )
}

//fun Array<Float>.toValueString(): String {
//    var output = ""
//    forEach { value ->
//        output += if (output.isBlank()) {
//            "$value" + "f"
//        } else {
//            ", $value" + "f"
//        }
//    }
//
//    return output
//}