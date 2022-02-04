package com.sample.hmssample.appauthdemo.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sample.hmssample.appauthdemo.R
import com.sample.hmssample.appauthdemo.databinding.MainFragmentBinding
import com.squareup.picasso.Picasso

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)

        viewModel.openId.observe(viewLifecycleOwner, {
            binding.textViewOpenId.text = it
        })
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
        viewModel.accessToken.observe(viewLifecycleOwner, {
            binding.textViewAccessToken.text = it
        })
        viewModel.refreshToken.observe(viewLifecycleOwner, {
            binding.textViewRefreshToken.text = it
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