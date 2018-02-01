package foodServer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

/**
 * Servlet implementation class Phonenum
 */
@WebServlet("/Phonenum")
public class Phonenum extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static String generateVer(){
    	Random rad=new Random();     
        String result  = rad.nextInt(1000000) +"";  
        if(result.length()!=6){  
            return generateVer();  
        }  
        return result;  
    }
	private void sendVer(String phone,String code) throws Exception {
		 TaobaoClient client = new DefaultTaobaoClient("https://eco.taobao.com/router/rest", "23761520", "13c1e58a94067e5f7f21daf7153584be");
		 AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		 req.setExtend( "" );
		 req.setSmsType( "normal" );
		 req.setSmsFreeSignName( "青春校驿站" );
		 req.setSmsParamString( "{code:\""+code+"\"}" );
		 req.setRecNum( phone );
		 req.setSmsTemplateCode( "SMS_62750278" );
		 AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
		 System.out.println(rsp.getBody()); 
	}
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Phonenum() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
    String jdbc_driver;
    String jdbc_url;
    String jdbc_user;
    String jdbc_pw;
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		ServletContext context = this.getServletContext(); 
		jdbc_driver = context.getInitParameter("jdbc_driver");
		jdbc_url = context.getInitParameter("jdbc_url");
		jdbc_user = context.getInitParameter("jdbc_user");
		jdbc_pw = context.getInitParameter("jdbc_pw");
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		OutputStream out = response.getOutputStream();
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		session.setMaxInactiveInterval(5*60);
		//System.out.println(session.getAttribute("verNum"));
		String type = request.getParameter("type");
		String openid = "";
		String phone = "";
		String verNumInp = "";
		System.out.println(type);
		if("1".equals(type)){
			String verNum = generateVer();
			openid = request.getParameter("openid");
			phone = request.getParameter("phone");
			session.setAttribute("openid", openid);
			session.setAttribute("phone", phone);
			session.setAttribute("verNum", verNum);
			try {
				sendVer(phone,verNum);
				System.out.println("验证码已发送"+verNum);
				String res = "{\"statement\":\"1\",\"sessionId\":\""+sessionId+"\"}";
				out.write(res.getBytes("utf-8"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				out.write("{\"statement\":\"0\"}".getBytes("utf-8"));
			}
		}
		else if("2".equals(type)){
			verNumInp = request.getParameter("yanInp");
			if(verNumInp.equals(session.getAttribute("verNum"))){
				String openidS = (String) session.getAttribute("openid");
				Connection conn = null;
				PreparedStatement stat = null;
				try {
					Class.forName(jdbc_driver);
					conn = (Connection) DriverManager.getConnection(jdbc_url,jdbc_user,jdbc_pw);
					String sql = "update personal set phone=? where openid=?";			
					stat = (PreparedStatement) conn.prepareStatement(sql);
					stat.setString(1, verNumInp);
					stat.setString(2, openidS);
					int i = stat.executeUpdate();
					String resStr;
					if(i==0){
						resStr = "{\"statement\":\"3\"}";	
					}
					else{
						resStr = "{\"statement\":\"1\"}";
					}
					out.write(resStr.getBytes("utf-8"));
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					out.write("{\"statement\":\"3\"}".getBytes("utf-8"));
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					out.write("{\"statement\":\"3\"}".getBytes("utf-8"));
					e.printStackTrace();
				} finally {
					try {
			              if(stat != null) {
			                  stat.close();
			              }
			          } catch (SQLException e) {
			              e.printStackTrace();
			          } finally {
			              try {
			                  if (conn != null) {
			                      conn.close();
			                  }
			              } catch (SQLException e) {
			                  e.printStackTrace();
			              }
			          }
					out.close();
				} 
				
				out.write("{\"statement\":\"1\"}".getBytes("utf-8"));
			}
			else{
				out.write("{\"statement\":\"2\"}".getBytes("utf-8"));
			}
		}
		else {
			out.write("{\"statement\":\"404\"}".getBytes("utf-8"));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

}
