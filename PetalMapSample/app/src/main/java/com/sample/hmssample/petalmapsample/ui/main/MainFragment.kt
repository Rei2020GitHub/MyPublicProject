package com.sample.hmssample.petalmapsample.ui.main

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sample.hmssample.petalmapsample.R
import com.sample.hmssample.petalmapsample.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.buttonNearBySearch.setOnClickListener {
            procNearbySearch()
        }

        binding.buttonKeywordSearch.setOnClickListener {
            procKeywordSearch()
        }

        binding.buttonRoutePlanning.setOnClickListener {
            procRoutePlanning()
        }

        binding.buttonPoiDetails.setOnClickListener {
            procPoiDetails()
        }
    }

    private fun procNearbySearch() {
        binding.editTextNearBySearch.text?.let { text ->
            if (text.isNotBlank()) {
                procDeepLink("petalmaps://nearbySearch?text=$text")
            }
        }
    }

    private fun procKeywordSearch() {
        binding.editTextKeywordSearch.text?.let { text ->
            if (text.isNotBlank()) {
                procDeepLink("petalmaps://textSearch?text=$text")
            }
        }
    }

    private fun procRoutePlanning() {
        val saddr = binding.editTextRoutePlanningFrom.text.toString()
        val daddr = binding.editTextRoutePlanningTo.text.toString()
        val type = binding.spinnerRoutePlanningTo.selectedItem.toString()

        if (type.isNotBlank()) {
            procDeepLink("petalmaps://route?saddr=$saddr&daddr=$daddr&type=$type")
//            procDeepLink("https://www.petalmaps.com/routes/?saddr=$saddr&daddr=$daddr&type=$type")
        }
    }

    private fun procPoiDetails() {
        binding.editTextPoiDetails.text?.let { text ->
            if (text.isNotBlank()) {
                procDeepLink("petalmaps://poidetail?center=$text&marker=$text")
//                procDeepLink("https://www.petalmaps.com/place/?center=$text&marker=$text")
            }
        }
    }

    private fun procDeepLink(uriString: String) {
        Uri.parse(uriString)?.let { uri ->
            Intent(Intent.ACTION_VIEW, uri).let { intent ->
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }
}