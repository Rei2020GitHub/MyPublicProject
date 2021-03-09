const Util = require("./util.js");
const PushUtil = require("./pushUtil.js");
// AWS SDKをロードする
const AWS = require('aws-sdk');
// リージョンを設定する
AWS.config.update({region: 'ap-northeast-1'});

exports.handler = function (event, context, callback) {
    const util = new Util();
    const pushUtil = new PushUtil();

    let pushData = null;
    
    try {
        // プッシュデータを取得
        const dataString = util.getData(event);

        // ログ出力
        console.info("data = " + dataString);

        // プッシュデータのJSONオブジェクトを生成
        pushData = JSON.parse(dataString);
    } catch (exception) {
        util.returnErrorResponse(callback, 400, exception);
    }    

    // プッシュデータの有効性を検証
    if (!pushData) {
        // プッシュデータのJSONオブジェクトが作れない
        util.returnErrorResponse(callback, 400, "No data");
    } else if (!(pushData.hasOwnProperty("HMS") || (pushData.hasOwnProperty("GCM") && pushData.hasOwnProperty("default")))) {
        // タイトルがない
        util.returnErrorResponse(callback, 400, "No push content");
    } else {
        const promiseArray = [];

        if (pushData.hasOwnProperty('HMS')) {
            // プッシュのパラメータを設定する
            const params = {
                // プッシュ内容
                Message: JSON.stringify(pushData.HMS),
                // 対象トピック
                TopicArn: 'arn:aws:sns:ap-northeast-1:316869381796:HMS_Push_Demo'
            };

            // Amazon SNSにアクセスするためのPromiseを作成する
            const promise = pushUtil.createPushPromise(new AWS.SNS(), params);
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
                TopicArn: 'arn:aws:sns:ap-northeast-1:316869381796:FCM_Push_Demo'
            };

            // Amazon SNSにアクセスするためのPromiseを作成する
            const promise = pushUtil.createPushPromise(new AWS.SNS(), params);
            promiseArray.push(promise);
        }
    
        // 全Promiseの実行完了を待つ
        Promise.all(promiseArray).then((result) => {
            // レスポンスを返す
            util.returnResponse(callback, 200, result);
        }).catch((error) => {
            util.returnErrorResponse(callback, 500, error);
        });
    }
}