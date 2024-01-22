package manager.entity.general;


import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.system.Gender;
import manager.system.SMDB;
import manager.system.converter.GenderConverter;

import java.util.Calendar;


@Entity
@Table(name = SMDB.T_USER)
@DynamicInsert
@DynamicUpdate
public class User extends SMGeneralEntity{
	
	private static final long serialVersionUID = 4205890933324878246L;

	@Column
	@Deprecated
	private Calendar createTime;
	@Column
	@Deprecated
	private Calendar updateTime;


	@Column
	private String account;
	
	@Column
	private String password;
	
	@Column
	private String email;
	
	@Column
	private String weiXinOpenId;
	
	@Column
	@Convert(converter = GenderConverter.class)
	private Gender gender = Gender.UNDECIDED;
	
	@Column
	private String nickName;
	
	@Column
	private Double donationAmount;
	
	@Column
	private String motto;
	
	/*身份证号*/
	@Column
	private String idNum;
	
	/*真实姓名*/
	@Column
	private String name;
	
	@Column
	private String telNum;
	
	@Column
	private String pwdSalt;
	
	@Override
	public User clone() {
		try {
			return (User) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("fatal error for clone");
		}
	}
	
	/*======================== Auto-Genrated Code==================================*/



	public Calendar getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}
	public Calendar getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Calendar updateTime) {
		this.updateTime = updateTime;
	}


	public Double getDonationAmount() {
		return donationAmount;
	}
	public String getPwdSalt() {
		return pwdSalt;
	}
	public void setPwdSalt(String pwdSalt) {
		this.pwdSalt = pwdSalt;
	}
	public void setDonationAmount(Double donationAmount) {
		this.donationAmount = donationAmount;
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
	public String getWeiXinOpenId() {
		return weiXinOpenId;
	}
	public void setWeiXinOpenId(String weiXinOpenId) {
		this.weiXinOpenId = weiXinOpenId;
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
	public String getIdNum() {
		return idNum;
	}
	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTelNum() {
		return telNum;
	}
	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}




}
