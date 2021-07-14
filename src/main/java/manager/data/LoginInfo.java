package manager.data;

import manager.data.proxy.UserProxy;
import manager.system.SMPerm;

public class LoginInfo {
	public String token = "";
	public String errMsg = "";
	public boolean success = false;
	public UserProxy user;
	
	public boolean seeUsersModule;
	public boolean seeNotesModule;
	
	
	public LoginInfo(UserProxy user) {
		this.user = user;
		initPerms();
	}
	
	private void initPerms() {
		seeUsersModule = hasPerm(SMPerm.SEE_USERS_MODULE);
		seeNotesModule = hasPerm(SMPerm.SEE_NOTES_MODULE);
	}
	
	
	public boolean hasPerm(SMPerm perm) {
		return user.perms.contains(perm);
	}
}
