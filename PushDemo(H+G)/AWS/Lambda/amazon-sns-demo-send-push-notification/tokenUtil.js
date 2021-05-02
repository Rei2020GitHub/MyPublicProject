// AWS SDKをロードする
const AWS = require('aws-sdk');
// リージョンを設定する
AWS.config.update({region: 'ap-northeast-1'});

// DynamoDBサービスのオブジェクトを生成
const ddb = new AWS.DynamoDB();

module.exports = class TokenUtil {

    constructor() {
    }

    createGetTokenPromise() {
        return new Promise((resolve, reject) => {
            const params = {
                TableName: 'token_info'
            };
    
            ddb.scan(params, function(error, data) {
                if (error) {
                    reject(error);
                    return;
                }
                resolve(data);
            });
        });
    }
}