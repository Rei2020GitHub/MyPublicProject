package com.sample.hmssample.ml.skeletondetectiondemo.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.skeleton.MLSkeleton
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory
import com.sample.hmssample.ml.skeletondetectiondemo.R
import com.sample.hmssample.ml.skeletondetectiondemo.databinding.MainFragmentBinding
import java.io.IOException


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        private const val REQUEST_CODE = 100
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    private val analyzer: MLSkeletonAnalyzer = MLSkeletonAnalyzerFactory.getInstance().skeletonAnalyzer

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

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.filePath.observe(viewLifecycleOwner, Observer {
            analyzeImage(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        analyzer.let {
            try {
                it.stop()
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE -> {
                if (Activity.RESULT_OK == resultCode) {
                    data?.data?.let { data ->
                        analyzeImage(data)
                    }
                }
            }
        }
    }

    private fun analyzeImage(uri: Uri?) {
        val context = context ?: return

        if (null == uri) {
            openFileChooser()
            return
        }

        binding.imageView.setImageURI(uri)
        binding.overlayView.setImageSize(binding.imageView.drawable.intrinsicWidth, binding.imageView.drawable.intrinsicHeight)

        analyzer.asyncAnalyseFrame(
            MLFrame.fromFilePath(context, uri)
        ).addOnSuccessListener(object : OnSuccessListener<List<MLSkeleton>> {
            override fun onSuccess(results: List<MLSkeleton>?) {
                binding.overlayView.setSkeletonResults(results)
            }
        }).addOnFailureListener(object : OnFailureListener {
            override fun onFailure(exception: Exception?) {
                exception?.printStackTrace()
            }
        })
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startActivityForResult(intent, REQUEST_CODE)
    }
}