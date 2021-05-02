const TokenUtil = require("./tokenUtil.js");

// AWS SDKをロードする
const AWS = require('aws-sdk');
// リージョンを設定する
AWS.config.update({region: 'ap-northeast-1'});

// SNSサービスのオブジェクトを生成
const sns = new AWS.SNS();

const CLIENT_ID = "102839983";
const CLIENT_SECRET = "b855f2e6cda6d835e6d88bc3424c9a52f87ae3cb8ae7a96466bd835d8ce7b514";

module.exports = class PushUtil {

    constructor() {
    }

    createPushPromise(params) {
        return new Promise((resolve, reject) => {
            sns.publish(params, (error, data) => {
                if (error) {
                    reject(error);
                    return;
                }
                resolve(data);
            });
        });
    }

    createHmsPushPromise(content, topicArn) {
        return new Promise((resolve, reject) => {
            const tokenUtil = new TokenUtil();
            const getTokenPromise = tokenUtil.createGetTokenPromise();
            
            getTokenPromise.then((result) => {
                if (result.hasOwnProperty("Items") && Array.isArray(result.Items)) {
                    content.token = result.Items.map(function(value) {
                        if (value.hasOwnProperty("token") && value.token.hasOwnProperty("S")) {
                            return value.token.S;
                        }
                        return "";
                    });
                }

                // プッシュのパラメータを設定する
                const params = {
                    // プッシュ内容
                    Message: JSON.stringify(content),
                    // 対象トピック
                    TopicArn: topicArn
                };
                
                sns.publish(params, (error, data) => {
                    if (error) {
                        reject(error);
                        return;
                    }
                    resolve(data);
                });
            }).catch((error) => {
                reject(error);
            });
        });
    }
}