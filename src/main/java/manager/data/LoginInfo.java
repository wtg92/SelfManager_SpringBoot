package manager.data;

import manager.data.proxy.UserProxy;
import manager.system.SelfXPerms;

public class LoginInfo {
	public String token = "";
	public String errMsg = "";
	public boolean success = false;
	public UserProxy user;

	public String userId;
	public String portraitId;

	public boolean seeUsersModule;
	public boolean seeBooksModule;
	public boolean seeToolsModule;
	public boolean seeWorkSheetModule;

	public LoginInfo() {}

	public LoginInfo(UserProxy user) {
		this.user = user;
		initPerms();
	}
	
	private void initPerms() {
		seeUsersModule = hasPerm(SelfXPerms.SEE_USERS_MODULE);
		seeBooksModule = hasPerm(SelfXPerms.SEE_BOOKS_MODULE);
		seeToolsModule = hasPerm(SelfXPerms.SEE_TOOLS_MODULE);
		seeWorkSheetModule = hasPerm(SelfXPerms.SEE_WORKSHEET_MODULE);
	}
	
	
	public boolean hasPerm(SelfXPerms perm) {
		return user.perms.contains(perm);
	}
}
