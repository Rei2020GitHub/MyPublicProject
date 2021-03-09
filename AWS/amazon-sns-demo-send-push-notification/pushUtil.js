const Util = require("./util.js");

const CLIENT_ID = "102839983";
const CLIENT_SECRET = "b855f2e6cda6d835e6d88bc3424c9a52f87ae3cb8ae7a96466bd835d8ce7b514";

module.exports = class MenuContent {

    constructor() {
        this.util = new Util();
    }

    createPushPromise(sns, params) {
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
}