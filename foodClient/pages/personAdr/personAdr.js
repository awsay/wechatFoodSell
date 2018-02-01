// pages/personAdr/personAdr.js
const app = getApp()
Page({
  data: {
     addressStr: '',
     addressArray: [],
     inputField: false,
     addressInp:''
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if (app.codeGet.address) {
      this.data.addressStr = app.codeGet.address.substring(0, app.codeGet.address.length-1)
      this.setData({
        'addressArray': this.data.addressStr.split(";")
      })  
    }
  },
  bindTextareaInput: function(e){
    this.data.addressInp = e.detail.value
  },
  add: function(){
    this.setData({
      'inputField': true
    })
  },
  submit: function(){
    if (!this.data.addressInp){
      wx.showModal({
        title: '提示',
        content: '请输入地址'
      })
      return;
    }
    wx.showLoading({
      title: '更新中...',
    })
    wx.request({
      url: 'https://lizitao.com/foodServer/modify',
      method: 'GET',
      data: {
        'modifyType': 'modify2',
        'openid': app.codeGet.openid,
        'address': this.data.addressInp
      },
      dataType: 'txt',
      success: res => {
        var resObj;
        try {
          resObj = JSON.parse(res.data);
        } catch (e) {
          resObj = { "statement": 0 }
        }
        //根据返回状态提示用户更新成功或失败
        if (resObj.statement == 1) {
          if (app.codeGet.address){
            app.codeGet.address += this.data.addressInp + ';'   
          }
          else{
            app.codeGet.address = this.data.addressInp + ';'
          }
          wx.setStorage({
            key: 'codeGet',
            data: app.codeGet
          })
          wx.hideLoading()
          wx.showToast({
            title: '添加成功',
            icon: 'succes',
            duration: 500,
          })
          this.data.addressArray.push(this.data.addressInp);
          this.setData({
            'addressArray': this.data.addressArray,
            'inputField': false
          })   
        }
        else{
          wx.hideLoading()
          wx.showModal({
            title: '提示',
            content: '添加失败'
          })
        }
      }
    })
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