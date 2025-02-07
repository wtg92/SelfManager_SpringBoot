package manager.system;

public abstract class DBConstants {
	public static final String T_USER = "user";
	public static final String T_USER_GROUP = "user_group";
	public static final String T_R_USER_GROUP = "r_user_group";
	public static final String T_R_GROUP_PERM = "r_group_perm";
	public static final String T_PLAN = "plan";
	public static final String T_PLAN_DEPT = "plan_dept";
	public static final String T_WORK_SHEET = "work_sheet";
	public static final String T_MEMO = "memo";
	public static final String T_NOTE_BOOK = "note_book";

	public static final String T_SYSTEM_MAPPING = "system_mapping";
	public static final String T_NOTE = "note";
	public static final String T_TOOL_RECORD = "tool_record";


	public static final String E_SHARING_BOOK = "SHARING_BOOK";

	public static final String[] ALL_TABLES = new String[] {
			T_USER,
			T_USER_GROUP,
			T_R_GROUP_PERM,
			T_R_USER_GROUP,
			T_PLAN,
			T_WORK_SHEET,
			T_PLAN_DEPT,
			T_MEMO,
			T_NOTE_BOOK,
			T_NOTE,
			T_TOOL_RECORD
	};
	
	
	public static final String F_ID= "id";
	public static final String F_NAME= "name";
	public static final String F_CONTENT= "content";
	public static final String F_ACCOUNT = "account";
	public static final String F_WEI_XIN_OPEN_ID = "wei_xin_open_id";
	public static final String F_EMAIL = "email";
	public static final String F_TEL_NUM = "tel_num";
	public static final String F_NICK_NAME = "nick_name";
	public static final String F_USER_ID = "user_id";

	public static final String F_START_DATE = "start_date";
	public static final String F_END_DATE = "end_date";


	public static final String F_START_UTC = "start_utc";
	public static final String F_END_UTC = "end_utc";

	public static final String F_CREATE_UTC = "create_utc";

	public static final String F_USER_GROUP_ID ="user_group_id";
	public static final String F_PERM_ID = "perm_id";
	public static final String F_ID_NUM = "id_num";
	public static final String F_STATE = "state";
	public static final String F_TAGS = "tags";
	public static final String F_OWNER_ID = "owner_id";
	public static final String F_DATE = "date";
	public static final String F_DATE_UTC = "date_utc";

	public static final String F_DATA_VERSION = "data_version";

	public static final String F_TIMEZONE = "timezone";

	public static final String F_PLAN_ID = "plan_id";
	public static final String F_NOTE_BOOK_ID = "note_book_id";
	public static final String F_WITH_TODOS = "with_todos";
	public static final String F_UPDATE_TIME = "update_time";
	public static final String F_UPDATE_UTC = "update_utc";
	public static final String F_IMPORTANT = "important";
	public static final String F_HIDDEN = "hidden";
	public static final String F_TOOL = "tool";

}
