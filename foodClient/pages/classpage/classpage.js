// pages/classpages/classpage.js
const app = getApp()
Page({
  /**
   * 页面的初始数据
   */
  data: {
    itemArray: [],
    itemData: [],
    botNote: '',
    topTitle: '',
    gou: [],
    total: 0,
    gouHei: 'auto'
  },
  gouHei: function () {
    if (this.data.gouHei == 'auto') {
      this.setData({
        'gouHei': '35rpx'
      })
    }
    else {
      this.setData({
        'gouHei': 'auto'
      })
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      'topTitle': options.type
    })
    wx.request({
      url: 'https://lizitao.com/foodServer/classdata',
      data: {
        'dataType': options.type
      },
      dataType: 'txt',
      success: res => {
        var resObj = JSON.parse(res.data);
        this.setData({
          'itemArray': JSON.parse(resObj.itemArrayStr),
          'itemData': resObj.foodItemStr
        })
      }
    })
   // console.log(options.type)
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
  
  },
  addItem: function (e) {
   // console.log(e.target.dataset.hi)
    app.data.gou.push(e.target.dataset.hi)
    app.data.total += parseFloat(e.target.dataset.hi.price)
    this.data.gou.push(e.target.dataset.hi)
    this.data.total += parseFloat(e.target.dataset.hi.price)
    this.setData({
      'gou': this.data.gou,
      'total': this.data.total
    })
  },
  delItem: function (e) {
    app.data.total -= parseFloat(app.data.gou[e.target.dataset.del].price)
    app.data.gou.splice(e.target.dataset.del, 1)
    this.data.total -= parseFloat(this.data.gou[e.target.dataset.del].price)
    this.data.gou.splice(e.target.dataset.del, 1)
    this.setData({
      'gou': this.data.gou,
      'total': this.data.total
    })
  },
  payment: function () {
    if (app.codeGetState === 0) {
      console.log("用户未登录");
      return;
    }
    if (!app.codeGet.phone) {
      console.log("请完善个人资料");
      return;
    }
    wx.request({
      url: 'https://lizitao.com/foodServer/payment',
      method: 'GET',
      data: {
        'openid': app.codeGet.openid,
        'price': app.data.total
      },
      dataType: 'txt',
      success: res => {
        var resObj = JSON.parse(res.data);
        console.log(resObj);
        wx.requestPayment(
          {
            'timeStamp': resObj.timeStamp,
            'nonceStr': resObj.nonceStr,
            'package': resObj.package,
            'signType': 'MD5',
            'paySign': resObj.paySign,
            'success': function (res) { },
            'fail': function (res) {
              console.log(res)
            },
            'complete': (res) => { 
              this.data.total = 0
              this.data.gou = []
              this.setData({
                'gou': this.data.gou,
                'total': this.data.total
              })
              app.data.total = 0
              app.data.gou = []
            }
          })
        //返回验证码是否发送成功 resObj.statement==1为成功，等于0位失败
      }
    })
  },
  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    this.setData({
      'gou': app.data.gou,
      'total': app.data.total
    })
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
    //用户返回，清除页面数据
    this.setData({
      itemArray: [],
      itemData: [],
      botNote: '',
      topTitle: ''
    })
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
    if (this.data.itemArray.length == 0) {
      this.setData({
        'botNote': "没有更多了"
      })
      return;
    } else {
      this.setData({
        'botNote': "loading..."
      })
    }
    var itemArrayFra = this.data.itemArray.splice(0, 10);
    wx.request({
      url: 'https://lizitao.com/foodServer/homedata',
      data: {
        'dataRang': itemArrayFra
      },
      dataType: 'txt',
      success: res => {
        var resObj = JSON.parse(res.data);
        this.data.itemData = this.data.itemData.concat(resObj.foodItemStr);
        this.setData({
          'itemData': this.data.itemData,
          'botNote': ""
        })
      }
    })
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
  
  }
})