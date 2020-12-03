package com.sample.hmssample.pushdemo.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sample.hmssample.pushdemo.R
import com.sample.hmssample.pushdemo.Utils
import com.sample.hmssample.pushdemo.databinding.MainFragmentBinding

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)
        viewModel.tokenDisplay.observe(viewLifecycleOwner, Observer {
            binding.textViewToken.text = it
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        with(binding) {
            buttonGetToken.setOnClickListener{
                context?.let { context ->
                    Utils.getAppId(context)?.let { appId ->
                        viewModel.getToken(context, appId)
                    }
                }
            }
            buttonRemoveToken.setOnClickListener{
                context?.let { context ->
                    Utils.getAppId(context)?.let { appId ->
                        viewModel.removeToken(context, appId)
                    }
                }
            }
            buttonSendNotification.setOnClickListener {
                context?.let { context ->
                    val radioButton: RadioButton? = activity?.findViewById(binding.radioGroupImprotance.checkedRadioButtonId)

                    viewModel.sendNotification(
                        context,
                        binding.editTextTitle.text.toString(),
                        binding.editTextBody.text.toString(),
                        radioButton?.text?.toString() ?: "HIGH"
                    )
                }
            }
        }
        super.onActivityCreated(savedInstanceState)
    }
}