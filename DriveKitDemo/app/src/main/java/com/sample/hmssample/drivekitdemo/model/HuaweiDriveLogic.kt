package com.sample.hmssample.drivekitdemo.model

import android.content.Context
import android.webkit.MimeTypeMap
import com.huawei.cloud.base.auth.DriveCredential
import com.huawei.cloud.base.auth.DriveCredential.AccessMethod
import com.huawei.cloud.base.http.FileContent
import com.huawei.cloud.base.http.InputStreamContent
import com.huawei.cloud.services.drive.Drive
import com.huawei.cloud.services.drive.model.About
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class HuaweiDriveLogic(
    private val context: Context,
    unionID: String,
    accessToken: String,
    accessMethod: AccessMethod
) {

    companion object {
        private const val DIRECT_UPLOAD_MAX_SIZE = 20L * 1024L * 1024L
        private const val DIRECT_DOWNLOAD_MAX_SIZE = 20L * 1024L * 1024L
        private const val PAGE_SIZE = 10
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

    // HUAWEI Driveからファイルまたはフォルダを検索する
    private fun getFile(fileName: String, isFolder: Boolean, isApplicationData: Boolean): com.huawei.cloud.services.drive.model.File? {
        drive?.let { drive ->
            val queryFile = "fileName = '$fileName' and mimeType " + (if (isFolder) "=" else "!=") + " 'application/vnd.huawei-apps.folder'"
            val files = drive.files().list().setQueryParam(queryFile)
                .setPageSize(PAGE_SIZE)
                .setOrderBy("fileName")
                .setFields("category,nextCursor,files/id,files/fileName,files/size").apply {
                    if (isApplicationData) {
                        containers = "applicationData"
                    }
                }
                .execute()

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

    // HUAWEI Driveでフォルダを作成する
    fun createFolder(folderName: String, isApplicationData: Boolean): com.huawei.cloud.services.drive.model.File? {
        drive?.let { drive ->
            val appProperties: Map<String, String> = mutableMapOf("appProperties" to "property")
            val file = com.huawei.cloud.services.drive.model.File()
                .setFileName(folderName)
                .setMimeType("application/vnd.huawei-apps.folder")
                .setAppSettings(appProperties).apply {
                    if (isApplicationData) {
                        parentFolder = listOf("applicationData")
                    }
                }
            return drive.files().create(file).execute()
        }

        return null
    }

    // HUAWEI Driveにファイルをアップロードする
    fun saveFile(localFile: File, driveFilename: String, folderName: String, isApplicationData: Boolean): com.huawei.cloud.services.drive.model.File? {
        drive?.let { drive ->
            // ドライブにフォルダを作成
            var directoryCreated = getFile(folderName, true, isApplicationData)
            if (null == directoryCreated) {
                directoryCreated = createFolder(folderName, isApplicationData)
            }

            directoryCreated?.let { directoryCreated ->
                // 既存ファイルを削除
                val oldFile = getFile(driveFilename, false, isApplicationData)
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
    fun saveBuffer(driveFilename: String, folderName: String, isApplicationData: Boolean, inputStream: InputStream, inputStreamLength: Long, mimeType: String): com.huawei.cloud.services.drive.model.File? {
        drive?.let { drive ->
            // ドライブにフォルダを作成
            var directoryCreated = getFile(folderName, true, isApplicationData)
            if (null == directoryCreated) {
                directoryCreated = createFolder(folderName, isApplicationData)
            }

            directoryCreated?.let { directoryCreated ->
                // 既存ファイルを削除
                val oldFile = getFile(driveFilename, false, isApplicationData)
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
    fun load(filename: String, dest: File, isApplicationData: Boolean) {
        getFile(filename, false, isApplicationData)?.let { file ->
            download(file, dest)
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