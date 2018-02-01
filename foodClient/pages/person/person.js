//index.js
//获取应用实例
const app = getApp()
Page({
  data: {
    userInfo: {},
    hasUserInfo: false,
    codeGet: {},
    codeGetState: 0
  },
  onLoad: function () {
    // 获取用户信息
    wx.getSetting({
        success: res => {
          if (res.authSetting['scope.userInfo']) {  //取得权限
            wx.getUserInfo({
              success: res => {
                this.setData({
                  'data.userInfo' : res.userInfo,
                  'hasUserInfo': true
                })
              }
            })
          }
        }
    })
  },  
  onShow: function(){
    if (app.codeGetState) {
      this.setData({
        'codeGet': app.codeGet,
        'codeGetState': 1
      })
    } else {
      app.codeGetCallback = res => {
        this.setData({
          'codeGet': JSON.parse(res.data),
          'codeGetState': 1
        })
      }  
    }
  },
  
  navto: function(event){
    if (event.target.id === 'dataBtn') {
      wx.navigateTo({
        url: '../personData/personData'
      })
    }
    else if (event.target.id === 'addrBtn') {
      wx.navigateTo({
        url: '../personAdr/personAdr'
      })
    }
    else if (event.target.id === 'advBtn') {
      wx.navigateTo({
        url: '../personAdv/personAdv'
      })
    }
    else if (event.target.id === 'aboutBtn') {
      wx.navigateTo({
        url: '../personAbout/personAbout'
      })
    }
  }
})
