const https = require('https');
const querystring = require('querystring');

module.exports = class Util {
    constructor() {}

    getData(event) {
        if (event
            && event.hasOwnProperty("Records")
            && Array.isArray(event.Records) 
            && event.Records.length > 0
            && event.Records[0].hasOwnProperty("Sns")
            && event.Records[0].Sns.hasOwnProperty("Message")
        ) {
            return event.Records[0].Sns.Message;
        }
        
        return null;
    }

    createRequestPromise_json(hostname, path, method, headers, data) {
        return new Promise((resolve, reject) => {
            try {
                const stringData = JSON.stringify(data);

                headers["Content-Type"] = "application/json";

                const options = {
                    hostname: hostname,
                    path: path,
                    method: method,
                    rejectUnauthorized: false,
                    headers: headers
                };
        
                const request = https.request(options, (response) => {
                    let data = '';
                    response.on('data', (chunk) => {
                        data += chunk;
                    });
        
                    response.on('end', () => {
                        resolve(data);
                    });
                }).on("error", (error) => {
                    reject(error);
                });
        
                request.write(stringData);
                request.end();
            } catch (exception) {
                reject(exception);
            }
        });
    }

    createRequestPromise_x_www_form_urlencoded(hostname, path, method, headers, data) {
        return new Promise((resolve, reject) => {
            try {
                const postData = querystring.stringify(data);

                headers["Content-Type"] = "application/x-www-form-urlencoded";
                headers["Content-Length"] = postData.length;

                const options = {
                    hostname: hostname,
                    path: path,
                    method: method,
                    headers: headers
                };
        
                const request = https.request(options, (response) => {
                    let data = '';
                    response.on('data', (chunk) => {
                        data += chunk;
                    });
        
                    response.on('end', () => {
                        resolve(data);
                    });
                }).on("error", (error) => {
                    reject(error);
                });
        
                request.write(postData);
                request.end();
            } catch (exception) {
                reject(exception);
            }
        });
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