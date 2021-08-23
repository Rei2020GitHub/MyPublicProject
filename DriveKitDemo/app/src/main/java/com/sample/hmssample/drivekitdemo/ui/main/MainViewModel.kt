package com.sample.hmssample.drivekitdemo.ui.main

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.cloud.base.auth.DriveCredential
import com.huawei.cloud.services.drive.model.About
import com.sample.hmssample.drivekitdemo.Utils
import com.sample.hmssample.drivekitdemo.model.HuaweiAccountLogic
import com.sample.hmssample.drivekitdemo.model.HuaweiDriveLogic
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.ReentrantLock

class MainViewModel : ViewModel() {
    companion object {
        private val TAG: String = MainViewModel.javaClass.simpleName
        private const val LOCAL_FILE_NAME = "local_cache.txt"
        private const val DRIVE_FILE_NAME = "drive_cache.txt"
        private const val APP_FOLDER_NAME = "YourAppFolderName"
    }

    val unionId = MutableLiveData<String>()
    val accessToken = MutableLiveData<String>()
    val avatarUri = MutableLiveData<String>()
    val displayName = MutableLiveData<String>()
    val driveInfo = MutableLiveData<String>()
    val content = MutableLiveData<String>()
    val textLog = MutableLiveData<String>("")

    // Account Kitのロジック
    private val huaweiAccountLogic = HuaweiAccountLogic(this)

    // Drive Kitのロジック
    private var huaweiDriveLogic: HuaweiDriveLogic? = null

    private val reentrantLock: ReentrantLock = ReentrantLock()

    // HUAWEI IDでサインイン
    fun signIn(fragment: Fragment) {
        huaweiAccountLogic.signIn(fragment)
    }

    // サインアウト
    fun signOut() {
        huaweiAccountLogic.signOut()
    }

    // ”HUAWEI IDでサインイン”処理のコールバック
    fun signInCallback(result: ActivityResult) {
        huaweiAccountLogic.signInCallback(result)
    }

    // 本アプリに与えたアカウントへのアクセス権限を取り消す
    fun cancelAuthorization() {
        huaweiAccountLogic.cancelAuthorization()
    }

    // HUAWEI Driveに接続する
    // unionIdとaccessTokenが必要なため、HUAWEI Driveに接続する前に、
    // まずHUAWEI IDのアカウントにログインしなければならない（signIn()を実行）
    fun connectHuaweiDrive(context: Context) {
        if (unionId.value.isNullOrBlank() || accessToken.value.isNullOrBlank()) {
            addLog("Please sign in")
            return
        }

        huaweiDriveLogic = HuaweiDriveLogic(
            context,
            unionId.value.toString(),
            accessToken.value.toString(),
            object : DriveCredential.AccessMethod {
                override fun refreshToken(): String {
                    reentrantLock.lock()
                    huaweiAccountLogic.slientSignIn()
                    reentrantLock.unlock()
                    return accessToken.value.toString()
                }
            }
        )

        if (isHuaweiDriveReady()) {
            addLog("HUAWEI Drive is connected")
        } else {
            addLog("HUAWEI Drive is not connected")
        }
    }

    private fun isHuaweiDriveReady(): Boolean {
        return (huaweiDriveLogic?.isReady() == true)
    }

