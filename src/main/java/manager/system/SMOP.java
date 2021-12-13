package manager.system;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum SMOP {
	UNDECIDED(""),
	U_SIGN_IN("u_sign_in"),
	U_SIGN_UP("u_sign_up"),
	U_CONFIRM_USER_TOKEN("u_confirm_user_token"),
	U_CONFIRM_TEMP_USER("u_confirm_temp_user"),
	U_GET_TEMP_USER("u_get_temp_user"),
	U_GET_YAM("u_get_yzm"),
	U_CHECK_YZM("u_check_yzm"),
	U_SEND_VERIFY_CODE("u_send_verify_code"),
	U_EXISTS_USER_WITH_FIELD("u_exists_user_with_field"),
	U_LOAD_USER_SUMMARY("u_load_user_summary"),
	U_LOAD_ALL_USER_GROUPS("u_load_all_user_groups"),
	U_LOAD_USERS_OF_GROUP("u_load_users_of_group"),
	U_LOAD_PERMS_OF_GROUP("u_load_perms_of_group"),
	U_OVERRIDE_GROUP_PERMS("u_override_group_perms"),
	U_SEND_EMAIL_VERIFY_CODE_FOR_SIGN_IN("u_send_email_verify_code_for_sign_in"),
	U_SEND_TEL_VERIFY_CODE_FOR_SIGN_IN("u_send_tel_veirfy_code_for_sign_in"),
	U_RETRIEVE_ACCOUNT("u_retrieve_account"),
	U_SEND_VERIFY_CODE_FOR_RESET_PWD("u_send_verify_code_for_reset_pwd"),
	U_RESET_PWD("u_reset_pwd"),
	
	C_LOAD_ACTIVE_PLANS("c_load_active_plans"),
	C_CREATE_PLAN("c_create_plan"),
	C_ADD_ITEM_TO_PLAN("c_add_item_to_plan"),
	C_ADD_ITEM_TO_WS_PLAN("c_add_item_to_ws_plan"),
	C_ADD_ITEM_TO_WS("c_add_item_to_ws"),
	C_REMOVE_ITEM_FROM_PLAN("c_remove_item_plan"),
	C_REMOVE_ITEM_FROM_WS_PLAN("c_remove_item_from_ws_plan"),
	C_REMOVE_ITEM_FROM_WORK_SHEET("c_remove_item_from_work_sheet"),
	C_LOAD_PLAN("c_load_plan"),
	C_SAVE_PLAN("c_save_plan"),
	C_SAVE_PLAN_ITEM("c_save_plan_item"),
	C_SAVE_PLAN_ITEM_FOLD("c_save_plan_item_fold"),
	C_SAVE_WS_PLAN_ITEM("c_save_ws_plan_item"),
	C_SAVE_WS_PLAN_ITEM_FOLD("c_save_ws_plan_item_fold"),
	C_ABANDON_PLAN("c_abandon_plan"),
	C_FINISH_PLAN("c_finish_plan"),
	C_LOAD_PLAN_STATE_STATISTICS("c_load_plan_state_statistics"),
	C_LOAD_WS_STATE_STATISITCS("c_load_ws_state_statistics"),
	C_LOAD_PLANS_BY_STATE("c_load_plans_by_state"),
	C_LOAD_WS_BY_STATE("c_load_ws_by_state"),
	C_OPEN_WORK_SHEET_TODAY("c_open_work_sheet_today"),
	C_SAVE_WORK_SHEET("c_save_work_sheet"),
	C_DELETE_WORK_SHEET("c_delete_work_sheet"),
	C_ASSUME_WORK_SHEET_FINISHED("c_assume_work_sheet_finished"),
	C_CANCEL_ASSUME_WORK_SHEET_FINISHED("c_cancel_assume_work_sheet_finished"),
	C_LOAD_WORK_SHEET_INFOS_RECENTLY("c_load_work_sheet_infos_recently"),
	C_LOAD_WORK_SHEET("c_load_work_sheet"),
	C_LOAD_WORK_SHEET_COUNT("c_load_work_sheet_count"),
	C_SAVE_WORK_ITEM("c_save_work_item"),
	C_SYNC_TO_PLAN_DEPT("c_sync_to_plan_dept"),
	C_SYNC_ALL_TO_PLAN_DEPT("c_sync_all_to_plan_dept"),
	C_SYNC_ALL_TO_PLAN_DEPT_BATCH("c_sync_all_to_plan_dept_batch"),
	C_LOAD_PLAN_DEPT("c_load_plan_dept"),
	C_LOAD_PLAN_DEPT_ITEM_NAMES("c_load_plan_dept_item_names"),
	C_SAVE_DEPT_ITEM("c_save_dept_item"),
	C_COPY_PLAN_ITEMS_BY_ID("c_copy_plan_items_by_id"),
	C_CREATE_NOTE_BOOK("c_create_note_book"),
	C_CREATE_NOTE("c_create_note"),
	C_SAVE_NOTE_BOOK("c_save_note_book"),
	C_SAVE_NOTE("c_save_note"),
	C_SAVE_NOTES_SEQ("c_save_notes_seq"),
	C_LOAD_BOOKS("c_load_books"),
	C_LOAD_BOOK("c_load_book"),
	C_LOAD_BOOK_CONTENT("c_load_book_content"),
	C_LOAD_NOTE("c_load_note"),
	C_DELETE_NOTE_BOOK("c_delete_note_book"),
	C_DELETE_NOTE("c_delete_note"),
	C_CLOSE_NOTE_BOOK("c_close_note_book"),
	C_OPEN_NOTE_BOOK("c_open_note_book"),
	
	C_LOAD_NOTE_LABELS("c_load_note_labels"),
	
	C_SAVE_NOTE_IMPORTANT("c_save_important"),
	
	C_SAVE_MEMO("c_save_memo"),
	C_ADD_ITEM_TO_MEMO("c_add_item_to_memo"),
	C_SAVE_MEMO_ITEM_LABEL("c_save_memo_label"),
	C_SAVE_MEMO_ITEM("c_save_memo_item"),
	C_SAVE_MEMO_ITEMS_SEQ("c_save_memo_items_seq"),
	C_REMOVE_ITEM_FROM_MEMO("c_remove_item_from_memo"),
	C_LOAD_MEMO("c_load_memo"),
	
	C_LOAD_WORK_SHEETS_BY_DATE_SCOPE("c_load_work_sheets_by_date_scope"),
	
	T_LOAD_TOOL_RECORD_SUMMARY("t_load_tool_record_summary"),
	T_LOAD_TOOL_RECORD("t_load_tool_record"),
	
	T_EXTRACT_PPT_IMGAS("t_extract_ppt_imgs"),
	T_MODIFY_IMGS_DPI("t_modify_imgs_dpi"),
	
	
	PDF_GENERAL("pdf_general"),
	
	;

	private final String name;

	private SMOP(String name) {
		this.name = name;
	}

	private final static Logger logger = Logger.getLogger(SMOP.class.getName());
	
	public static SMOP valueOfName(String name) {
		List<SMOP> rlt = Arrays.stream(SMOP.values()).filter(e -> e.name.equals(name)).collect(toList());

		if (rlt.size() > 1)
			throw new RuntimeException("dup op" + name);

		if (rlt.size() == 0) {
			logger.log(Level.SEVERE,"unkown op" + name);
			assert false : name;
			return UNDECIDED;
		}

		return rlt.get(0);
	}

	public String getName() {
		return name;
	}

}
