//app.js
App({
  data:{
    sessionId:'',
    gou: [],
    total: 0
  },
  codeGet: {},
  codeGetState: 0,
  onLaunch: function () {
    wx.getStorage({
      key: 'codeGet',
      success: res => {     
          this.codeGet = res.data
          this.codeGetState = 1
      },
      fail: () => {
        // 登录
        wx.login({
          success: res => {
            if (res.code) {
              wx.request({
                url: 'https://lizitao.com/foodServer/login',
                data: {
                  code: res.code
                },
                success: res => {
                  if(!res.data.openid){
                    return;
                  }
                  this.codeGet = res.data
                  this.codeGetState = 1
                  wx.setStorage({
                    key: "codeGet",
                    data: res.data
                  })
                  if (this.codeGetCallback) {
                    this.codeGetCallback(res)
                  }
                }
              })
            }
          }
        })
      }
    })
  },
})