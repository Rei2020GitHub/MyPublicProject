const Util = require("./util.js");

const CLIENT_ID = "102839983";
const CLIENT_SECRET = "b855f2e6cda6d835e6d88bc3424c9a52f87ae3cb8ae7a96466bd835d8ce7b514";

module.exports = class MenuContent {

    constructor() {
        this.util = new Util();
    }

    createGetAccessTokenPromise() {
        const hostname = "oauth-login.cloud.huawei.com";
        const path = "/oauth2/v3/token";
        const method = "POST";
        const headers = {};
        const data = {
            grant_type: "client_credentials",
            client_id: CLIENT_ID,
            client_secret: CLIENT_SECRET
        };

        return this.util.createRequestPromise_x_www_form_urlencoded(hostname, path, method, headers, data);
    }

    createPushNotificationPromise(accessToken, title, body, token) {
        const hostname = "push-api.cloud.huawei.com";
        const path = "/v2/736430079244623135/messages:send";
        const method = "POST";
        const headers = {
            Authorization: "Bearer " + accessToken
        };
        const data = {
            validate_only: false,
            message: {
                notification: {
                    title: title,
                    body: body
                },
                android: {
                    notification: {
                        title: title,
                        body: body,
                        click_action: {
                            "type": 1,
                            "intent": "#Intent;compo=com.rvr/.Activity;S.W=U;end"
                        }
                    }
                },
                token: token
            }
        };

        return this.util.createRequestPromise_json(hostname, path, method, headers, data);
    }

}