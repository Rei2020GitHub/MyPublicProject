package com.sample.hmssample.accountdemo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.hms.api.HuaweiApiAvailability
import com.sample.hmssample.accountdemo.R
import com.sample.hmssample.accountdemo.databinding.MainFragmentBinding
import com.squareup.picasso.Picasso


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModelFactory(com.huawei.hms.api.ConnectionResult.SUCCESS == HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context))
        ).get(MainViewModel::class.java)
    }
    private lateinit var binding: MainFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)
        viewModel.unionId.observe(viewLifecycleOwner, Observer {
            binding.textViewUnionId.text = it
        })

        viewModel.avatarUri.observe(viewLifecycleOwner, Observer {
            Picasso
                .get()
                .load(it)
                .fit()
                .centerInside()
                .into(binding.imageViewAvatar)
        })
        viewModel.displayName.observe(viewLifecycleOwner, Observer {
            binding.textViewDisplayName.text = it
        })

        viewModel.textLog.observe(viewLifecycleOwner, Observer {
            binding.textLog.text = it
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        binding.buttonSignInAuthorizationCode.setOnClickListener{
            viewModel.huaweiIdLogicModel.signIn(this)
        }
        binding.buttonSignOut.setOnClickListener{
            viewModel.huaweiIdLogicModel.signOut()
        }
        binding.buttonCancelAuthorization.setOnClickListener{
            viewModel.huaweiIdLogicModel.cancelAuthorization(requireContext())
        }

        activity?.let { activity ->
            viewModel.huaweiIdLogicModel.update(requireContext(), activity.intent)
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        viewModel.huaweiIdLogicModel.onActivityResult(requestCode, resultCode, data)
    }
}