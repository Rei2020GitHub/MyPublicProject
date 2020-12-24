exports.handler = function (event, context, callback) {
    let query = "";
    let count = 0;
    const keyArray = Object.keys(event.queryStringParameters);
    keyArray.forEach(key => {
        if (count > 0) {
            query += "&";
        }
        query += key + "=" + event.queryStringParameters[key];
        count++;
    });

    const response = {
        'statusCode': 302,
        'isBase64Encoded': false,
        'headers': {
            'Location': 'app://com.sample.hmssample.authdemo/auth?' + query
        }
    }

    callback(null, response);
}