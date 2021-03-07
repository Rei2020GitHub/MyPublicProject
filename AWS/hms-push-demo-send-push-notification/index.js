const Util = require("./util.js");
const PushUtil = require("./pushUtil.js");

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
    } else if (!pushData.hasOwnProperty("title")) {
        // タイトルがない
        util.returnErrorResponse(callback, 400, "No title");
    } else if (!pushData.hasOwnProperty("body")) {
        // 本文がない
        util.returnErrorResponse(callback, 400, "No body");
    } else if (!pushData.hasOwnProperty("token") || !Array.isArray(pushData.token) || pushData.token.length < 1) {
        // 送信先がない
        util.returnErrorResponse(callback, 400, "No push target");
    } else {
        // プッシュサーバーにアクセスするためのプッシュトークンを認証サーバーから取得する
        const getAccessTokenPromise = pushUtil.createGetAccessTokenPromise();
        getAccessTokenPromise.then((result) => {
            // ログ出力
            console.info("https://oauth-login.cloud.huawei.com/oauth2/v3/token = " + result);
            
            let accessTokenJson = null;
            try {
                accessTokenJson = JSON.parse(result);
            } catch (exception) {
                util.returnErrorResponse(callback, 500, exception);
            }
            
            // レスポンスを検証する
            if (!accessTokenJson) {
                // レスポンスがない
                util.returnErrorResponse(callback, 500, "No access token response");
            } else if (!accessTokenJson.hasOwnProperty("access_token")) {
                // アクセストークンがない
                util.returnErrorResponse(callback, 500, "No access token");
            } else if (!accessTokenJson.hasOwnProperty("token_type")) {
                // アクセストークンの種類が不明
                util.returnErrorResponse(callback, 500, "Unknown access token type");
            } else if (accessTokenJson.token_type != "Bearer") {
                // アクセストークンの種類が違う
                util.returnErrorResponse(callback, 500, "Incorrect access token type");
            } else {
                // アクセストークン
                const accessToken = accessTokenJson.access_token;
                // タイトル
                const pushTitle = pushData.title;
                // 本文
                const pushBody = pushData.body;
                // 送信対象
                const pushToken = pushData.token;
                
                // プッシュ通知を送る
                const pushNotificationPromise = pushUtil.createPushNotificationPromise(accessToken, pushTitle, pushBody, pushToken);
                pushNotificationPromise.then((result) => {
                    // ログ出力
                    console.info("https://push-api.cloud.huawei.com/v2/736430079244623135/messages:send = " + result);
                    
                    let pushNotificationJson = null;
                    try {
                        pushNotificationJson = JSON.parse(result);
                    } catch (exception) {
                        util.returnErrorResponse(callback, 500, exception);
                    }
                    
                    // レスポンスを検証する
                    if (!pushNotificationJson) {
                        // レスポンスがない
                        util.returnErrorResponse(callback, 500, "No push notification response");
                    } else if (!pushNotificationJson.hasOwnProperty("code")) {
                        // 送信結果がない
                        util.returnErrorResponse(callback, 500, "No push notification result");
                    } else if (pushNotificationJson.result != "80000000" && pushNotificationJson.result != "80100000") {
                        // アクセストークンの種類が不明
                        util.returnErrorResponse(callback, 500, "Push failed");
                    } else {
                        util.returnResponse(callback, 200, result);
                    }
                }).catch((error) => {
                    util.returnErrorResponse(callback, 500, error);
                });
            }
        }).catch((error) => {
            util.returnErrorResponse(callback, 500, error);
        });
    }
}