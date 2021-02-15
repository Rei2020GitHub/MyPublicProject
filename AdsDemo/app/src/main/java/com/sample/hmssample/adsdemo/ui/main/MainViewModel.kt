package com.sample.hmssample.adsdemo.ui.main

import android.content.Context
import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.hms.ads.identifier.AdvertisingIdClient
import com.huawei.hms.ads.installreferrer.api.InstallReferrerClient
import com.huawei.hms.ads.installreferrer.api.InstallReferrerStateListener
import com.huawei.hms.ads.installreferrer.api.ReferrerDetails
import com.sample.hmssample.adsdemo.BuildConfig
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException


class MainViewModel : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val oaid = MutableLiveData<String>("")
    val isLimitAdTrackingEnabled = MutableLiveData<Boolean>(false)
    val rewardScore = MutableLiveData<Int>(0)

    private var installReferrerClient: InstallReferrerClient? = null

    private var installReferrerStateListener = object : InstallReferrerStateListener{
        override fun onInstallReferrerSetupFinished(responseCode: Int) {
            when(responseCode) {
                InstallReferrerClient.InstallReferrerResponse.OK -> {
                    installReferrerClient?.let {
                        try {
                            val referrerDetails: ReferrerDetails = it.installReferrer
                            Log.i(TAG, "Install Referrer = " + referrerDetails.installReferrer)
                            Log.i(TAG, "Referrer Click Timestamp Millisecond = " + referrerDetails.referrerClickTimestampMillisecond)
                            Log.i(TAG, "Install Begin Timestamp Millisecond = " + referrerDetails.installBeginTimestampMillisecond)
                        } catch (exception: RemoteException) {
                            Log.e(TAG, exception.message, exception)
                        } catch (exception: IOException) {
                            Log.e(TAG, exception.message, exception)
                        } finally {
                            disconnectInstallReferrerClient()
                        }
                    }
                }
                InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                    Log.i(TAG, "FEATURE_NOT_SUPPORTED")
                }
                InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                    Log.i(TAG, "SERVICE_UNAVAILABLE")
                }
                InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED -> {
                    Log.i(TAG, "SERVICE_DISCONNECTED")
                }
                InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR -> {
                    Log.i(TAG, "DEVELOPER_ERROR")
                }
            }
        }

        override fun onInstallReferrerServiceDisconnected() {
            Log.i(TAG, "onInstallReferrerServiceDisconnected")
        }
    }

    fun getAdvertisingIdInfo(context: Context) {
        Single.create<AdvertisingIdClient.Info>{ emitter ->
            val info = AdvertisingIdClient.getAdvertisingIdInfo(context)
            emitter.onSuccess(info)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ info ->
                oaid.value = info.id
                isLimitAdTrackingEnabled.value = info.isLimitAdTrackingEnabled
            },{
                it.printStackTrace()
            })
    }

    fun connectInstallReferrerClient(context: Context) {
        Completable.create{ emitter ->
            installReferrerClient = InstallReferrerClient
                .newBuilder(context)
                .setTest(BuildConfig.INSTALL_REFERRER_TEST)
                .build()
            installReferrerClient?.let {
                it.startConnection(installReferrerStateListener)
            }
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun disconnectInstallReferrerClient() {
        installReferrerClient?.endConnection()
        installReferrerClient = null
    }

    fun addRewardScore(score: Int) {
        val oldScore = rewardScore.value ?: 0
        rewardScore.value = oldScore + score
    }
}