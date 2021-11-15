package com.sample.hmssample.scankitdemo.ui.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.sample.hmssample.scankitdemo.R
import com.sample.hmssample.scankitdemo.databinding.MainFragmentBinding
import kotlin.math.min

class MainFragment : Fragment() {

    companion object {
        val TAG: String = this.javaClass.simpleName
        fun newInstance() = MainFragment()

        private val PERMISSION_LIST: List<String> = listOf(
            Manifest.permission.CAMERA
        )
        private const val PERMISSION_REQUEST_CODE = 100
        private const val REQUEST_CODE_SCAN_ONE = 200
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.scanText.observe(viewLifecycleOwner, Observer {
            binding.textViewScanText.text = it
        })

        binding.buttonScan.setOnClickListener {
            checkAndAskPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        data?.let { data ->
            when (requestCode) {
                REQUEST_CODE_SCAN_ONE -> {
                    val hmsScan = data.getParcelableExtra(ScanUtil.RESULT) as HmsScan?
                    hmsScan?.let { hmsScan ->
                        viewModel.scanText.value = hmsScan.linkUrl.linkvalue

                        if (canProcDeepLink(hmsScan.linkUrl.linkvalue)) {
                            showOpenDialog(hmsScan.linkUrl.linkvalue)
                        }
                    }
                }
                else -> {

                }
            }
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

                    if (resultMap[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED) {
                        scan()
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
                scan()
            }

            if (!permissionList.isNullOrEmpty()) {
                requestPermissions(permissionList.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun scan() {
        ScanUtil.startScan(
            requireActivity(),
            REQUEST_CODE_SCAN_ONE,
            null
        )
    }

    private fun canProcDeepLink(uriString: String): Boolean {
        Uri.parse(uriString)?.let { uri ->
            Intent(Intent.ACTION_VIEW, uri).let { intent ->
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    return true
                }
            }
        }

        return false
    }

    private fun procDeepLink(uriString: String): Boolean {
        Uri.parse(uriString)?.let { uri ->
            Intent(Intent.ACTION_VIEW, uri).let { intent ->
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                    return true
                }
            }
        }

        return false
    }

    private fun showOpenDialog(uriString: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(uriString + "を開きますか？")
            .setPositiveButton("はい") { dialog, which ->
                procDeepLink(uriString)
            }
            .setNegativeButton("いいえ") { dialog, which ->

            }
            .create()
            .show()
    }
}