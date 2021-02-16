package com.sample.hmssample.adsdemo.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.hms.ads.*
import com.huawei.hms.ads.VideoOperator.VideoLifecycleListener
import com.huawei.hms.ads.annotation.GlobalApi
import com.huawei.hms.ads.instreamad.InstreamAd
import com.huawei.hms.ads.instreamad.InstreamAdLoadListener
import com.huawei.hms.ads.instreamad.InstreamAdLoader
import com.huawei.hms.ads.instreamad.InstreamMediaStateListener
import com.huawei.hms.ads.nativead.NativeAd
import com.huawei.hms.ads.nativead.NativeAdConfiguration
import com.huawei.hms.ads.nativead.NativeAdLoader
import com.huawei.hms.ads.nativead.NativeView
import com.huawei.hms.ads.reward.Reward
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener
import com.sample.hmssample.adsdemo.R
import com.sample.hmssample.adsdemo.databinding.MainFragmentBinding
import com.sample.hmssample.adsdemo.databinding.NativeSmallTemplateBinding
import com.sample.hmssample.adsdemo.databinding.NativeVideoTemplateBinding


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private var globalNativeAd: NativeAd? = null
    private var rewardAd: RewardAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(
            inflater,
            R.layout.main_fragment,
            container,
            false
        )
        viewModel.oaid.observe(viewLifecycleOwner, Observer {
            binding.textViewOaid.text = it
        })
        viewModel.isLimitAdTrackingEnabled.observe(viewLifecycleOwner, Observer {
            binding.textViewLimitAdTracking.text =
                if (it) getString(R.string.limit_ad_tracking_enabled) else getString(
                    R.string.limit_ad_tracking_disabled
                )
        })
        viewModel.rewardScore.observe(viewLifecycleOwner, Observer {
            binding.textViewRewardScore.text = it.toString()
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.buttonGetAdvertisingIdInfo.setOnClickListener {
            context?.let {
                viewModel.getAdvertisingIdInfo(it)
            }
        }
        binding.buttonConnectInstallReferrerClient.setOnClickListener {
            context?.let {
                viewModel.connectInstallReferrerClient(it)
            }
        }
        binding.buttonDisconnectInstallReferrerClient.setOnClickListener {
            viewModel.disconnectInstallReferrerClient()
        }
        binding.buttonRewardAd.setOnClickListener {
            context?.let {
                showRewardAd(it)
            }
        }
        binding.buttonInterstitialAd.setOnClickListener {
            context?.let {
                showInterstitialAd(it)
            }
        }
        binding.buttonRollAd.setOnClickListener {
            context?.let {
                showRollAd(it)
            }
        }
        binding.btnLoad.setOnClickListener {
            context?.let {
                showNativeAd(it)
            }
        }
        binding.instreamView.setInstreamMediaStateListener(object : InstreamMediaStateListener {
            override fun onMediaProgress(p0: Int, p1: Int) {
            }

            override fun onMediaStart(p0: Int) {
            }

            override fun onMediaPause(p0: Int) {
            }

            override fun onMediaStop(p0: Int) {
            }

            override fun onMediaCompletion(p0: Int) {
            }

            override fun onMediaError(p0: Int, p1: Int, p2: Int) {
            }
        })
    }

    override fun onStart() {
        super.onStart()

        showBanner()
    }

    override fun onDestroy() {
        globalNativeAd?.destroy()
        rewardAd?.destroy()
        binding.instreamView.destroy()
        super.onDestroy()
    }

    private fun showBanner() {
        val param = AdParam.Builder().build()
        binding.hwBannerView.loadAd(param)
    }

    private fun getAdId(): String {
        return when {
            binding.radioButtonSmall.isChecked -> {
                getString(R.string.ad_id_native_small)
            }
            binding.radioButtonVideo.isChecked -> {
                getString(R.string.ad_id_native_video)
            }
            else -> {
                getString(R.string.ad_id_native)
            }
        }
    }

    private fun showNativeAd(context: Context) {
        binding.btnLoad.isEnabled = false

        val nativeAdLoadedListener = object : NativeAd.NativeAdLoadedListener {
            override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                nativeAd?.let {
                    globalNativeAd?.destroy()
                    globalNativeAd = it

                    // 動画広告のコールバックを設定
                    it.videoOperator.videoLifecycleListener = object : VideoOperator.VideoLifecycleListener() {
                        override fun onVideoStart() {}
                        override fun onVideoPlay() {}
                        override fun onVideoPause() {}
                        override fun onVideoEnd() {}
                        override fun onVideoMute(var1: Boolean) {}
                    }

                    // 広告の×ボタンを押したときのコールバック
                    it.setDislikeAdListener {
                        binding.scrollViewAd.removeAllViews()
                    }

                    // 取得したネイティブ広告のデータを画面に表示
                    binding.scrollViewAd.removeAllViews()
                    binding.scrollViewAd.addView(
                        if (binding.radioButtonSmall.isChecked) {
                            bindNativeSmallView(it)
                        } else {
                            bindNativeVideoView(it)
                        }
                    )
                }
            }
        }

        // 広告の各種イベントのコールバック
        val adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                binding.btnLoad.isEnabled = true
            }

            override fun onAdFailed(errorCode: Int) {
                super.onAdFailed(errorCode)
                binding.btnLoad.isEnabled = true
            }
        }

        // ネイティブ広告ロードのビルダ
        val builder = NativeAdLoader.Builder(context, getAdId())
            //
            .setNativeAdLoadedListener(nativeAdLoadedListener)
            .setAdListener(adListener)

        // 動画広告の設定
        val videoConfiguration = VideoConfiguration.Builder()
            // 動画広告の場合、ミュート状態で開始
            .setStartMuted(true)
            .build()

        // 広告の設定
        val adConfiguration = NativeAdConfiguration.Builder()
            // 動画広告の設定
            .setVideoConfiguration(videoConfiguration)
            // true: デフォルトの広告非表示ボタンを使わない, false: デフォルトの広告非表示ボタンを使う
            .setRequestCustomDislikeThisAd(false)
            // "i" または "x"の表示位置
            .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT)
            .build()

        val nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build()
        nativeAdLoader.loadAds(AdParam.Builder().build(), 10)
    }

    private fun bindNativeVideoView(nativeAd: NativeAd): View {
        val nativeBinding = DataBindingUtil.inflate<NativeVideoTemplateBinding>(
            LayoutInflater.from(
                context
            ), R.layout.native_video_template, null, false
        )
        val nativeView = nativeBinding.nativeVideoView

        nativeView.titleView = nativeBinding.adTitle
        nativeView.mediaView = nativeBinding.adMedia
        nativeView.adSourceView = nativeBinding.adSource
        nativeView.callToActionView = nativeBinding.adCallToAction

        bindNativeView(nativeAd, nativeView)

        return nativeBinding.root
    }

    private fun bindNativeSmallView(nativeAd: NativeAd): View {
        val nativeBinding = DataBindingUtil.inflate<NativeSmallTemplateBinding>(
            LayoutInflater.from(
                context
            ), R.layout.native_small_template, null, false
        )
        val nativeView = nativeBinding.nativeSmallView

        nativeView.titleView = nativeBinding.adTitle
        nativeView.mediaView = nativeBinding.adMedia
        nativeView.adSourceView = nativeBinding.adSource
        nativeView.callToActionView = nativeBinding.adCallToAction

        bindNativeView(nativeAd, nativeView)

        return nativeBinding.root
    }

    private fun bindNativeView(nativeAd: NativeAd, nativeView: NativeView) {
        nativeView.mediaView.setMediaContent(nativeAd.mediaContent)

        val textView: TextView? = nativeView.adSourceView as? TextView
        nativeAd.adSource?.let{ adSource ->
            textView?.text = adSource
        }
        textView?.visibility = if (null != nativeAd.adSource) View.VISIBLE else View.INVISIBLE

        val button: Button? = nativeView.callToActionView as? Button
        nativeAd.callToAction?.let { callToAction ->
            button?.text = callToAction
        }
        button?.visibility = if (null != nativeAd.callToAction) View.VISIBLE else View.INVISIBLE

        nativeView.setNativeAd(nativeAd)
    }

    private fun loadRewardAd(context: Context) {
        rewardAd?.destroy()
        rewardAd = RewardAd(context, getString(R.string.ad_id_reward))
        rewardAd?.loadAd(
            AdParam.Builder().build(),
            object : RewardAdLoadListener() {
                override fun onRewardedLoaded() {
                    showRewardAd(context)
                }

                override fun onRewardAdFailedToLoad(errorCode: Int) {
                    super.onRewardAdFailedToLoad(errorCode)
                }
            }
        )
    }

    private fun showRewardAd(context: Context) {
        rewardAd?.let { it ->
            if (it.isLoaded) {
                it.show(this.activity, object : RewardAdStatusListener() {
                    override fun onRewardAdClosed() {
                        rewardAd?.destroy()
                        rewardAd = null
                        super.onRewardAdClosed()
                    }

                    override fun onRewardAdFailedToShow(errorCode: Int) {
                        super.onRewardAdFailedToShow(errorCode)
                    }

                    override fun onRewardAdOpened() {
                        super.onRewardAdOpened()
                    }

                    override fun onRewarded(reward: Reward?) {
                        reward?.let {
                            viewModel.addRewardScore(it.amount)
                        }
                    }
                })
            }
        } ?: run {
            loadRewardAd(context)
        }
    }

    private fun showInterstitialAd(context: Context) {
        val interstitialAd = InterstitialAd(context).apply {
            adId = getString(R.string.ad_id_interstitial)
        }
        val listener = object : AdListener() {
            override fun onAdLoaded() {
                interstitialAd.show()
                super.onAdLoaded()
            }
        }
        interstitialAd.adListener = listener
        interstitialAd.loadAd(AdParam.Builder().build())
    }

    private fun showRollAd(context: Context) {
        val builder: InstreamAdLoader.Builder = InstreamAdLoader.Builder(
            context,
            getString(R.string.ad_id_roll)
        )
        val adLoader: InstreamAdLoader = builder.setTotalDuration(60)
            .setMaxCount(8)
            .setInstreamAdLoadListener(object : InstreamAdLoadListener {
                override fun onAdLoaded(list: MutableList<InstreamAd>?) {
                    binding.instreamView.setInstreamAds(list)
                }

                override fun onAdFailed(i: Int) {
                }
            })
            .build()

        adLoader.loadAd(AdParam.Builder().build())
    }
}