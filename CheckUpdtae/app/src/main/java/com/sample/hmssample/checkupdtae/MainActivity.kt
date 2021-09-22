package com.sample.hmssample.checkupdtae

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import com.sample.hmssample.checkupdtae.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.huaweioverseas.smarthome"
        private const val APPGALLERY_LINK = "market://com.huawei.appmarket.applink?appId=C10406921"

        private const val PLAY_STORE_PACKAGENAME = "com.android.vending"
        private const val APPGALLERY_PACKAGENAME = "com.huawei.appmarket"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.buttonCheckUpdate.setOnClickListener {
            checkUpdate()
        }
        binding.textViewVersion.text = getVersion()
    }

    private fun getVersion(): String? {
        return packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)?.versionName
    }

    private fun checkUpdate() {
        // サーバー側から最新バージョンのバージョン番号を取得
        // ↓
        // 現在のバージョンと比較
        // ↓
        // 最新バージョンではない場合
        // ↓
        updateAppByCheckingPackage()
    }

    private fun updateAppByCheckingService() {
        // GMSの存在確認
        val googleResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        // HMSの存在確認
        val huaweiResult = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this)

        // 先にGMSが使えるかどうか調べる
        // GMSが使える場合、Play Storeが存在するとみなす
        if (com.google.android.gms.common.ConnectionResult.SUCCESS == googleResult) {
            openLink(PLAY_STORE_LINK)
        }
        // GMSが使えない場合、HMSが使えるかどうか調べる
        // HMSが使える場合、AppGalleryが存在するとみなす
        else if (com.huawei.hms.api.ConnectionResult.SUCCESS == huaweiResult) {
            openLink(APPGALLERY_LINK)
        }
    }

    private fun updateAppByCheckingPackage() {
        // 先にPlay Storeが使えるかどうか調べる
        if (getApplicationInfo(PLAY_STORE_PACKAGENAME) != null) {
            openLink(PLAY_STORE_LINK)
        }
        // Play Storeが使えない場合、AppGalleryが使えるかどうか調べる
        else if (getApplicationInfo(APPGALLERY_PACKAGENAME) != null) {
            openLink(APPGALLERY_LINK)
        }
    }

    private fun getApplicationInfo(packageName: String): ApplicationInfo? {
        return try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (exception: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun openLink(uriString: String): Boolean {
        Uri.parse(uriString)?.let { uri ->
            Intent(Intent.ACTION_VIEW, uri).let { intent ->
                if (intent.resolveActivity(applicationContext.packageManager) != null) {
                    startActivity(intent)
                    return true
                }
            }
        }
        return false
    }
}