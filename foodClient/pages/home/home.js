// pages/home/home.js
const app = getApp()
Page({
  /**
   * 页面的初始数据
   */
    data: {
      imgUrls: [
        'http://img02.tooopen.com/images/20150928/tooopen_sy_143912755726.jpg',
        'http://img06.tooopen.com/images/20160818/tooopen_sy_175866434296.jpg',
        'http://img06.tooopen.com/images/20160818/tooopen_sy_175833047715.jpg'
      ],
      indicatorDots: true,
      autoplay: true,
      interval: 5000,
      duration: 800,
      itemArray: [],
      itemData: [],
      botNote: '',
      gou: [],
      total: 0,
      gouHei: 'auto'
    },
    gouHei: function(){
      if (this.data.gouHei=='auto'){
        this.setData({
          'gouHei': '35rpx'
        })
      }
      else{
        this.setData({
          'gouHei': 'auto'
        })
      }
    },
    location: {
      hasLocation: 1,
      latitude : null,
      longitude : null
    },
    imgTap: function(event){ // 顶部图片
      console.log(event.target.id);
    },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    // 获取位置信息
    wx.getLocation({
      type: 'wgs84',
      success: (res) => {
        this.setData({
          'location.hasLocation': 3,
          'location.latitude': parseInt(res.latitude),
          'location.longitude': parseInt(res.longitude)
        })
      },
      fail: () => {
        this.setData({
          'location.hasLocation': 2
        })
      }
    })
    wx.request({
      url: 'https://lizitao.com/foodServer/homedata',
      data: {
        'dataRang': 'first'
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
    //console.log(app.codeGet.openid);
    this.setData({
      'gou': app.data.gou,
      'total': app.data.total
    })
    //console.log(this.data.total);
  },
  addItem: function(e){
    //console.log(e.target.dataset.hi)
    app.data.gou.push(e.target.dataset.hi)
    app.data.total += parseFloat(e.target.dataset.hi.price)
    this.data.gou.push(e.target.dataset.hi)
    this.data.total += parseFloat(e.target.dataset.hi.price)
    this.setData({
      'gou': this.data.gou,
      'total': this.data.total
    })
  },
  delItem: function(e){
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
    //console.log(this.data.itemArray);
    if (this.data.itemArray.length==0){
      this.setData({
        'botNote': "没有更多了"
      })
      return;
    }else{
      this.setData({
        'botNote': "loading..."
      })
    }   
    var itemArrayFra = this.data.itemArray.splice(0,10);
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
  
  },
  loadmore: function(){

  }
})