package foodServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;


public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Login() {
        // TODO Auto-generated constructor stub
    }
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // �򿪺�URL֮�������
            java.net.URLConnection connection = realUrl.openConnection();
            // ����ͨ�õ���������
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("contentType", "application/x-www-form-urlencoded");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // ����ʵ�ʵ�����
            connection.connect();
            // ��ȡ������Ӧͷ�ֶ�
            //Map<String, java.util.List<String>> map = connection.getHeaderFields();
            // �������е���Ӧͷ�ֶ�
            //for (String key : map.keySet()) {
            //    System.out.println(key + "--->" + map.get(key));
            //}
            // ���� BufferedReader����������ȡURL����Ӧ
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("����GET��������쳣��" + e);
            e.printStackTrace();
        }
        // ʹ��finally�����ر�������
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
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
		// �õ�code����΢�ŷ�������ȡopenID��
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		OutputStream out = response.getOutputStream();
		
		String code = request.getParameter("code");
		//System.out.print(code);
		String reqparam = "appid=wxf3780089591094c4&secret=dac9ac7a859cdb97c586b5e28cf00b15&js_code="+code+"&grant_type=authorization_code";
		String str = sendGet("https://api.weixin.qq.com/sns/jscode2session",reqparam);
		//��¼��ȷ�ķ�������  String str = "{\"session_key\":\"CGyZW0VKkIkCcjuatLBA0g==\",\"openid\":\"9oTB8k0ZwJJ6z-gm-hhy3PB0fLn14\"}";
		//��¼����ķ�������  String strError = "{\"errcode\": 40029,\"errmsg\": \"invalid code\"}";
		User user = JSON.parseObject(str,User.class);
		if(user.getOpenid()==null){  //��¼����������
			String errstr = "{\"errcode\":"+user.getErrcode()+"}"; 
			out.write(errstr.getBytes("utf-8"));
		}
		else{  //��¼�ɹ��õ�openID
			Connection conn = null;
			PreparedStatement stat = null;  
			ResultSet rs = null;
			try {
				Class.forName(jdbc_driver);
				conn = (Connection) DriverManager.getConnection(jdbc_url,jdbc_user,jdbc_pw);
				String sql = "SELECT * FROM personal where openid = ?";
				stat = (PreparedStatement) conn.prepareStatement(sql);
				stat.setString(1, user.getOpenid());
				rs = stat.executeQuery();
				if(rs.next()){
					//System.out.println("�û�����");
					//���û����ݴ����ݿ���ȡ�������ַ�����ʽ����
					user.setName(rs.getString("name"));
					user.setSex(rs.getString("sex"));
					user.setPhone(rs.getString("phone"));
				    user.setAddress(rs.getString("address"));
				    user.setAddnum(rs.getInt("addnum"));
				    user.setSession_key(null);
				    String resUser = JSON.toJSONString(user);
				    out.write(resUser.getBytes("utf-8"));
				} else{  //�û������ڣ�����ȡ��openID��session_key�������ݿ�
					sql = "insert into personal (openid,session_key) values(?,?)";
					stat = (PreparedStatement) conn.prepareStatement(sql);
					stat.setString(1, user.getOpenid());
					stat.setString(2, user.getSession_key());
					int i = stat.executeUpdate();
					//ע���꽫openID���ؿͻ���
					user.setSession_key(null); //΢���ĵ�˵�����Ҫ�����ͻ���
					String resUser = JSON.toJSONString(user);
					out.write(resUser.getBytes("utf-8")); 
					//System.out.println(i+"ע��ɹ�");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
			          if (rs != null) {
			              rs.close();
			          }
			      } catch (SQLException e) {
			          e.printStackTrace();
			      } finally {
			          try {
			              if (stat != null) {
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
			      }
			} //���ݿ��ѯ
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
