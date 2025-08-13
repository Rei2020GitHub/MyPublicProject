import router from '@system.router'

export default {
    data: {
        title: '',
        switchMyPositionDisplayMessage: '',
        switchMyPositionDisplayValue: '',
        buttonMessage: ''
    },
    // パラメータの初期化はdataでもonInitでもできますが、thisはonInitに入ってからじゃないと生成されないので、
    // this.$t()を使う場合、ライフサイクルがonInitに来てから使う必要がある
    onInit() {
        this.title = this.$t('strings.setting_title');
        this.switchMyPositionDisplayMessage = this.$t('strings.setting_position');
        this.switchMyPositionDisplayValue = 'false';
        this.buttonMessage = this.$t('strings.back');
    },
    onChangeSwitchMyPostionDisplay(event) {

    },
    onClickBack() {
        router.replace({
            uri: 'pages/index/index'
        })
    }
};
