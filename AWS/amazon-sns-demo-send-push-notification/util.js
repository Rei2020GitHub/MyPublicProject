module.exports = class Util {
    constructor() {}

    getData(event) {
        if (event
            && event.hasOwnProperty("body")
        ) {
            return event.body;
        }
        
        return null;
    }

    returnResponse(callback, statusCode, body) {
        if (callback && callback instanceof Function) {
            const response = {
                'statusCode': statusCode,
                'headers': {
                    'Content-type': 'application/json'
                },
                'body': JSON.stringify(body)
            }
            callback(null, response);    
        }
    }

    returnErrorResponse(callback, statusCode, error) {
        const body = {
            error: error
        };

        this.returnResponse(callback, statusCode, body);
    }
}