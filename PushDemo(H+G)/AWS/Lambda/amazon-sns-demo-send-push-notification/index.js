const PushUtil = require("./pushUtil.js");

exports.handler = function (event, context, callback) {
    const pushUtil = new PushUtil();

    let pushData = event;
    
    // プッシュデータの有効性を検証
    if (!pushData) {
        // プッシュデータのJSONオブジェクトが作れない
        callback(null, { error : "No data" });
    } else if (!(pushData.hasOwnProperty("HMS") || (pushData.hasOwnProperty("GCM") && pushData.hasOwnProperty("default")))) {
        // タイトルがない
        callback(null, { error : "No push content" });
    } else {
        const promiseArray = [];

        if (pushData.hasOwnProperty('HMS')) {
            // Amazon SNSにアクセスするためのPromiseを作成する
            const promise = pushUtil.createHmsPushPromise(pushData.HMS, 'arn:aws:sns:ap-northeast-1:316869381796:HMS_Push_Demo');
            promiseArray.push(promise);
        }
    
        if (pushData.hasOwnProperty('GCM')) {
            // プッシュのパラメータを設定する
            const params = {
                // プッシュ内容
                Message: JSON.stringify({
                    default: pushData.default,
                    GCM: pushData.GCM
                }),
                // 配信プロトコルごとにカスタムペイロードに設定する
                MessageStructure: "json",
                // 対象トピック
                TopicArn: 'arn:aws:sns:ap-northeast-1:316869381796:FCM_Push_demo'
            };

            // Amazon SNSにアクセスするためのPromiseを作成する
            const promise = pushUtil.createPushPromise(params);
            promiseArray.push(promise);
        }
    
        // 全Promiseの実行完了を待つ
        Promise.all(promiseArray).then((result) => {
            // レスポンスを返す
            callback(null, result);
        }).catch((error) => {
            callback(null, { error : error });
        });
    }
}