package com.sample.hmssample.appauthdemo.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.huawei.hms.api.HuaweiApiAvailability
import com.sample.hmssample.appauthdemo.R
import com.sample.hmssample.appauthdemo.databinding.MainFragmentBinding
import com.squareup.picasso.Picasso

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory(
                com.huawei.hms.api.ConnectionResult.SUCCESS == HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)
            )
        )[MainViewModel::class.java]
    }
    private lateinit var binding: MainFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)

        viewModel.pictureUrl.observe(viewLifecycleOwner, {
            Picasso
                .get()
                .load(it)
                .fit()
                .centerInside()
                .into(binding.imageViewPicture)
        })
        viewModel.name.observe(viewLifecycleOwner, {
            binding.textViewName.text = it
        })
        viewModel.email.observe(viewLifecycleOwner, {
            binding.textViewEmail.text = it
        })
        viewModel.openId.observe(viewLifecycleOwner, {
            binding.textViewOpenId.text = it
        })
        viewModel.textLog.observe(viewLifecycleOwner, {
            binding.textLog.text = it
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.buttonSignInHuaweiId.setOnClickListener{
            viewModel.signInHuaweiId(this)
        }
        binding.buttonSignInGoogleId.setOnClickListener{
            viewModel.signInGoogleId(this)
        }
        binding.buttonSignOut.setOnClickListener{
            viewModel.signOut(requireContext())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requireContext(), requestCode, resultCode, data)
    }
}