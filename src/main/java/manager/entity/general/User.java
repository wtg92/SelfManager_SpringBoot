package manager.entity.general;


import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import manager.system.DBConstants;
import manager.system.Gender;
import manager.system.converter.GenderConverter;
import manager.util.CommonUtil;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Table(name = DBConstants.T_USER)
@DynamicInsert
@DynamicUpdate
public class User extends SMGeneralEntity{
	
	private static final long serialVersionUID = 4205890933324878246L;

	@Column
	private String account;
	
	@Column
	private String password;
	
	@Column
	private String email;
	

	@Column
	@Convert(converter = GenderConverter.class)
	private Gender gender = Gender.UNDECIDED;
	
	@Column
	private String nickName;
	

	@Column
	private String motto;
	
	@Column
	private String telNum;

	@Column
	private String alipayOpenId;
	
	@Column
	private String pwdSalt;

	@Column
	private Long portraitId;

    @Column Long sessionVersion;

	@Override
	public User clone() {
		return CommonUtil.deepClone(this);
	}
	
	/*======================== Auto-Genrated Code==================================*/

    public Long getSessionVersion() {
        return sessionVersion;
    }

    public void setSessionVersion(Long sessionVersion) {
        this.sessionVersion = sessionVersion;
    }

    public String getAlipayOpenId() {
		return alipayOpenId;
	}

	public void setAlipayOpenId(String alipayOpenId) {
		this.alipayOpenId = alipayOpenId;
	}

	public Long getPortraitId() {
		return portraitId;
	}

	public void setPortraitId(Long portraitId) {
		this.portraitId = portraitId;
	}

	public String getPwdSalt() {
		return pwdSalt;
	}
	public void setPwdSalt(String pwdSalt) {
		this.pwdSalt = pwdSalt;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getMotto() {
		return motto;
	}
	public void setMotto(String motto) {
		this.motto = motto;
	}
	public String getTelNum() {
		return telNum;
	}
	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}




}
