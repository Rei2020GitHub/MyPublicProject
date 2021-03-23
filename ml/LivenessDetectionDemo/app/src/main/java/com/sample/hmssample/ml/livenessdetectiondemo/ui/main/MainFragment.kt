package com.sample.hmssample.ml.livenessdetectiondemo.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult
import com.sample.hmssample.ml.livenessdetectiondemo.R
import com.sample.hmssample.ml.livenessdetectiondemo.databinding.MainFragmentBinding
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

    private val livenessCaptureCallback = object : MLLivenessCapture.Callback{
        override fun onSuccess(result: MLLivenessCaptureResult?) {
            result?.let {
                viewModel.photo.postValue(it.bitmap)
                viewModel.resultText.postValue(if (it.isLive) "Is Live" else "Not Live")
            }
        }

        override fun onFailure(errorCode: Int) {
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

        viewModel.photo.observe(viewLifecycleOwner, Observer {
            binding.imageView.setImageBitmap(it)
        })
        viewModel.resultText.observe(viewLifecycleOwner, Observer {
            binding.textviewResult.text = it
        })

        binding.buttonStart.setOnClickListener {
            startLivenessDetection()
        }

        return binding.root
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
                            binding.buttonStart.isEnabled = true
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
        binding.buttonStart.isEnabled = false
        context?.let { context ->
            val permissionList: MutableList<String> = mutableListOf()

            PERMISSION_LIST.forEach { permission ->
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission)
                }
            }

            if (!permissionList.contains(Manifest.permission.CAMERA)) {
                binding.buttonStart.isEnabled = true
            }

            if (!permissionList.isNullOrEmpty()) {
                requestPermissions(permissionList.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        checkAndAskPermission()
    }

    private fun startLivenessDetection() {
        activity?.let {
            MLLivenessCapture.getInstance().startDetect(it, livenessCaptureCallback)
        }
    }
}