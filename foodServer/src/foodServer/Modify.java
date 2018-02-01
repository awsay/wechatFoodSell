package foodServer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * Servlet implementation class Modify
 */

public class Modify extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Modify() {
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
	@SuppressWarnings("resource")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		OutputStream out = response.getOutputStream();
		String openid = request.getParameter("openid");
		String modifyType = request.getParameter("modifyType");
		String name = request.getParameter("name");
		String sex = request.getParameter("sex");
		String address = request.getParameter("address");
System.out.println(openid);
		if(openid==null){
			out.write("{\"statement\":\"0\"}".getBytes("utf-8"));
			out.close();
			return;
		}
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet rs = null;
		try {
			Class.forName(jdbc_driver);
			conn = (Connection) DriverManager.getConnection(jdbc_url,jdbc_user,jdbc_pw);
			String resStr;
			if("modify1".equals(modifyType)){	
				String sql = "update personal set name=?, sex=? where openid=?";			
				stat = (PreparedStatement) conn.prepareStatement(sql);
				stat.setString(1, name);
				stat.setString(2, sex);
				stat.setString(3, openid);
				int i = stat.executeUpdate();	
				if(i==0){
					resStr = "{\"statement\":\"0\"}";	
				}
				else{
					resStr = "{\"statement\":\"1\"}";
				}
				out.write(resStr.getBytes("utf-8"));		
			}
			if("modify2".equals(modifyType)){
				String sqlSel = "SELECT address FROM personal where openid = ?";
				String sqlUpd = "update personal set address=? where openid=?";
				stat = (PreparedStatement) conn.prepareStatement(sqlSel);
				stat.setString(1, openid);
				rs = stat.executeQuery();
				if(rs.next()){
				   String addressIni = rs.getString("address");
				   String addressNew;
				   if(addressIni != null){
					  addressNew = addressIni+address+";";
				   }
				   else{
					  addressNew = address+";";   
				   }
				   stat = (PreparedStatement) conn.prepareStatement(sqlUpd);
				   stat.setString(1, addressNew);
				   stat.setString(2, openid);
				   int i = stat.executeUpdate();	
				   if(i==0){
					  resStr = "{\"statement\":\"0\"}";	
				   }
				   else{
					  resStr = "{\"statement\":\"1\"}";
				   }
				}
				else{
				   resStr = "{\"statement\":\"0\"}";	
				}
				out.write(resStr.getBytes("utf-8"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
