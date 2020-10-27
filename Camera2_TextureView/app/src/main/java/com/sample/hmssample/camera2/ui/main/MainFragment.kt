package com.sample.hmssample.camera2.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sample.hmssample.camera2.R
import com.sample.hmssample.camera2.databinding.MainFragmentBinding
import kotlin.math.min

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        private val PERMISSION_LIST: List<String> = listOf(
                Manifest.permission.CAMERA
        )
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private var surfaceTexture: SurfaceTexture? = null

    private var cameraManager: CameraManager? = null
    private var cameraDevice: CameraDevice? = null
    private var captureRequest: CaptureRequest? = null
    private var cameraCaptureSession: CameraCaptureSession? = null

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            surfaceTexture = surface
            viewModel.surfaceWidth = width
            viewModel.surfaceHeight = height

            checkAndAskPermission()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            surfaceTexture = surface.apply {
                updateSurfaceTexture(this, width, height)
            }
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }
    }

    private val cameraDeviceStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(device: CameraDevice) {
            cameraDevice = device

            cameraDevice?.let { cameraDevice ->
                val surfaceList: ArrayList<Surface> = arrayListOf()

                surfaceTexture?.let {
                    surfaceList.add(Surface(it))
                }

                try {
                    captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                        surfaceList.forEach {
                            addTarget(it)
                        }
                    }.build()

                    cameraDevice.createCaptureSession(
                            surfaceList,
                            cameraCaptureSessionStateCallback,
                            null
                    )
                } catch (cameraAccessException: CameraAccessException) {
                    cameraAccessException.printStackTrace()
                }
            }
        }

        override fun onDisconnected(device: CameraDevice) {
            cameraDevice = null
        }

        override fun onError(device: CameraDevice, error: Int) {
            cameraDevice = null
        }
    }

    private val cameraCaptureSessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onActive(session: CameraCaptureSession) {
            super.onActive(session)
            cameraCaptureSession = session
        }

        override fun onClosed(session: CameraCaptureSession) {
            super.onClosed(session)
        }

        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
            val captureRequest: CaptureRequest = captureRequest ?: return
            try {
                cameraCaptureSession.setRepeatingRequest(
                        captureRequest,
                        null,
                        null
                )
            } catch (cameraAccessException: CameraAccessException) {
                cameraAccessException.printStackTrace()
            }
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
        }

        override fun onReady(session: CameraCaptureSession) {
            super.onReady(session)
        }

        override fun onSurfacePrepared(session: CameraCaptureSession, surface: Surface) {
            super.onSurfacePrepared(session, surface)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        cameraManager = context?.getSystemService(Context.CAMERA_SERVICE) as? CameraManager?
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(
                inflater,
                R.layout.main_fragment,
                container,
                false
        )
        binding.textureView.surfaceTextureListener = surfaceTextureListener

        return binding.root
    }

    override fun onStop() {
        super.onStop()

        cameraCaptureSession?.let {
            try {
                it.stopRepeating()
            } catch (cameraAccessException: CameraAccessException) {
                cameraAccessException.printStackTrace()
            }
            it.close()

            cameraCaptureSession = null
        }

        cameraDevice?.close()
        cameraDevice = null
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (permissions.isNotEmpty() && grantResults.isNotEmpty()) {
                    val resultMap: MutableMap<String, Int> = mutableMapOf()
                    for (i in 0..min(permissions.size - 1, grantResults.size - 1)) {
                        resultMap[permissions[i]] = grantResults[i]
                    }

                    when {
                        resultMap[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED -> {
                            useCamera(viewModel.cameraType.value)
                        }
                        else -> {
                            activity?.finish()
                        }
                    }
                }
            }
        }
    }

    private fun checkAndAskPermission() {
        context?.let { context ->
            val permissionList: MutableList<String> = mutableListOf()

            PERMISSION_LIST.forEach { permission ->
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission)
                }
            }

            if (!permissionList.contains(Manifest.permission.CAMERA)) {
                useCamera(viewModel.cameraType.value)
            }

            if (!permissionList.isNullOrEmpty()) {
                requestPermissions(permissionList.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun useCamera(cameraType: Int?) {
        val context = context ?: return
        val cameraManager: CameraManager = cameraManager ?: return
        val cameraId = getCameraId(cameraType) ?: return

        if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        try {
            cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)?.getOutputSizes(SurfaceTexture::class.java)?.getOrNull(0)?.let { size ->
                updateSurfaceSize(binding.textureView, size.width, size.height)
            }

            cameraManager.openCamera(cameraId, cameraDeviceStateCallback, null)
        } catch (cameraAccessException: CameraAccessException) {
            cameraAccessException.printStackTrace()
        }
    }

    private fun getCameraId(cameraType: Int?): String? {
        val cameraType = cameraType ?: return null
        val cameraManager = cameraManager ?: return null

        cameraManager.cameraIdList.firstOrNull { cameraId ->
            cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.LENS_FACING) == cameraType
        }?.let { cameraId ->
            return cameraId
        }

        return null
    }

    private fun updateSurfaceSize(view: View, width: Int, height: Int) {
        val param = view.layoutParams.apply {
            if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
                this.width = height
                this.height = width
            } else {
                this.width = width
                this.height = height
            }
        }

        view.layoutParams = param
    }

    private fun updateSurfaceTexture(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        var newWidth = width
        var newHeight = height

        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            newWidth = height
            newHeight = width
        }
        surfaceTexture.setDefaultBufferSize(newWidth, newHeight)

        val transform = Matrix()
        val center = Point(newWidth / 2, newHeight / 2)

        when (activity?.windowManager?.defaultDisplay?.rotation) {
            Surface.ROTATION_90 -> {
                transform.postRotate(270.0f, center.x.toFloat(), center.y.toFloat())
            }
            Surface.ROTATION_180 -> {
                transform.postRotate(180.0f, center.x.toFloat(), center.y.toFloat())
            }
            Surface.ROTATION_270 -> {
                transform.postRotate(90.0f, center.x.toFloat(), center.y.toFloat())
            }
        }

        when (activity?.windowManager?.defaultDisplay?.rotation) {
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                transform.postScale(newWidth.toFloat() / newHeight.toFloat() * newWidth.toFloat() / newHeight.toFloat(),
                        1.0f,
                        center.x.toFloat(),
                        center.y.toFloat())
            }
        }

        binding.textureView.setTransform(transform)

        viewModel.surfaceWidth = width
        viewModel.surfaceHeight = height
    }
}