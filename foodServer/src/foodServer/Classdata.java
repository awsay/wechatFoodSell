package foodServer;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * Servlet implementation class Classdata
 */
@WebServlet("/Classdata")
public class Classdata extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Classdata() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */

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
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		OutputStream out = response.getOutputStream();
		
		String dataType = request.getParameter("dataType");
		System.out.println(dataType);
		//数据库操作
		Connection conn = null;
		PreparedStatement stat = null;
		PreparedStatement statInd = null;  
		ResultSet rs = null;
		ResultSet rsInd = null;
		try {
			Class.forName(jdbc_driver);
			conn = (Connection) DriverManager.getConnection(jdbc_url,jdbc_user,jdbc_pw);
			String sql1 = "SELECT itemId FROM fooditem where type = ?";		
			String sql2 = "SELECT * FROM fooditem where type = ? limit 10";	
			int itemArrayLen;
			String foodItemStr;
			if(dataType!=null){
				statInd = (PreparedStatement) conn.prepareStatement(sql1);
				stat = (PreparedStatement) conn.prepareStatement(sql2);
				statInd.setString(1, dataType);
				stat.setString(1, dataType);
				rsInd = statInd.executeQuery();
				rs = stat.executeQuery();				
				StringBuffer itemArray = new StringBuffer();
			    itemArray.append("[");	
			    rsInd.absolute(10);
				while(rsInd.next()){
					itemArray.append(rsInd.getInt("itemId")+",");	
				}
				itemArrayLen = itemArray.length();
				if(itemArrayLen>1){
					itemArray.delete(itemArrayLen-1, itemArrayLen);	
				}				
				itemArray.append("]");
				String itemArrayStr = itemArray.toString();
				System.out.println(itemArrayStr);  //返回给客户端的id字符串
				foodItemStr = Homedata.rsToobj(rs);
				System.out.println(foodItemStr);    //返回前十条数据
				String resStr = "{\"itemArrayStr\":\""+itemArrayStr+"\",\"foodItemStr\":"+foodItemStr+"}";
				out.write(resStr.getBytes("utf-8"));
			}
			else{
				out.write("error".getBytes("utf-8"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
		          if(rs != null) {
		              rs.close();
		          }
		          if(rsInd != null){
		        	  rsInd.close();
		          }
		      } catch (SQLException e) {
		          e.printStackTrace();
		      } finally {
		          try {
		              if(stat != null) {
		                  stat.close();
		              }
		              if(statInd != null){
		            	  statInd.close();
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
