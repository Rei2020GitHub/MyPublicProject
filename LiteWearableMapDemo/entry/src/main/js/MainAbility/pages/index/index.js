// 画面の切り替えで@system.routerを使います。
// https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-apis-system-router
import router from '@system.router'

// マップのメタデータ
const MAP_DATA = {
    "map_1": {
        // ズームアウトのサブマップ（全体図）
        "all": {
            "id": "all",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_all.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 40,
                // 経度
                "longitude": 10
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 10,
                // 経度
                "longitude": 40
            },
            // ズームイン可能なサブマップID
            "sub_map_position": [
                "top_left",
                "top_center",
                "top_right",
                "center_left",
                "center_center",
                "center_right",
                "bottom_left",
                "bottom_center",
                "bottom_right"
            ]
        },
        // ズームインのサブマップ（左上）
        "top_left": {
            "id": "top_left",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_top_left.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 40,
                // 経度
                "longitude": 10
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 30,
                // 経度
                "longitude": 20
            },
            // 右のサブマップID
            "right_map_position": "top_center",
            // 下のサブマップID
            "down_map_position": "center_left"
        },
        // ズームインのサブマップ（上）
        "top_center": {
            "id": "top_center",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_top_center.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 40,
                // 経度
                "longitude": 20
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 30,
                // 経度
                "longitude": 30
            },
            // 左のサブマップID
            "left_map_position": "top_left",
            // 右のサブマップID
            "right_map_position": "top_right",
            // 下のサブマップID
            "down_map_position": "center_center"
        },
        // ズームインのサブマップ（右上）
        "top_right": {
            "id": "top_right",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_top_right.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 40,
                // 経度
                "longitude": 30
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 30,
                // 経度
                "longitude": 40
            },
            // 左のサブマップID
            "left_map_position": "top_center",
            // 下のサブマップID
            "down_map_position": "center_right"
        },
        // ズームインのサブマップ（左）
        "center_left": {
            "id": "center_left",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_center_left.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 30,
                // 経度
                "longitude": 10
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 20,
                // 経度
                "longitude": 20
            },
            // 右のサブマップID
            "right_map_position": "center_center",
            // 上のサブマップID
            "up_map_position": "top_left",
            // 下のサブマップID
            "down_map_position": "bottom_left"
        },
        // ズームインのサブマップ（中央）
        "center_center": {
            "id": "center_center",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_center_center.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 30,
                // 経度
                "longitude": 20
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 20,
                // 経度
                "longitude": 30
            },
            // 左のサブマップID
            "left_map_position": "center_left",
            // 右のサブマップID
            "right_map_position": "center_right",
            // 上のサブマップID
            "up_map_position": "top_center",
            // 下のサブマップID
            "down_map_position": "bottom_center"
        },
        // ズームインのサブマップ（右）
        "center_right": {
            "id": "center_right",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_center_right.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 30,
                // 経度
                "longitude": 30
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 20,
                // 経度
                "longitude": 40
            },
            // 左のサブマップID
            "left_map_position": "center_center",
            // 上のサブマップID
            "up_map_position": "top_right",
            // 下のサブマップID
            "down_map_position": "bottom_right"
        },
        // ズームインのサブマップ（左下）
        "bottom_left": {
            "id": "bottom_left",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_bottom_left.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 20,
                // 経度
                "longitude": 10
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 10,
                // 経度
                "longitude": 20
            },
            // 右のサブマップID
            "right_map_position": "bottom_center",
            // 上のサブマップID
            "up_map_position": "center_left",
        },
        // ズームインのサブマップ（下）
        "bottom_center": {
            "id": "bottom_center",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_bottom_center.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 20,
                // 経度
                "longitude": 20
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 10,
                // 経度
                "longitude": 30
            },
            // 左のサブマップID
            "left_map_position": "bottom_left",
            // 右のサブマップID
            "right_map_position": "bottom_right",
            // 上のサブマップID
            "up_map_position": "center_center",
        },
        // ズームインのサブマップ（右下）
        "bottom_right": {
            "id": "bottom_right",
            // 画像のパス
            "path": "common/map/image/yukiyama_map_bottom_right.png",
            // 画像の高さ（ピクセル）
            "height": 454,
            // 画像の幅（ピクセル）
            "width": 454,
            // 画像の左上の角の情報
            "top_left": {
                // 緯度
                "latitude": 20,
                // 経度
                "longitude": 30
            },
            // 画像の右下の角の情報
            "bottom_right": {
                // 緯度
                "latitude": 10,
                // 経度
                "longitude": 40
            },
            // 左のサブマップID
            "left_map_position": "bottom_center",
            // 上のサブマップID
            "up_map_position": "center_right",
        }
    }
};

