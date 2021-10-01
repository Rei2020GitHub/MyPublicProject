package com.sample.hmssample.ml.translationdemo.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.sample.hmssample.ml.translationdemo.R
import com.sample.hmssample.ml.translationdemo.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
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

        viewModel.outputText.observe(viewLifecycleOwner, Observer {
            binding.textViewTranslatedText.text = it
        })
        viewModel.translatorReady.observe(viewLifecycleOwner, Observer {
            binding.buttonTranslate.isEnabled = it && (viewModel.downloadReady.value == true)
        })
        viewModel.downloadReady.observe(viewLifecycleOwner, Observer {
            binding.buttonTranslate.isEnabled = it && (viewModel.translatorReady.value == true)
        })

        binding.buttonTranslate.setOnClickListener {
            translate()
        }

        viewModel.initTranslator("ja", "en")
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }

    private fun translate() {
        viewModel.translate(binding.editTextOriginalText.text.toString())
    }
}