package com.sample.hmssample.pushdemo.ui.main

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.hms.push.RemoteMessage
import com.sample.hmssample.pushdemo.Const
import com.sample.hmssample.pushdemo.R
import com.sample.hmssample.pushdemo.Utils
import com.sample.hmssample.pushdemo.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { intent ->
                if (Const.ACTION == intent.action) {
                    intent.extras?.let {  bundle ->
                        when (bundle.getString(Const.KEY_COMMEND)) {
                            Const.COMMEND_NEW_TOKEN -> {
                                viewModel.setToken(bundle.getString(Const.KEY_TOKEN) ?: "")
                            }
                            Const.COMMEND_MESSAGE_RECEIVED -> {
                                val remoteMessage = bundle.getParcelable<RemoteMessage>(Const.KEY_REMOTE_MESSAGE)
                                remoteMessage?.let { remoteMessage ->
                                    context?.let { context ->
                                        AlertDialog.Builder(context)
                                            .setMessage(remoteMessage.data)
                                            .setPositiveButton("OK", null)
                                            .show()
                                    }
                                }
                            }
                            Const.COMMEND_TOKEN_ERROR -> {
                                context?.let {  context ->
                                    viewModel.setGetTokenError(context)
                                }
                            }
                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }

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

    override fun onStart() {
        super.onStart()

        val intentFilter = IntentFilter().apply {
            addAction(Const.ACTION)
        }
        context?.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()

        context?.unregisterReceiver(broadcastReceiver)
    }
}