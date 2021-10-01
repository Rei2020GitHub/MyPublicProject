package com.sample.hmssample.ml.translationdemo.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.hms.mlsdk.model.download.MLModelDownloadListener
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy
import com.huawei.hms.mlsdk.translate.MLTranslateLanguage
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslateSetting
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslator

class MainViewModel : ViewModel() {

    var inputText = MutableLiveData("")
    var outputText = MutableLiveData("")
    var translatorReady = MutableLiveData(false)
    var downloadReady = MutableLiveData(false)

    private var loaclTranslator: MLLocalTranslator? = null

    fun initTranslator(sourceLanguageCode: String, targetLanguageCode: String) {
        translatorReady.value = false
        MLTranslateLanguage.getLocalAllLanguages().addOnSuccessListener {
            if (it.contains(sourceLanguageCode) && it.contains(targetLanguageCode)) {
                loaclTranslator = MLTranslatorFactory.getInstance().getLocalTranslator(
                    MLLocalTranslateSetting.Factory()
                        .setSourceLangCode(sourceLanguageCode)
                        .setTargetLangCode(targetLanguageCode)
                        .create()
                )
                translatorReady.value = true

                downloadModel()
            }
        }.addOnFailureListener {
            it.printStackTrace()
            translatorReady.value = false
        }
    }

    private fun downloadModel() {
        downloadReady.value = false

        loaclTranslator?.preparedModel(
            MLModelDownloadStrategy.Factory()
                .needWifi()
                .create(),
            MLModelDownloadListener { alreadyDownLength, totalLength ->
            }
        )?.addOnSuccessListener {
            downloadReady.value = true
        }?.addOnFailureListener {
            it.printStackTrace()
            downloadReady.value = false
        }
    }

    fun translate(text: String) {
        if (text.isNotBlank()) {
            loaclTranslator?.asyncTranslate(text)?.addOnSuccessListener {
                outputText.postValue(it)
            }?.addOnFailureListener {
                it.printStackTrace()
            }
        }
    }

    fun release() {
        loaclTranslator?.stop()
    }
}