package com.sample.hmssample.googleplayerrordialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import com.sample.hmssample.googleplayerrordialog.databinding.ActivityMainBinding


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
        val googleErrorCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        val huaweiErrorCode = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this)
        message += if (com.google.android.gms.common.ConnectionResult.SUCCESS == googleErrorCode) {
            "GMS : Exist."
        } else {
            "GMS : Not exist. Error code = $googleErrorCode"
        }
        message += "\n"
        message += if (com.huawei.hms.api.ConnectionResult.SUCCESS == huaweiErrorCode) {
            "HMS : Exist."
        } else {
            "HMS : Not exist. Error code = $huaweiErrorCode"
        }

        binding.textViewCheckResult.text = message
    }

    private fun checkGooglePlayServicesAvailable() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val googleErrorCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (googleErrorCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(googleErrorCode)) {
                googleApiAvailability.getErrorDialog(
                    this,
                    googleErrorCode,
                    REQUEST_CODE
                ).apply {
                    setOnDismissListener {
                        // Do something after error dialog closed
                    }
                }.show()
            }
        }
    }

    private fun checkHuaweiServicesAvailable() {
        val huaweiApiAvailability = HuaweiApiAvailability.getInstance()
        val huaweiErrorCode = huaweiApiAvailability.isHuaweiMobileServicesAvailable(this)
        if (huaweiErrorCode != com.huawei.hms.api.ConnectionResult.SUCCESS) {
            if (huaweiApiAvailability.isUserResolvableError(huaweiErrorCode)) {
                huaweiApiAvailability.getErrorDialog(
                    this,
                    huaweiErrorCode,
                    REQUEST_CODE
                ).apply {
                    setOnDismissListener {
                        // Do something after error dialog closed
                    }
                }.show()
            }
        }
    }

    private fun checkGooglePlayServicesAndHmsAvailable() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val googleErrorCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (googleErrorCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            val huaweiApiAvailability = HuaweiApiAvailability.getInstance()
            val huaweiErrorCode = huaweiApiAvailability.isHuaweiMobileServicesAvailable(this)
            if (huaweiErrorCode != com.huawei.hms.api.ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(googleErrorCode)) {
                    googleApiAvailability.getErrorDialog(
                        this,
                        googleErrorCode,
                        REQUEST_CODE
                    ).apply {
                        setOnDismissListener {
                            // Do something after error dialog closed
                        }
                    }.show()
                }
            }
        }
    }
}