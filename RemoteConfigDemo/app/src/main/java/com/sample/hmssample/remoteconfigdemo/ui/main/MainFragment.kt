package com.sample.hmssample.remoteconfigdemo.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.huawei.agconnect.remoteconfig.AGConnectConfig
import com.sample.hmssample.remoteconfigdemo.R
import com.sample.hmssample.remoteconfigdemo.databinding.MainFragmentBinding
import java.math.BigDecimal

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()

        private const val FETCH_INTERVAL = 60 * 5L
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private val config = AGConnectConfig.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)

        config.applyDefault(R.xml.remote_config)
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

        viewModel.boolean.observe(viewLifecycleOwner, Observer {
            binding.textViewBoolean.text = it.toString()
        })

        viewModel.double.observe(viewLifecycleOwner, Observer {
            binding.textViewDouble.text = BigDecimal.valueOf(it).toPlainString()
        })

        viewModel.long.observe(viewLifecycleOwner, Observer {
            binding.textViewLong.text = it.toString()
        })

        viewModel.string.observe(viewLifecycleOwner, Observer {
            binding.textViewString.text = it
        })

        binding.buttonFetch.setOnClickListener {
            fetch()
        }

        loadConfig()
    }

    private fun fetch() {
        config.fetch(FETCH_INTERVAL).addOnSuccessListener {
            config.apply(it)
            applyConfigValue()
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun loadConfig() {
        config.apply(config.loadLastFetched())
        applyConfigValue()
    }

    private fun applyConfigValue() {
        viewModel.boolean.value = config.getValueAsBoolean("boolean")
        viewModel.double.value = config.getValueAsDouble("double")
        viewModel.long.value = config.getValueAsLong("long")
        viewModel.string.value = config.getValueAsString("string")
    }
}