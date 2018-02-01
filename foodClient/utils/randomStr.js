const randomStr = () => {
  var x = "0123456789qwertyuioplkjhgfdsazxcvbnm";
  var tmp = "";
  for (var i = 0; i < 30; i++) {
    tmp += x.charAt(Math.ceil(Math.random() * 100000000) % x.length);
  }
  return tmp;
}
module.exports = {
  randomStr: randomStr
}