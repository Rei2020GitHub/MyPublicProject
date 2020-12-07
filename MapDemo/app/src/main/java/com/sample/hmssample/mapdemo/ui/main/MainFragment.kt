package com.sample.hmssample.mapdemo.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.widget.SearchIntent
import com.sample.hmssample.mapdemo.R
import com.sample.hmssample.mapdemo.Utils
import com.sample.hmssample.mapdemo.databinding.MainFragmentBinding
import kotlin.math.min


class MainFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = MainFragment()
        private val PERMISSION_LIST: List<String> = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        savedInstanceState?.let {
            viewModel.mapViewBundle = savedInstanceState
        }
        viewModel.searchService = SearchServiceFactory.create(context, Utils.getApiKeyEncoded(context))
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)
        binding.mapView.onCreate(viewModel.mapViewBundle)
        binding.mapView.getMapAsync(this)
        viewModel.isMyLocationEnabled.observe(viewLifecycleOwner, Observer {
            viewModel.map?.isMyLocationEnabled = it
        })
        viewModel.isMyLocationButtonEnabled.observe(viewLifecycleOwner, Observer {
            viewModel.map?.uiSettings?.isMyLocationButtonEnabled = it
        })
        viewModel.isRecording.observe(viewLifecycleOwner, Observer {
            if (it) {
                startRecord()
            } else {
                stopRecord()
            }
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        viewModel.settingsClient = LocationServices.getSettingsClient(activity)

        binding.buttonSearch.setOnClickListener {
            viewModel.searchIntent = SearchIntent().apply {
                setApiKey(Utils.getApiKeyEncoded(context))
                startActivityForResult(this.getIntent(activity), SearchIntent.SEARCH_REQUEST_CODE)
            }
        }
        binding.buttonRecord.setOnClickListener {
            viewModel.isRecording.value = viewModel.isRecording.value?.not()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()

        checkAndAskPermission()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()

        context?.let {
            viewModel.saveCameraPosition(it)
        }

        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopRecord()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onMapReady(map: HuaweiMap?) {
        viewModel.map = map
        context?.let {
            viewModel.initMap(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SearchIntent.SEARCH_REQUEST_CODE -> {
                if (SearchIntent.isSuccess(resultCode)) {
                    viewModel.searchIntent?.getSiteFromIntent(data)?.let {
                        viewModel.removeMarkers()
                        viewModel.addMarker(it)
                        viewModel.moveCamera(it)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (permissions.isNotEmpty() && grantResults.isNotEmpty()) {
                    val resultMap: MutableMap<String, Int> = mutableMapOf()
                    for (i in 0..min(permissions.size - 1, grantResults.size - 1)) {
                        resultMap[permissions[i]] = grantResults[i]
                    }

                    when {
                        resultMap[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                                && resultMap[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED -> {
                            viewModel.isMyLocationEnabled.value = true
                            viewModel.isMyLocationButtonEnabled.value = true
                            binding.buttonRecord.visibility = View.VISIBLE
                        }
                        resultMap[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                                && resultMap[Manifest.permission.ACCESS_COARSE_LOCATION] == null -> {
                            viewModel.isMyLocationEnabled.value = true
                            viewModel.isMyLocationButtonEnabled.value = true
                            binding.buttonRecord.visibility = View.VISIBLE
                        }
                        resultMap[Manifest.permission.ACCESS_FINE_LOCATION] == null
                                && resultMap[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED -> {
                            viewModel.isMyLocationEnabled.value = true
                            viewModel.isMyLocationButtonEnabled.value = true
                            binding.buttonRecord.visibility = View.VISIBLE
                        }
                        else -> {
                            viewModel.isMyLocationEnabled.value = false
                            viewModel.isMyLocationButtonEnabled.value = false
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
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission)
                }
            }

            if (permissionList.isNullOrEmpty()) {
                viewModel.isMyLocationEnabled.value = true
                viewModel.isMyLocationButtonEnabled.value = true
                binding.buttonRecord.visibility = View.VISIBLE
            } else {
                requestPermissions(permissionList.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun startRecord() {
        viewModel.startRecord()
        binding.buttonRecord.setBackgroundResource(R.drawable.ic_action_stop)
    }

    private fun stopRecord() {
        viewModel.stopRecord()
        binding.buttonRecord.setBackgroundResource(R.drawable.ic_action_record)
    }
}