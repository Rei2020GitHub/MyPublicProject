package com.sample.hmssample.drivekitdemo.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.sample.hmssample.drivekitdemo.R
import com.sample.hmssample.drivekitdemo.databinding.MainFragmentBinding
import com.squareup.picasso.Picasso

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel = MainViewModel()
    private lateinit var binding: MainFragmentBinding
    lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.signInCallback(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(inflater, R.layout.main_fragment, container, false)

        viewModel.avatarUri.observe(viewLifecycleOwner, {
            Picasso
                .with(requireContext())
                .load(it)
                .fit()
                .centerInside()
                .into(binding.imageViewAvatar)
        })
        viewModel.displayName.observe(viewLifecycleOwner, {
            binding.textViewDisplayName.text = it
        })
        viewModel.unionId.observe(viewLifecycleOwner, {
            binding.textViewUnionId.text = it
        })
        viewModel.accessToken.observe(viewLifecycleOwner, {
            binding.textViewAccessToken.text = it
        })
        viewModel.driveInfo.observe(viewLifecycleOwner, {
            binding.textViewDriveInfo.text = it
        })
        viewModel.content.observe(viewLifecycleOwner, {
            binding.editTextSaveContent.setText(it)
        })

        viewModel.textLog.observe(viewLifecycleOwner, {
            binding.textLog.text = it
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonSignInAuthorizationCode.setOnClickListener{
            viewModel.signIn(this)
        }
        binding.buttonSignOut.setOnClickListener{
            viewModel.signOut()
        }
        binding.buttonCancelAuthorization.setOnClickListener{
            viewModel.cancelAuthorization()
        }
        binding.buttonDriveConnect.setOnClickListener {
            viewModel.connectHuaweiDrive(requireContext())
        }
        binding.buttonDriveInfo.setOnClickListener {
            viewModel.showHuaweiDriveInfo()
        }
        binding.buttonDriveSaveFile.setOnClickListener {
            if (binding.editTextSaveContent.text.toString().isNotEmpty()) {
                viewModel.saveFileToHuaweiDrive(
                    requireContext(),
                    binding.editTextSaveContent.text.toString()
                )
            } else {
                viewModel.addLog("Please enter content")
            }
        }
        binding.buttonDriveSaveBuffer.setOnClickListener {
            if (binding.editTextSaveContent.text.toString().isNotEmpty()) {
                viewModel.saveBufferToHuaweiDrive(
                    binding.editTextSaveContent.text.toString()
                )
            } else {
                viewModel.addLog("Please enter content")
            }
        }
        binding.buttonDriveLoad.setOnClickListener {
            viewModel.loadFromHuaweiDrive(requireContext())
        }

        super.onViewCreated(view, savedInstanceState)
    }
}