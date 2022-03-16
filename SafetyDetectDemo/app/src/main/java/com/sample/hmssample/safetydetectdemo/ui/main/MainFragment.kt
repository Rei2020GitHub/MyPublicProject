package com.sample.hmssample.safetydetectdemo.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.sample.hmssample.safetydetectdemo.R
import com.sample.hmssample.safetydetectdemo.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)

        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager(requireContext()).orientation)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.mainDataModelArray.observe(viewLifecycleOwner, { mainDataModelArray ->
            MainAdapter(requireContext().packageManager, mainDataModelArray)
                .also { mainAdapter ->
                    binding.recyclerView.adapter = mainAdapter
                    mainAdapter.notifyDataSetChanged()
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        binding.buttonAppsCheck.setOnClickListener{
            appsCheck()
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.recyclerView.adapter = null
    }

//    private fun showAllApps() {
//        viewModel.packageInfoArray.postValue(
//            requireContext().packageManager.getInstalledPackages(0).toTypedArray()
//        )
//    }

    private fun appsCheck() {
        SafetyDetect.getClient(requireContext())
            .maliciousAppsList
            .addOnSuccessListener { maliciousAppsListResp ->
                viewModel.updateAppsCheckResult(
                    requireContext().packageManager.getInstalledPackages(0),
                    maliciousAppsListResp
                )
            }
            .addOnFailureListener { error ->
                error?.printStackTrace()
            }
    }
}