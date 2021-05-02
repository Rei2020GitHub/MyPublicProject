exports.handler = async (event) => {
    const content = '<!DOCTYPE html><html><script type="text/javascript">function send() { const selectElement = document.getElementById("platform");const selectedValues = Array.from(selectElement.selectedOptions).map(option => option.value); const title = document.getElementById("title").value; const body = document.getElementById("body").value; const data = {}; if (selectedValues.includes("GCM")) { data.default = ""; data.GCM = JSON.stringify({ notification: { title: title, body: body } }); } if (selectedValues.includes("HMS")) { data.HMS = { title: title, body: body, token: [ "IQAAAACy0ZkDAACDokH_ptc7CpYqb29Hz_H3lfK297LpuvHtjE8-NMUHqrVAgKm3ChQDqrgbHteWfWKH3AJywLK9Ra1gsiJE1i57JDhWIij5utGLeA" ] } } const xhr = new XMLHttpRequest();xhr.open("POST", "https://cwbqgobeni.execute-api.ap-northeast-1.amazonaws.com/default/amazon-sns-demo-send-push-notification"); xhr.setRequestHeader("x-api-key", "ayZpqb671Z1XcPdNa4FVD5ULOqOQ35eyagU76f8v"); xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); xhr.onload = () => { console.log(xhr.status); }; xhr.onerror = () => { console.log(xhr.status); }; xhr.send(JSON.stringify(data)); }</script><body><h2>プッシュ通知配信システム</h2><p>対象プラットフォームを選択してください</p><div id="form" name="form"> <label for="platform">対象プラットフォーム:</label> <br> <select multiple id="platform" name="platform"> <option value="GCM">GCM</option> <option value="HMS">HMS</option> </select> <br> <br> <label for="title">配信タイトル:</label> <br> <input type="text" id="title" name="title"> <br> <br> <label for="body">配信内容:</label> <br> <input type="text" id="body" name="body"> <br> <br> <button type="submit" onclick="send()">配信</button></div></body></html>';
    
    const response = {
        statusCode: 200,
        headers: {
            'Content-type': 'text/html; charset=utf-8'
        },
        body: content,
    };
    return response;
};
