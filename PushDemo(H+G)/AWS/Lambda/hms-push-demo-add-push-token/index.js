// AWS SDKをロードする
const AWS = require('aws-sdk');
// リージョンを設定する
AWS.config.update({region: 'ap-northeast-1'});
// DynamoDBサービスのオブジェクトを生成
const ddb = new AWS.DynamoDB();
// SNSサービスのオブジェクトを生成
const sns = new AWS.SNS();

exports.handler = async (event) => {
    const response = {};

    // 入力データを検証
    if (!event) {
        response.error = "No data";
        return response;
    }
    // 入力データを検証（トークンがない）
    if (!event.hasOwnProperty("token")) {
        response.error = "No token";
        return response;
    }
    // 入力データを検証（トークンがない）
    if (!event.hasOwnProperty("token_type")) {
        response.error = "No token type";
        return response;
    }

    const token = event.token;
    
    if (event.token_type == "HMS") {
        const params = {
            TableName: 'token_info',
            Item: {
                'token' : {S: token},
                'status' : {N: "0"}
            }
        };
    
        // DynamoDBへ書き込む
        await ddb.putItem(params, function(err, data) {
            if (err) {
                response["error"] = err;
            } else {
                response["result"] = data;
            }
        }).promise();        
    } else if (event.token_type == "FCM") {
        var params = {
            PlatformApplicationArn: 'arn:aws:sns:ap-northeast-1:316869381796:app/GCM/FCM_Push_Demo', /* required */
            Token: token
        };

        await sns.createPlatformEndpoint(params, function(err, data) {
            if (err) {
                console.log(err, err.stack); // an error occurred
                response["error"] = err;
            } else {
                console.log(data);           // successful response
                response["result"] = data;
            }
        }).promise();
    } else {
        response.error = "Invalid token type";
    }

    return response;
};
