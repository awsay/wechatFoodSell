package foodServer;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import java.io.*;

/**
 * Servlet implementation class Payment
 */

public class Payment extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Payment() {
        super();
        
        // TODO Auto-generated constructor stub
       
    }
   
    public static String sendPost2(String url, String data) {
    	try {
            //String xml = "<?xml version='1.0' encoding='UTF-8'?><group><name>周成林</name><age>22</age><Image>我们</Image></group>";
            // 创建url资源
            URL realUrl = new URL(url);
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            //转换为字节数组
            byte[] dataByte = data.getBytes("utf-8");
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(dataByte.length));
            // 设置文件类型:
            conn.setRequestProperty("contentType", "text/xml");
            // 开始连接请求
            conn.connect();
            OutputStream  out = conn.getOutputStream();     
            // 写入请求的字符串
            out.write(dataByte);
            out.flush();
            out.close();
            System.out.println(conn.getResponseCode());
            // 请求返回的状态
            if (conn.getResponseCode() == 200) {
                System.out.println("连接成功");
                // 请求返回的数据
                InputStream in = conn.getInputStream();
                String a = null;
                String b = null;
                try {
                    byte[] data1 = new byte[in.available()];
                    in.read(data1);
                    // 转成字符串
                    a = new String(data1);
                    b = new String(a.getBytes("GBK"),"utf-8");
                    System.out.println(b);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                return b;
            } else {
                System.out.println("no++");
                return "";
            }
        } catch (Exception e) {
        	return "";
        }
    }

    
     public static String GetIp() {  //获取终端ip
        InetAddress ia=null;  
        try {  
            ia=InetAddress.getLocalHost();  
            String localip=ia.getHostAddress();  
            return localip;  
        } catch (Exception e) {  
            return null;  
        }  
    }
     
     private static String randomChar = "1234567890ZBCDEFGHIJKLMNOPQRSTUVWXYZ";  
     private static String randomNum = "1234567890";
     private static int getRandom(int count){
    	 return (int) Math.round(Math.random() * (count));
     }
     public static String NonceStr(){  // 产生随机字符串  数字/字母
         StringBuffer sb = new StringBuffer();
         int len = randomChar.length();
         for (int i = 0; i < 32; i++) {
             sb.append(randomChar.charAt(getRandom(len-1)));
         }
         return sb.toString();
     }    
     
     public static String GetMapToXML(Map<String,String> param){  //map转xml
         StringBuffer sb = new StringBuffer();  
         sb.append("<xml>");  
         for (Map.Entry<String,String> entry : param.entrySet()) {   
                sb.append("<"+ entry.getKey() +">");  
                sb.append(entry.getValue());  
                sb.append("</"+ entry.getKey() +">");  
        }    
         sb.append("</xml>");  
         return sb.toString();  
     }
       
     
     public static String out_trade_no(){
    	  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	  Date date = new Date();
    	  String timeStr = simpleDateFormat.format(date);
    	  StringBuffer sb = new StringBuffer();
    	  sb.append(timeStr);
          int len = randomNum.length();
          for (int i = 0; i < 14; i++) {
              sb.append(randomNum.charAt(getRandom(len-1)));
          }
          return sb.toString();   	 
     }
     
     //map转URL字符串
     public static String mapToFormData(Map<String, String> map, boolean isURLEncoder) throws UnsupportedEncodingException {
    	    String formData = "";
    	    if (map != null && map.size() > 0) {
    	        formData = Joiner.on("&").withKeyValueSeparator("=").join(map);
//    	        if (isURLEncoder) {
//    	        	formData = URLEncoder.encode(formData, "UTF-8");
//    	        }
    	    }
    	    return formData;
     }
     
     public static String GetSign(Map<String,String> param) throws UnsupportedEncodingException{
    	 TreeMap<String, String> StringA = new TreeMap<String, String>(param);
         //System.out.println("排序后的map:"+StringA);
         String StringB = mapToFormData(StringA,false);
         //System.out.println("连接起来的map:"+StringB);
		 String stringSignTemp = MD5.MD5Encode(StringB+"&key=QWERTYUIOPasdfghjklzxcvbnm123456").toUpperCase();  
         return stringSignTemp;  
      } 
  
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("content-type", "application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		OutputStream out = response.getOutputStream();
		
	    String openid = request.getParameter("openid");
	    System.out.println(openid);
		//String openid = "oTB8k0ZwJJ6z-g-hhy3PB0fLn14";
		String total_fee = "10";
		String out_trade_no = out_trade_no();
		String NonceStr = NonceStr();
		String GetIp = GetIp();
		String shanghu="武汉市校青春科技有限责任公司-食品";
		//String GetIp = "127.0.0.1";
		Map<String,String> param = new HashMap<String,String>();  
		param.put("appid", "wxf3780089591094c4");
		param.put("mch_id", "1494117912");
		param.put("nonce_str", NonceStr);
		param.put("body",shanghu );
		param.put("out_trade_no",out_trade_no);  //订单号
		param.put("total_fee", total_fee);
		//param.put("spbill_create_ip",GetIp);
		param.put("spbill_create_ip","119.103.232.238");
		param.put("notify_url","http://lizitao.com/foodServer/notifyurl");  
		param.put("trade_type","JSAPI");
		param.put("openid", openid);
		
		String sign = GetSign(param);
		//System.out.println(sign);
		param.put("sign",sign);
		String paramXml = GetMapToXML(param);
		String resStr = sendPost2("https://api.mch.weixin.qq.com/pay/unifiedorder",paramXml);
		//这里开工
		byte[] test = resStr.getBytes();  
		String filename = out_trade_no+".xml";
		String dr="D:/";
		File file=new File(dr,filename );
		if(!file.exists()){
			System.out.println("文件不存在，可以创建");
			FileOutputStream outStream;
			try {
				outStream = new FileOutputStream(file);
				FileOutputStream outStream1 = new FileOutputStream(file);    //文件输出流用于将数据写入文件  
		        outStream1.write(test);  
		        outStream1.close();
		        // 开始读文件
		        
		         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();   
		         try {
		             //创建DocumentBuilder对象
		             DocumentBuilder db = dbf.newDocumentBuilder();
		             //通过documentBuilder对象 的parser方法加载books。xml文件到当前项目下
		             org.w3c.dom.Document document = db.parse(dr+filename);
		            
		             NodeList return_code = document.getElementsByTagName("return_code");
		             String return_codevalue = ((Text) (return_code.item(0).getFirstChild())).getData().trim();
		             NodeList zan_code;
		             Map<String, String> resMap1 = new HashMap<String, String>();
		             if("SUCCESS".equals(return_codevalue)){
		            	 zan_code = document.getElementsByTagName("appid");
		            	 resMap1.put("appid", ((Text) (zan_code.item(0).getFirstChild())).getData().trim());
		            	 resMap1.put("timeStamp", String.valueOf(System.currentTimeMillis()));
		            	 //zan_code = document.getElementsByTagName("nonce_str");
		            	 //resMap1.put("nonceStr", ((Text) (zan_code.item(0).getFirstChild())).getData().trim());
		            	 resMap1.put("nonceStr", NonceStr);
		            	 zan_code = document.getElementsByTagName("prepay_id");
		            	 resMap1.put("package", "prepay_id="+((Text) (zan_code.item(0).getFirstChild())).getData().trim());
		            	 resMap1.put("signType","MD5");
		            	 String paySign = GetSign(resMap1);
		            	 resMap1.put("paySign", paySign);
		            	 JSONObject resJSON = JSONObject.parseObject(JSON.toJSONString(resMap1));
		            	 System.out.println(resJSON.toString());
		            	 out.write(resJSON.toString().getBytes("utf-8"));
		            	 out.close();
		            	 System.gc();
		            	 file.getAbsoluteFile().delete();
		            	 if(file.exists()){
		            		 System.out.println("他妈的还在！");
		            	 }		            	 
		             }		         
		         } catch (ParserConfigurationException e) {
		             // TODO Auto-generated catch block
		             e.printStackTrace();
		         } catch (SAXException e) {
		             // TODO Auto-generated catch block
		             e.printStackTrace();
		         } catch (IOException e) {
		             // TODO Auto-generated catch block
		             e.printStackTrace();
		         } 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {//文件输出流用于将数据写入文件  
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
		
		
	
	
}

