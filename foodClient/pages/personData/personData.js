// pages/personData/personData.js
const app = getApp()
Page({
  data: {
    name: '',
    sex: '',
    phone: '未绑定',
    isModify: false,
    isPhoneModify: false,
    nameInp:'',
    sexInp:'',
    phoneInp:'',
    yanInp:'',
    buttonDis:false,
    miao: '获取验证码',
    min: 60
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if(app.codeGet.name){
      this.setData({
        'name': app.codeGet.name,
        'nameInp': app.codeGet.name
      })
    }
    if (app.codeGet.sex) {
      this.setData({
        'sex': app.codeGet.sex,
        'sexInp': app.codeGet.sex
      })
    }
    if (app.codeGet.phone) {
      this.setData({
        'phone': app.codeGet.phone
      })
    }
       
  },
  changeisModify: function(){
     this.data.isModify = !this.data.isModify;
     this.setData({
       'isModify': this.data.isModify
     })
  },
  changeIsPhoneModify: function(){
    this.data.isPhoneModify = !this.data.isPhoneModify;
    this.setData({
      'isPhoneModify': this.data.isPhoneModify
    })
  },
  bindNameInput: function (e) {    
      this.data.nameInp = e.detail.value
  },
  bindSexInput: function (e) {
    this.data.sexInp = e.detail.value
  },
  bindPhomeInput: function (e) {
    this.setData({
      'phoneInp': e.detail.value
    })
  }, 
  bindYanInput: function (e) {
    this.data.yanInp = e.detail.value
  },
  getyan: function(){
    if (!app.codeGet.openid){
      return;
    } 
    if (this.data.phoneInp.length != 11){
      wx.showModal({
        title: '提示',
        content: '手机号格式不正确',
      })
      return;
    } 
    this.setData({
      'buttonDis': true,
      'miao': '60s'
    })
    var timer = setInterval(() => {
        this.data.min -= 1;
        this.setData({
          miao: this.data.min + 's'
        })
        if(this.data.min <= 0){
          clearInterval(timer);
          this.data.min = 60;
          this.setData({
            'buttonDis': false,
            'miao': '获取验证码'
          })
        }
    },1000)
    wx.request({
      url: 'https://lizitao.com/foodServer/phonenum',
      method: 'GET',
      data: {
        'type': 1,
        'openid': app.codeGet.openid,
        'phone': this.data.phoneInp
      },
      dataType: 'txt',
      success: res => {
        //console.log(res.data);
        var resObj;
        try{
          resObj = JSON.parse(res.data);
        }catch(e){
          resObj = {"statement": 0}
        }
        if (resObj.statement == 1){
          app.data.sessionId = resObj.sessionId;
          //console.log("验证码发送成功,sessionid为" + resObj.sessionId);
        }
        else{
          this.setData({
            'butNote': '验证码发送失败'
          })
          //console.log("验证码发送失败");
        }
        //返回验证码是否发送成功 resObj.statement==1为成功，等于0位失败
      }
    })
  },
  submit: function(){
    if (!app.codeGet.openid){
      return;
    }
    if (this.data.nameInp != this.data.name || this.data.sexInp != this.data.name){//更新个人信息
      wx.request({
        url: 'https://lizitao.com/foodServer/modify',
        method:'GET',
        data: {
          'modifyType': 'modify1',
          'openid': app.codeGet.openid,
          'name': this.data.nameInp,
          'sex': this.data.sexInp
        },
        dataType: 'txt',
        success: res => {
          var resObj;
          try{
            resObj = JSON.parse(res.data);
          }catch(e){
            resObj = {"statement": 0}
          }
          //根据返回状态提示用户更新成功或失败
          if (resObj.statement==1){
            app.codeGet.name = this.data.nameInp
            app.codeGet.sex = this.data.sexInp
            wx.setStorage({
              key: 'codeGet',
              data: app.codeGet
            })
            if (this.data.yanInp.length != 6) {
              wx.showToast({
                title: '修改成功',
                icon: 'succes',
                duration: 500,
              })
            }
          }
        }
      })
    }
    if(this.data.yanInp.length==6){
      if (!app.data.sessionId){
         return;
      }
      wx.showLoading({
        title: '更新中...',
      })
      wx.request({
        url: 'https://lizitao.com/foodServer/phonenum',
        method: 'GET',
        header: {
          'Cookie': 'JSESSIONID=' + app.data.sessionId
        },
        data: {
          'type': 2,
          'yanInp': this.data.yanInp
        },
        dataType: 'txt',
        success: res => {
         // console.log(res.data);
          var resObj;
          try{
            resObj = JSON.parse(res.data);
          }
          catch(e){
            resObj = {"statement": 3}
          }
          if (resObj.statement==1){
            app.codeGet.phone = resObj.phone
            wx.setStorage({
              key: 'codeGet',
              data: app.codeGet
            })
            wx.hideLoading()
            wx.showToast({
              title: '手机号绑定成功',
              icon: 'succes',
              duration: 1500,
              complete: function () {
                wx.switchTab({
                  url: '/pages/person/person'
                })
              }
            })      
          }
          else if (resObj.statement == 2){
            wx.showModal({
              title: '提示',
              content: '验证码错误',
              success: function (res) {
          
              }
            })
          }
          else{
            wx.showModal({
              title: '提示',
              content: '更改手机号失败',
              success: function (res) {

              }
            })         
          }
          //返回验证码是否正确
        }
      })
    }
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
  
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
  
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
  
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
  
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
  
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
  
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
  
  }
})