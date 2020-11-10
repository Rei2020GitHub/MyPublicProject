package com.sample.hmssample.mobileservicesdetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import com.sample.hmssample.mobileservicesdetection.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE = 100
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        with(binding) {
            buttonCheckGms.setOnClickListener {
                checkGooglePlayServicesAvailable()
            }
            buttonCheckHms.setOnClickListener {
                checkHuaweiServicesAvailable()
            }
            buttonCheckGmsHms.setOnClickListener {
                checkGooglePlayServicesAndHmsAvailable()
            }
        }
        updateCheckResult()
    }

    private fun updateCheckResult() {
        var message = ""

        // GMSの存在確認
        val googleErrorCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        // HMSの存在確認
        val huaweiErrorCode = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this)

        // 確認結果を表示
        message += if (com.google.android.gms.common.ConnectionResult.SUCCESS == googleErrorCode) {
            getString(R.string.gms) + getString(R.string.exist)
        } else {
            getString(R.string.gms) + getString(R.string.not_exist) + getString(R.string.error_code) + googleErrorCode
        }
        message += "\n"
        message += if (com.huawei.hms.api.ConnectionResult.SUCCESS == huaweiErrorCode) {
            getString(R.string.hms) + getString(R.string.exist)
        } else {
            getString(R.string.hms) + getString(R.string.not_exist) + getString(R.string.error_code) + huaweiErrorCode
        }

        binding.textViewCheckResult.text = message
    }

    private fun checkGooglePlayServicesAvailable() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()

        // GMSの存在確認
        googleApiAvailability.isGooglePlayServicesAvailable(this).let { code ->
            // com.google.android.gms.common.ConnectionResult.SUCCESS = 0: The connection was successful.
            // com.google.android.gms.common.ConnectionResult.SERVICE_MISSING = 1: Google Play services is missing on this device.
            // com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED = 2: The installed version of Google Play services is out of date.
            // com.google.android.gms.common.ConnectionResult.SERVICE_DISABLED = 3: The installed version of Google Play services has been disabled on this device.
            // com.google.android.gms.common.ConnectionResult.SERVICE_INVALID = 9: The version of the Google Play services installed on this device is not authentic.
            // com.google.android.gms.common.ConnectionResult.SERVICE_UPDATING = 9004: Google Play service is currently being updated on this device.
            if (com.google.android.gms.common.ConnectionResult.SUCCESS != code) {
                // ユーザーが対処可能なエラー
                if (googleApiAvailability.isUserResolvableError(code)) {
                    googleApiAvailability.getErrorDialog(
                            this,
                            code,
                            REQUEST_CODE
                    ).apply {
                        setOnDismissListener {
                            // GMSが見つからないポップアップ終了後の処理
                        }
                    }.show()
                } else {
                    // ユーザーが対処不可なエラー
                }
            }
        }
    }

    private fun checkHuaweiServicesAvailable() {
        val huaweiApiAvailability = HuaweiApiAvailability.getInstance()

        // HMSの存在確認
        huaweiApiAvailability.isHuaweiMobileServicesAvailable(this).let { code ->
            // com.huawei.hms.api.ConnectionResult.SUCCESS = 0: connection succeeded.
            // com.huawei.hms.api.ConnectionResult.SERVICE_MISSING = 1: no HMS Core (APK) found on the device.
            // com.huawei.hms.api.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED = 2: The installed HMS Core (APK) is out of date.
            // com.huawei.hms.api.ConnectionResult.SERVICE_DISABLED = 3: The HMS Core (APK) installed on this device is unavailable.
            // com.huawei.hms.api.ConnectionResult.SERVICE_INVALID = 9: The HMS Core (APK) installed on the device is not the official version.
            // com.huawei.hms.api.ConnectionResult.SERVICE_UNSUPPORTED = 21: The device is too old to be supported.
            if (com.huawei.hms.api.ConnectionResult.SUCCESS != code) {
                // ユーザーが対処可能なエラー
                if (huaweiApiAvailability.isUserResolvableError(code)) {
                    huaweiApiAvailability.getErrorDialog(
                            this,
                            code,
                            REQUEST_CODE
                    ).apply {
                        setOnDismissListener {
                            // HMSが見つからないポップアップ終了後の処理
                        }
                    }.show()
                } else {
                    // ユーザーが対処不可なエラー
                }
            }
        }
    }

    private fun checkGooglePlayServicesAndHmsAvailable() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()

        // まずGMSの存在確認を行う
        googleApiAvailability.isGooglePlayServicesAvailable(this).let { googleErrorCode ->
            // com.google.android.gms.common.ConnectionResult.SUCCESS = 0: The connection was successful.
            // com.google.android.gms.common.ConnectionResult.SERVICE_MISSING = 1: Google Play services is missing on this device.
            // com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED = 2: The installed version of Google Play services is out of date.
            // com.google.android.gms.common.ConnectionResult.SERVICE_DISABLED = 3: The installed version of Google Play services has been disabled on this device.
            // com.google.android.gms.common.ConnectionResult.SERVICE_INVALID = 9: The version of the Google Play services installed on this device is not authentic.
            // com.google.android.gms.common.ConnectionResult.SERVICE_UPDATING = 9004: Google Play service is currently being updated on this device.
            if (com.google.android.gms.common.ConnectionResult.SUCCESS != googleErrorCode) {
                val huaweiApiAvailability = HuaweiApiAvailability.getInstance()

                // GMSが使えない場合はHMSの存在確認
                huaweiApiAvailability.isHuaweiMobileServicesAvailable(this).let { huaweiErrorCode ->
                    // com.huawei.hms.api.ConnectionResult.SUCCESS = 0: connection succeeded.
                    // com.huawei.hms.api.ConnectionResult.SERVICE_MISSING = 1: no HMS Core (APK) found on the device.
                    // com.huawei.hms.api.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED = 2: The installed HMS Core (APK) is out of date.
                    // com.huawei.hms.api.ConnectionResult.SERVICE_DISABLED = 3: The HMS Core (APK) installed on this device is unavailable.
                    // com.huawei.hms.api.ConnectionResult.SERVICE_INVALID = 9: The HMS Core (APK) installed on the device is not the official version.
                    // com.huawei.hms.api.ConnectionResult.SERVICE_UNSUPPORTED = 21: The device is too old to be supported.
                    if (com.huawei.hms.api.ConnectionResult.SUCCESS != huaweiErrorCode) {
                        // GMSもHMSも使えない場合、まずユーザーがGMSエラー対処ができるかどうかを確認する
                        if (googleApiAvailability.isUserResolvableError(googleErrorCode)) {
                            // GMSエラー対処が可能な場合は、GMSエラーダイアログを表示する
                            googleApiAvailability.getErrorDialog(
                                    this,
                                    googleErrorCode,
                                    REQUEST_CODE
                            ).apply {
                                setOnDismissListener {
                                    // GMSが見つからないポップアップ終了後の処理
                                }
                            }.show()
                        }
                        // ユーザーがGMSエラー対処ができない場合、ユーザーがHMSエラー対処ができるかどうかを確認する
                        else if (huaweiApiAvailability.isUserResolvableError(huaweiErrorCode)) {
                            // HMSエラー対処が可能な場合は、HMSエラーダイアログを表示する
                            huaweiApiAvailability.getErrorDialog(
                                    this,
                                    huaweiErrorCode,
                                    REQUEST_CODE
                            ).apply {
                                setOnDismissListener {
                                    // HMSが見つからないポップアップ終了後の処理
                                }
                            }.show()
                        } else {
                            // ユーザーが対処不可なエラー
                        }
                    }
                }
            }
        }
    }
}