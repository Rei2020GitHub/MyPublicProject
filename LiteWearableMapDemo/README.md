# ファーウェイLite Wearableマップアプリのサンプルプロジェクト
## 現時点（２０２５年８月）におけるLite Wearable開発の注意事項
* Lite Wearableで実現できることがかなり限られています。おそらく開発者が思った以上に制限が多いです。特に画面表示について、基本的に一度決まったレイアウトを動的に再編集することがほぼ無理なので、凝ったものを作るのがかなり難しいです。そのため、なるべく簡単かつ静的なレイアウトを作るように心掛けることをお勧めします。
* 利用できるCSSのプロパティはかなり限られています。ウェブページ感覚でレイアウトを作ってしまうと、アプリが動かなかったり、表示できなかったり、表示が崩れたり、インストールできなかったりといった不具合が生じるかもしれません。
* margin-left、margin-right、margin-top、margin-bottomにマイナスの値を入れられません。
* HAPファイルをLite Wearableの実機にインストールし、動作させることができますが、ログが取れず、デバッグもできません。ログとデバッグをしたい場合、DevEco Studio付属のシミュレーター（Huawei Lite Wearable Simulator）を使うしかありません。
* Wearableで動作するからといって、Lite Wearableでも動作するとは限りません。必ずシミュレーター（Huawei Lite Wearable Simulator）で動作確認した上、Lite Wearableの実機で動作を確認しましょう。
* config.jsonのmodule > deviceTypeでは、liteWearableとwearableを両方入れました。しかし、実際に運用するときに、プロジェクトを２つに分け、module > deviceTypeにliteWearableまたはwearableのみを設定することをお勧めします。特にWear Engineを導入する際、WearableのWear Engine SDKとLite WearableのWear Engine SDKは違うものになります。同時に導入すると、プロジェクトがビルドできなくなります。
## 参考資料
* Lite Smart Watches (https://developer.huawei.com/consumer/en/doc/best-practices/bpta-lite-wearable-guide)
* JavaScript-compatible Web-like Development Paradigm (ArkUI.Lite) (https://developer.huawei.com/consumer/en/doc/harmonyos-references/arkui-js-lite-comp)
* ライフサイクル (https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-lite-framework-lifecycle)
* 多言語対応 (https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-lite-framework-localization)
* 利用可能なCSSのプロパティ (https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-lite-common-styles)
* stackタグ (https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-lite-components-container-stack)
* divタグ (https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-lite-components-container-div)
* imageタグ (https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-lite-components-basic-image)
* inputタグ (https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-lite-components-basic-input)
* textタグ (https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-lite-components-basic-text)
* @system.router (https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-apis-system-router)
* Wearable App Development (JS) (https://developer.huawei.com/consumer/en/doc/connectivity-Guides/fitnesswatch-dev-0000001051423561)