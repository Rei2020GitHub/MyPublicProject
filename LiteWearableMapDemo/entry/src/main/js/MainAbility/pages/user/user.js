import router from '@system.router'

export default {
    data: {
        title: '',
        message: '',
        buttonMessage: ''
    },
    onInit() {
        this.title = 'KEIJI OKAMOTO';
        this.message = 'yukiyamaの監修をしています。スノーボードはライフワーク。いつまでも長く滑ります。';
        this.buttonMessage = this.$t('strings.back');
    },
    onClickBack() {
        router.replace({
            uri: 'pages/index/index'
        })
    }
};
