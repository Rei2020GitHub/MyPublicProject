package com.sample.hmssample.ml.textrecognitiondemo.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.text.MLLocalTextSetting
import com.huawei.hms.mlsdk.text.MLText
import com.huawei.hms.mlsdk.text.MLTextAnalyzer
import com.sample.hmssample.ml.textrecognitiondemo.R
import com.sample.hmssample.ml.textrecognitiondemo.databinding.MainFragmentBinding
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

    private var textAnalyzer: MLTextAnalyzer? = null
    private var lensEngine: LensEngine? = null

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
            lensEngine?.let { lensEngine ->
                updateSurfaceSize(
                    binding.surfaceView,
                    lensEngine.displayDimension.width,
                    lensEngine.displayDimension.height,
                    viewModel.surfaceWidth,
                    viewModel.surfaceHeight
                )
                updateOverlay(lensEngine)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel.displayDimensionWidth.observe(viewLifecycleOwner, Observer {
            refreshLensEngine()
        })
        viewModel.displayDimensionHeight.observe(viewLifecycleOwner, Observer {
            refreshLensEngine()
        })
        viewModel.fps.observe(viewLifecycleOwner, Observer {
            refreshLensEngine()
        })
        viewModel.autoFocusEnabled.observe(viewLifecycleOwner, Observer {
            refreshLensEngine()
        })
        viewModel.flashMode.observe(viewLifecycleOwner, Observer {
            refreshLensEngine()
        })
        viewModel.focusMode.observe(viewLifecycleOwner, Observer {
            refreshLensEngine()
        })
        viewModel.zoom.observe(viewLifecycleOwner, Observer {
            lensEngine?.doZoom(it)
        })
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        releaseLensEngine()
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
                            refreshLensEngine()
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
                refreshLensEngine()
            }

            if (!permissionList.isNullOrEmpty()) {
                requestPermissions(permissionList.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun releaseLensEngine() {
        textAnalyzer?.let {
            try {
                it.stop()
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
            textAnalyzer = null
        }
        lensEngine?.let {
            it.release()
            lensEngine = null
        }
    }

    private fun refreshLensEngine() {
        releaseLensEngine()
        initLensEngine()
    }

    private fun initLensEngine() {
        val surfaceHolder = surfaceHolder ?: return
        val lensType = viewModel.lensType.value ?: return
        val displayDimensionWidth = viewModel.displayDimensionWidth.value ?: return
        val displayDimensionHeight = viewModel.displayDimensionHeight.value ?: return
        val fps = viewModel.fps.value ?: return
        val autoFocusEnabled = viewModel.autoFocusEnabled.value ?: return
        val flashMode = viewModel.flashMode.value ?: return
        val focusMode = viewModel.focusMode.value ?: return

        val creator = generateTextRecognitionLensEngineCreator()
        lensEngine = creator
            .setLensType(lensType)
            .applyDisplayDimension(displayDimensionWidth, displayDimensionHeight)
            .applyFps(fps)
            .enableAutomaticFocus(autoFocusEnabled)
            .setFlashMode(flashMode)
            .setFocusMode(focusMode)
            .create()
            .apply {
                run(surfaceHolder)

                updateSurfaceSize(
                    binding.surfaceView,
                    this.displayDimension.width,
                    this.displayDimension.height,
                    viewModel.surfaceWidth,
                    viewModel.surfaceHeight
                )
                updateOverlay(this)
            }
    }

    private fun generateTextRecognitionLensEngineCreator() : LensEngine.Creator {
        textAnalyzer = MLTextAnalyzer.Factory(context)
            .setLocalOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
            .setLanguage("ja")
            .create().apply {
            setTransactor(object : MLAnalyzer.MLTransactor<MLText.Block> {
                override fun transactResult(results: MLAnalyzer.Result<MLText.Block>?) {
                    binding.overlayView.setResults(results)
                }

                override fun destroy() {
                }
            })
        }

        return LensEngine.Creator(context, textAnalyzer)
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

    private fun updateOverlay(lensEngine: LensEngine) {
        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.overlayView.setCameraWidth(lensEngine.displayDimension.height)
            binding.overlayView.setCameraHeight(lensEngine.displayDimension.width)
        } else {
            binding.overlayView.setCameraWidth(lensEngine.displayDimension.width)
            binding.overlayView.setCameraHeight(lensEngine.displayDimension.height)
        }

        binding.overlayView.layoutParams.width = binding.surfaceView.layoutParams.width
        binding.overlayView.layoutParams.height = binding.surfaceView.layoutParams.height
        binding.overlayView.setLensType(lensEngine.lensType)
    }
}