    // ユーザーのHUAWEI Driveの情報（容量を含む）を取得する
    fun showHuaweiDriveInfo() {
        if (!isHuaweiDriveReady()) {
            addLog("Please connect to HUAWEI Drive")
            return
        }

        Single.create<About?> { emitter ->
            emitter.onSuccess(huaweiDriveLogic?.getAbout())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ about ->
                // ドライブ容量
                val total = about.storageQuota.userCapacity
                // 使用済み容量
                val used = about.storageQuota.usedSpace
                // ドライブ空き容量
                val free = total - used

                driveInfo.value = "ドライブ容量:$total, 使用済み容量:$used, 空き容量:$free"
                addLog("Get HUAWEI Drive Info success")
            }, {
                addLog("Get HUAWEI Drive Info fail")
            })
    }

    // データをファイルとして端末に保存してから、HUAWEI Driveにアップロードする
    fun saveFileToHuaweiDrive(context: Context, content: String) {
        if (!isHuaweiDriveReady()) {
            addLog("Please connect to HUAWEI Drive")
            return
        }

        huaweiDriveLogic?.let { huaweiDriveLogic ->
            Single.create<com.huawei.cloud.services.drive.model.File?> { emitter ->
                // データを端末のキャッシュフォルダに保存する
                val localFile = createLocalTempFile(context, content, LOCAL_FILE_NAME)
                // 端末のファイルをHUAWEI Driveにアップロードする
                val file = huaweiDriveLogic.saveFile(localFile, DRIVE_FILE_NAME, APP_FOLDER_NAME, true)
                emitter.onSuccess(file)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    addLog("Save file success : " + file.toPrettyString())
                }, {
                    addLog("Save file fail : $it")
                })
        }
    }

    // データをそのままHUAWEI Driveにアップロードする
    fun saveBufferToHuaweiDrive(content: String) {
        if (!isHuaweiDriveReady()) {
            addLog("Please connect to HUAWEI Drive")
            return
        }

        huaweiDriveLogic?.let { huaweiDriveLogic ->
            Single.create<com.huawei.cloud.services.drive.model.File?> { emitter ->
                val inputStream = content.byteInputStream()
                val inputStreamLength = inputStream.available().toLong()
                val mimeType = "text/plain"

                // データをHUAWEI Driveにアップロードする
                val file = huaweiDriveLogic.saveBuffer(DRIVE_FILE_NAME, APP_FOLDER_NAME, true, inputStream, inputStreamLength, mimeType)
                emitter.onSuccess(file)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    addLog("Save buffer success : " + file.toPrettyString())
                }, {
                    addLog("Save buffer fail : $it")
                })
        }
    }

    // HUAWEI Driveからファイルをダウンロードし、端末のキャッシュフォルダに保存する
    fun loadFromHuaweiDrive(context: Context) {
        if (!isHuaweiDriveReady()) {
            addLog("Please connect to HUAWEI Drive")
            return
        }

        huaweiDriveLogic?.let { huaweiDriveLogic ->
            Single.create<String?> { emitter ->
                // 保存先を指定する
                val localFile = File(context.cacheDir, LOCAL_FILE_NAME)
                // HUAWEI Driveからファイルをダウンロードし、指定した保存先に保存する
                huaweiDriveLogic.load(DRIVE_FILE_NAME, localFile, true)

                // 保存した後に、保存先を開き、中身を読み込む
                val bufferedReader = BufferedReader(InputStreamReader(localFile.inputStream(), StandardCharsets.UTF_8))
                var body = ""
                var lineBuffer: String? = null
                while (bufferedReader.readLine().also { lineBuffer = it } != null) {
                    body += lineBuffer
                }

                emitter.onSuccess(body)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ body ->
                    // 中身を表示する
                    content.postValue(body)
                    addLog("Load success")
                }, {
                    addLog("Load fail : $it")
                })
        }
    }

    private fun createLocalTempFile(context: Context, content: String, filename: String): File {
        val file = File(context.cacheDir, filename)
        var outputStream: FileOutputStream? = null
        try {
            file.createNewFile()
            if (file.exists()) {
                outputStream = FileOutputStream(file)
                outputStream.write(content.toByteArray())
            }
        } catch (exception: IOException) {
            throw exception
        } finally {
            try {
                outputStream?.close()
            } catch (exception: IOException) {
                throw exception
            }
        }
        return file
    }

    fun createFolderInHuaweiDrive(folderName: String) {
        if (!isHuaweiDriveReady()) {
            addLog("Please connect to HUAWEI Drive")
            return
        }

        huaweiDriveLogic?.let { huaweiDriveLogic ->
            Single.create<com.huawei.cloud.services.drive.model.File?> { emitter ->
                // データをHUAWEI Driveにアップロードする
                val file = huaweiDriveLogic.createFolder(folderName, false)
                emitter.onSuccess(file)
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    addLog("Create folder success : " + file.toPrettyString())
                }, {
                    addLog("Create folder fail : $it")
                })
        }
    }

    fun addLog(message: String) {
        Log.i(TAG, message)
        textLog.postValue(textLog.value + Utils.getLogStringLine(message))
    }
}