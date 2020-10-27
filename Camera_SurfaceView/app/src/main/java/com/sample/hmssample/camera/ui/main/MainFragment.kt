package com.sample.hmssample.camera.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sample.hmssample.camera.R
import com.sample.hmssample.camera.databinding.MainFragmentBinding
import java.io.IOException
import kotlin.math.max
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
    private var surfaceHolder: SurfaceHolder? = null

    private var camera: Camera? = null

    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            surfaceHolder = holder
            checkAndAskPermission()
        }

        override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
        ) {
            viewModel.surfaceWidth = width
            viewModel.surfaceHeight = height

            camera?.let { camera ->
                updateSurfaceSize(
                        binding.surfaceView,
                        camera.parameters.previewSize.width,
                        camera.parameters.previewSize.height,
                        viewModel.surfaceWidth,
                        viewModel.surfaceHeight
                )
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
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
        binding.surfaceView.holder.addCallback(surfaceHolderCallback)

        return binding.root
    }

    override fun onStop() {
        super.onStop()

        camera?.let {
            it.stopPreview()
            it?.release()
        }
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
        val cameraId = getCameraId(cameraType) ?: return
        val windowManager = activity?.windowManager ?: return

        camera = Camera.open(cameraId)
        camera?.let { camera ->
            try {
                camera.setPreviewDisplay(surfaceHolder)
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }

            camera.setDisplayOrientation(
                    when (windowManager.defaultDisplay.rotation) {
                        Surface.ROTATION_0 -> 90
                        Surface.ROTATION_90 -> 0
                        Surface.ROTATION_180 -> 270
                        Surface.ROTATION_270 -> 180
                        else -> 0
                    }
            )

            camera.parameters = camera.parameters.apply {
                val size = supportedPreviewSizes.firstOrNull()
                size?.let { size ->
                    setPreviewSize(size.width, size.height)
                }
            }
            camera.startPreview()
            updateSurfaceSize(
                    binding.surfaceView,
                    camera.parameters.previewSize.width,
                    camera.parameters.previewSize.height,
                    viewModel.surfaceWidth,
                    viewModel.surfaceHeight
            )
        }
    }

    private fun getCameraId(cameraType: Int?): Int? {
        val cameraType = cameraType ?: return null

        val cameraInfo = CameraInfo()
        for (i in 0 until Camera.getNumberOfCameras()) {
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == cameraType) {
                return i
            }
        }

        return null
    }

    private fun updateSurfaceSize(view: View, width: Int, height: Int, surfaceWidth: Int, surfaceHeight: Int) {
        val param = view.layoutParams
        var newWidth = width
        var newHeight = height

        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            newWidth = height
            newHeight = width
        }

        val scale1 = surfaceWidth / newWidth.toFloat()
        val scale2 = surfaceHeight / newHeight.toFloat()
        val scale = if (newWidth < surfaceWidth || newHeight < surfaceHeight) {
            max(scale1, scale2)
        } else if (newWidth < surfaceWidth) {
            scale1
        } else if (newHeight < surfaceHeight) {
            scale2
        } else {
            1.0f
        }

        param.width = (newWidth.toFloat() * scale).toInt()
        param.height = (newHeight.toFloat() * scale).toInt()
        view.layoutParams = param
    }
}