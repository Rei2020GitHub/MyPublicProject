package com.sample.hmssample.drivekitdemo.model

import android.content.Context
import android.webkit.MimeTypeMap
import com.huawei.cloud.base.auth.DriveCredential
import com.huawei.cloud.base.auth.DriveCredential.AccessMethod
import com.huawei.cloud.base.http.FileContent
import com.huawei.cloud.base.http.InputStreamContent
import com.huawei.cloud.services.drive.Drive
import com.huawei.cloud.services.drive.model.About
import com.sample.hmssample.drivekitdemo.ui.main.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class HuaweiDriveLogic(
    private val context: Context,
    unionID: String,
    accessToken: String,
    accessMethod: AccessMethod
) {

    companion object {
        private const val APP_FOLDER_NAME = "YourAppFolderName"
        private const val DIRECT_UPLOAD_MAX_SIZE = 20L * 1024L * 1024L
        private const val DIRECT_DOWNLOAD_MAX_SIZE = 20L * 1024L * 1024L
    }

    private var credential: DriveCredential? = null
    private var drive: Drive? = null

    init {
        if (unionID.isNotBlank() && accessToken.isNotBlank()) {
            credential = DriveCredential.Builder(unionID, accessMethod)
                            .build()
                            .setAccessToken(accessToken)
            credential?.let {
                drive = Drive.Builder(it, context).build()
            }
        }
    }

    fun isReady(): Boolean {
        return (drive != null)
    }

    // HUAWEI Driveの情報を取得する
    fun getAbout(): About? {
        return drive?.about()?.get()?.setFields("*")?.execute()
    }

    // HUAWEI Driveからファイルを検索する
    private fun getFile(fileName: String): com.huawei.cloud.services.drive.model.File? {
        drive?.let { drive ->
            val containers = "applicationData"
            val queryFile = "fileName = '$fileName' and mimeType != 'application/vnd.huawei-apps.folder'"
            val files = drive.files().list().setQueryParam(queryFile)
                .setPageSize(10)
                .setOrderBy("fileName")
                .setFields("category,nextCursor,files/id,files/fileName,files/size")
                .setContainers(containers).execute()

            files.files.forEach { file ->
                if (file.fileName == fileName) {
                    return file
                }
            }
        }

        return null
    }

    // HUAWEI Driveからファイルを削除する
    private fun deleteFile(target: com.huawei.cloud.services.drive.model.File) {
        drive?.let { drive ->
            val deleteFile = drive.files().delete(target.id)
            deleteFile.execute()
        }
    }

    // HUAWEI Driveからアプリケーションフォルダを取得する
    private fun getApplicationFolder(): com.huawei.cloud.services.drive.model.File? {
        drive?.let { drive ->
            val containers = "applicationData"
            val queryFile = "fileName = '$APP_FOLDER_NAME' and mimeType = 'application/vnd.huawei-apps.folder'"
            val files = drive.files().list().setQueryParam(queryFile)
                .setPageSize(10)
                .setOrderBy("fileName")
                .setFields("category,nextCursor,files/id,files/fileName,files/size")
                .setContainers(containers).execute()

            files.files.forEach { file ->
                if (file.fileName == APP_FOLDER_NAME) {
                    return file
                }
            }
        }

        return null
    }

    // HUAWEI Driveでアプリケーションフォルダを作成する
    private fun createApplicationFolder(): com.huawei.cloud.services.drive.model.File? {
        drive?.let { drive ->
            // ドライブにアプリケーションの専用フォルダを作成
            val appProperties: Map<String, String> = mutableMapOf("appProperties" to "property")
            val file = com.huawei.cloud.services.drive.model.File()
                .setFileName(APP_FOLDER_NAME)
                .setMimeType("application/vnd.huawei-apps.folder")
                .setAppSettings(appProperties)
                .setParentFolder(listOf("applicationData"))
            return drive.files().create(file).execute()
        }

        return null
    }

    // HUAWEI Driveにファイルをアップロードする
    fun saveFile(localFile: File, driveFilename: String): com.huawei.cloud.services.drive.model.File? {
        drive?.let { drive ->
            // ドライブにアプリケーションの専用フォルダを作成
            var directoryCreated = getApplicationFolder()
            if (null == directoryCreated) {
                directoryCreated = createApplicationFolder()
            }

            directoryCreated?.let { directoryCreated ->
                // 既存ファイルを削除
                val oldFile = getFile(driveFilename)
                if (null != oldFile) {
                    deleteFile(oldFile)
                }

                // 中身をアップロード
                val mimeTypeMap = MimeTypeMap.getSingleton()
                val extension = MimeTypeMap.getFileExtensionFromUrl(localFile.name)
                val mimeType = mimeTypeMap.getMimeTypeFromExtension(extension)
                val content = com.huawei.cloud.services.drive.model.File()
                    .setFileName(driveFilename)
                    .setMimeType(mimeType)
                    .setParentFolder(listOf(directoryCreated.id))

                val isDirectUpload = localFile.length() < DIRECT_UPLOAD_MAX_SIZE
                val request = drive.files().create(content, FileContent(mimeType, localFile)).apply {
                    mediaHttpUploader.isDirectUploadEnabled = isDirectUpload
                }

                return request.execute()
            }
        }

        return null
    }

    // HUAWEI Driveにデータをアップロードする
    fun saveBuffer(driveFilename: String, inputStream: InputStream, inputStreamLength: Long, mimeType: String): com.huawei.cloud.services.drive.model.File? {
        drive?.let { drive ->
            // ドライブにアプリケーションの専用フォルダを作成
            var directoryCreated = getApplicationFolder()
            if (null == directoryCreated) {
                directoryCreated = createApplicationFolder()
            }

            directoryCreated?.let { directoryCreated ->
                // 既存ファイルを削除
                val oldFile = getFile(driveFilename)
                if (null != oldFile) {
                    deleteFile(oldFile)
                }

                // 中身をアップロード
                val streamContent = InputStreamContent(mimeType, inputStream).apply {
                    length = inputStreamLength
                }

                val content = com.huawei.cloud.services.drive.model.File()
                    .setFileName(driveFilename)
                    .setParentFolder(listOf(directoryCreated.id))

                val request = drive.files().create(content, streamContent)
                return request.execute()
            }
        }

        return null
    }

    // HUAWEI Driveからファイルをダウンロード
    fun load(filename: String, dest: File) {
        drive?.let { drive ->
            val containers = "applicationData"
            val queryFile = "fileName = '$filename' and mimeType != 'application/vnd.huawei-apps.folder'"
            val files = drive.files().list().setQueryParam(queryFile)
                .setPageSize(10)
                .setOrderBy("fileName")
                .setFields("category,nextCursor,files/id,files/fileName,files/size")
                .setContainers(containers).execute()

            files.files.forEach { file ->
                if (file.fileName == filename) {
                    download(file, dest)
                    return@forEach
                }
            }
        }
    }

    private fun download(file: com.huawei.cloud.services.drive.model.File, dest: File) {
        drive?.let { drive ->
            val size: Long = file.getSize()
            val isDirectDownload = file.getSize() < DIRECT_DOWNLOAD_MAX_SIZE
            drive.files()[file.id].let { get ->
                get.mediaHttpDownloader.setContentRange(0, size - 1).isDirectDownloadEnabled = isDirectDownload
                get.executeContentAndDownloadTo(FileOutputStream(dest))
            }
        }
    }
}