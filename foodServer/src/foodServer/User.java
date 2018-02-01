/**
 * 
 */
package foodServer;

/**
 * @author Administrator
 *
 */
public class User {
	public User() {
      
    }
	private String session_key;
    private String openid;
    private String name;
    private String sex;
    private String phone;
    private String address;
    private int addnum;
    private String errcode;
	public String getSession_key() {
		return session_key;
	}
	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getAddnum() {
		return addnum;
	}
	public void setAddnum(int addnum) {
		this.addnum = addnum;
	}
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
    
}