// メインマップID
let mapId = 'map_1';
// サブマップID
let mapPosition = 'all';

// 現在地の緯度
let myLatitude = 16;
// 現在地の経度
let myLongitude = 16;

export default {
    // パラメータの宣言
    data: {
        // マップ画像のパス
        mapSrc: 'common/map/image/yukiyama_map_original.jpg',
        // マップの表示状態（表示：flex、非表示：none）
        mapDisplay: 'flex',
        // マップ表示の高さ
        mapHeight: '100%',
        // マップ表示の幅
        mapWidth: '100%',
        // マップの表示位置（X軸）
        mapLeft: '0%',
        // マップの表示位置（Y軸）
        mapTop: '0%',

        // 現在地のアイコンのパス（ズームアウトとズームイン）
        meSrc: 'common/flag/me.png',
        // ズームアウト時の現在地のアイコンの表示状態（表示：flex、非表示：none）
        meDisplay: 'flex',
        // ズームアウト時の現在地のアイコンの高さ
        meHeight: '15%',
        // ズームアウト時の現在地のアイコンの幅
        meWidth: '15%',
        // ズームアウト時の現在地のアイコンの表示位置（X軸、margin-left）
        meLeft: '0%',
        // ズームアウト時の現在地のアイコンの表示位置（X軸、margin-right）
        meRight: '0%',
        // ズームアウト時の現在地のアイコンの表示位置（Y軸、margin-top）
        meTop: '0%',
        // ズームアウト時の現在地のアイコンの表示位置（Y軸、margin-bottom）
        meBottom: '0%',

        // ズームイン時の現在地のアイコンの表示状態（表示：flex、非表示：none）
        subMeDisplay: 'none',
        // ズームイン時の現在地のアイコンの高さ
        subMeHeight: '15%',
        // ズームイン時の現在地のアイコンの幅
        subMeWidth: '15%',
        // ズームイン時の現在地のアイコンの表示位置（X軸、margin-left）
        subMeLeft: '0%',
        // ズームイン時の現在地のアイコンの表示位置（X軸、margin-right）
        subMeRight: '0%',
        // ズームイン時の現在地のアイコンの表示位置（Y軸、margin-top）
        subMeTop: '0%',
        // ズームイン時の現在地のアイコンの表示位置（Y軸、margin-bottom）
        subMeBottom: '0%',

        // 左矢印ボタンの表示状態（表示：flex、非表示：none）
        uiArrowLeftDisplay: 'flex',
        // 右矢印ボタンの表示状態（表示：flex、非表示：none）
        uiArrowRightDisplay: 'flex',
        // 上矢印ボタンの表示状態（表示：flex、非表示：none）
        uiArrowUpDisplay: 'flex',
        // 下矢印ボタンの表示状態（表示：flex、非表示：none）
        uiArrowDownDisplay: 'flex',
        // ズームインボタンの表示状態（表示：flex、非表示：none）
        uiZoomInDisplay: 'flex',
        // ズームアウトボタンの表示状態（表示：flex、非表示：none）
        uiZoomOutDisplay: 'flex'
    },
    // ライフサイクル
    // ページのデータ準備が完了したときに
    onInit() {
        // ここで初期化処理を書く

        // this.mapHeight = '100%';
        // this.mapWidth = '100%';
        // this.mapLeft = '0%';
        // this.mapTop = '0%';
        //
        // this.meSrc = 'common/flag/me.png';
        // this.meHeight = '15%';
        // this.meWidth = '15%';
        // this.meLeft = '0%';
        // this.meTop = '0%';
        // this.meBottom = '0%';
        //
        // this.subMeHeight = '15%';
        // this.subMeWidth = '15%';
        // this.subMeLeft = '0%';
        // this.subMeTop = '0%';
        // this.subMeBottom = '0%';

        // 表示内容を更新する
        this.refreshDisplay();
    },
    // ライフサイクル
    // ページの表示準備が完了したときに
    onReady() {
    },
    // ライフサイクル
    // ページがフォアグラウンドに遷移し、操作可能になるとき
    onShow() {
        // 表示内容を更新する
        this.refreshDisplay();
    },
    // ライフサイクル
    // ページがバックグラウンドに遷移するとき
    onHide() {

    },
    // ライフサイクル
    // ページが破棄されるとき
    onDestroy() {
    },
    // 画面のスワイプイベント
    onSwipe(event) {
        // 左スワイプ
        if (event.direction === 'left') {
            // 設定画面に遷移する
            router.replace({
                uri: 'pages/setting/setting'
            })
        }
        // 右スワイプ
        else if (event.direction === 'right') {

        }
    },
    // 現在地アイコンのクリックイベント
    onClickMyFlag() {
        // ユーザー情報画面に遷移する
        router.replace({
            uri: 'pages/user/user'
        })
    },
    // 左矢印ボタンのクリックイベント
    onClickUiLeft() {
        // 現在のサブマップのメタデータを取得する
        let mapData = MAP_DATA[mapId][mapPosition];
        // サブマップを左のサブマップに切り替える
        mapPosition = mapData.left_map_position;
        // 表示内容を更新する
        this.refreshDisplay();
    },
    // 右矢印ボタンのクリックイベント
    onClickUiRight() {
        // 現在のサブマップのメタデータを取得する
        let mapData = MAP_DATA[mapId][mapPosition];
        // サブマップを右のサブマップに切り替える
        mapPosition = mapData.right_map_position;
        // 表示内容を更新する
        this.refreshDisplay();
    },
    // 上矢印ボタンのクリックイベント
    onClickUiUp() {
        // 現在のサブマップのメタデータを取得する
        let mapData = MAP_DATA[mapId][mapPosition];
        // サブマップを上のサブマップに切り替える
        mapPosition = mapData.up_map_position;
        // 表示内容を更新する
        this.refreshDisplay();
    },
    // 下矢印ボタンのクリックイベント
    onClickUiDown() {
        // 現在のサブマップのメタデータを取得する
        let mapData = MAP_DATA[mapId][mapPosition];
        // サブマップを下のサブマップに切り替える
        mapPosition = mapData.down_map_position;
        // 表示内容を更新する
        this.refreshDisplay();
    },
    // ズームインボタンのクリックイベント
    // 全体図から現在地の部分マップに切り替える
    onClickUiZoomIn() {
        // マップが全体図の場合（全体図のみズームイン可能）
        if (mapPosition === 'all') {
            // 各サブマップに対して、次の処理を行う
            MAP_DATA[mapId].all.sub_map_position.forEach(subMapId => {
                // サブマップのメタデータを取得する
                let subMap = MAP_DATA[mapId][subMapId];
                // 現在地の経緯度とサブマップの経緯度を比較し、現在地がサブマップの中にある場合、次の処理を行う
                if (
                    subMap.bottom_right.latitude <= myLatitude
                    && myLatitude <= subMap.top_left.latitude
                    && subMap.top_left.longitude <= myLongitude
                    && myLongitude <= subMap.bottom_right.longitude
                ) {
                    // 対象のサブマップに切り替える
                    mapPosition = subMapId;
                    // 表示内容を更新する
                    this.refreshDisplay();
                    return;
                }
            });
        }
    },
    // ズームアウトボタンのクリックイベント
    // 部分マップから全体図に切り替える
    onClickUiZoomOut() {
        // マップが全体図でない場合（つまり部分マップの場合）
        if (mapPosition !== 'all') {
            // 全体図に切り替える
            mapPosition = 'all';
            // 表示内容を更新する
            this.refreshDisplay();
        }
    },
    // 表示内容を更新する
    refreshDisplay() {
        // マップ表示のパスを更新する
        this.refreshMapSrc();
        // 現在地アイコンの表示を更新する
        this.refreshMyFlagDisplayPosition();
        this.refreshUi();
    },
    // マップ表示のパスを更新する
    refreshMapSrc() {
        this.mapSrc = MAP_DATA[mapId][mapPosition].path;
    },
    // 現在地アイコンの表示を更新する
    refreshMyFlagDisplayPosition() {
        // 全体図の現在地アイコンをいったん非表示する
        this.meDisplay = 'none';
        // 部分マップの現在地アイコンをいったん非表示する
        this.subMeDisplay = 'none';
        // 現在のサブマップのメタデータを取得する
        let mapData = MAP_DATA[mapId][mapPosition];

        // 現在地の経緯度とサブマップの経緯度を比較し、現在地がサブマップの中にある場合、次の処理を行う
        if (
            mapData.bottom_right.latitude <= myLatitude
                && myLatitude <= mapData.top_left.latitude
                && mapData.top_left.longitude <= myLongitude
                && myLongitude <= mapData.bottom_right.longitude
        ) {
            // サブマップの中心の経緯度を計算する
            let mapCenterLatitude = (mapData.top_left.latitude + mapData.bottom_right.latitude) / 2;
            let mapCenterLongitude = (mapData.top_left.longitude + mapData.bottom_right.longitude) / 2;

            // 現在地アイコンの表示座標を計算する
            let top = Math.round((myLatitude - mapCenterLatitude) / (mapData.bottom_right.latitude - mapCenterLatitude) * 100);
            let left = Math.round((myLongitude - mapCenterLongitude) / (mapData.bottom_right.longitude - mapCenterLongitude) * 100);

            // 表示中のマップが全体図の場合
            if (mapPosition === 'all') {
                // 計算した値（left）がプラスの場合、meLeft（margin-left）を使う。マイナスの場合、meRight（margin-right）を使う
                if (left >= 0) {
                    this.meLeft = left + '%';
                    this.meRight = '';
                } else {
                    this.meLeft = '';
                    this.meRight = -left + '%';
                }

                // 計算した値（top）がプラスの場合、meTop（margin-top）を使う。マイナスの場合、meBottom（margin-bottom）を使う
                if (top >= 0) {
                    this.meTop = top + '%';
                    this.meBottom = '';
                } else {
                    this.meTop = '';
                    this.meBottom = -top + '%';
                }

                // 全体図の現在地アイコンを表示する
                this.meDisplay = 'flex';
            } else
            // 表示中のマップが部分マップの場合
            {
                // 計算した値（left）がプラスの場合、subMeLeft（margin-left）を使う。マイナスの場合、subMeRight（margin-right）を使う
                if (left >= 0) {
                    this.subMeLeft = left + '%';
                    this.subMeRight = '';
                } else {
                    this.subMeLeft = '';
                    this.subMeRight = -left + '%';
                }

                // 計算した値（top）がプラスの場合、subMeTop（margin-top）を使う。マイナスの場合、subMeBottom（margin-bottom）を使う
                if (top >= 0) {
                    this.subMeTop = top + '%';
                    this.subMeBottom = '';
                } else {
                    this.subMeTop = '';
                    this.subMeBottom = -top + '%';
                }

                // 部分マップの現在地アイコンをいったん非表示する
                this.subMeDisplay = 'flex';
            }
        }
    },
    refreshUi() {
        // 現在のサブマップのメタデータを取得する
        let mapData = MAP_DATA[mapId][mapPosition];

        // 矢印の表示表示状態を設定する
        this.uiArrowLeftDisplay = (mapData.left_map_position != undefined && mapData.left_map_position != null ? 'flex' : 'none');
        this.uiArrowRightDisplay = (mapData.right_map_position != undefined && mapData.right_map_position != null ? 'flex' : 'none');
        this.uiArrowUpDisplay = (mapData.up_map_position != undefined && mapData.up_map_position != null ? 'flex' : 'none');
        this.uiArrowDownDisplay = (mapData.down_map_position != undefined && mapData.down_map_position != null ? 'flex' : 'none');

        // ズームボタンの表示状態を設定する
        this.uiZoomInDisplay = (mapPosition === 'all' ? 'flex' : 'none');
        this.uiZoomOutDisplay = (mapPosition === 'all' ? 'none' : 'flex');
    }
};
