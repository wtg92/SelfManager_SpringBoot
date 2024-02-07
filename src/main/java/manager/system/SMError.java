package manager.system;

public enum SMError {
	
	
	UNDECIDED(""),
	FAIL_PARSE_JSON_FORMAT("JSON字符串解析失败"),
	INCONSISTANT_DB_ERROR("出现了数据库不一致错误，请刷新页面重试"),
	UNKNOWN_DB_ERROR("数据库未知错误"),
	DATA_TOO_LONG("数据长度超过数据库支持大小，请精简信息，重新存储；或上传BUG，调整数据库支持大小（假如你认为是合理的话）"),
	ILLEGAL_PWD("密码不符合格式要求"),
	UNEXPCTED_ERROR("出乎意料的系统错误"),
	ILLEGAL_INSTRUCTURE_ERROR("实体结构错误"),
	INCONSISTANT_CACHE_ERROR("缓存不一致"),
	INCONSTSTANT_ARGS_BETWEEN_DATA("参数与数据不一致"),
	LACK_PERM("缺少权限"),
	DUP_USER_GROUP_NAME("用户组名已存在"),
	ERROR_CREATE_YZM("验证码生成过程发生意外的错误"),
	NOT_SUPPORTED_YET("抱歉，该功能暂不支持"),
	TEMP_USER_TIMEOUT("停留页面过长，相关数据已失效，请保存好页面信息，刷新页面以重新获取数据"),
	CHECK_YZM_ERROR("图片验证码校验失败"),
	SEND_EMAIL_ERROR("发送邮件出现错误，请在检查邮箱地址没有填写错误后，稍候再试"),
	SIGN_UP_ILLGEAL("注册格式非法"),
	CHECK_VERIFY_CODE_FAIL("验证码校验失败"),
	ACCOUNT_NULL("账号不存在"),
	EMAIL_NULL("email不存在"),
	EMAIL_VERIFY_TIMEOUT("邮箱验证码已失效"),
	EMAIL_VERIFY_WRONG("邮箱验证码错误"),
	TEL_VERIFY_TIMEOUT("手机验证码已失效"),
	TEL_VERIFY_WRONG("手机验证码错误"),
	PWD_WRONG("密码错误"),
	REQUEST_ARG_NULL("请求参数为空"),
	REQUEST_ARG_ILLEGAL("请求参数不合法"),
	UNKOWN_OP("未知的请求"),
	ILLEGAL_USER_TOKEN("登录凭证损坏，请重新登录"),
	YZM_ENCODE_ERROR("验证码图片转码异常"),
	CANNOT_EDIT_OTHERS_PLAN("不能编辑他人的工作表计划"),
	CANNOT_SYNC_OTHERS_PLAN_TAGS("不能同步他人的工作表标签"),
	DB_SYNC_ERROR("操作过于频繁，请刷新页面重试"),
	ILLEGAL_WL_ENTITY_CONTENT("Content 格式非法"),
	FORBID_DUP_CAT_IN_PLAN("同一份计划里不允许有重名的类别"),
	CAREER_ACTION_ERROR("操作失败"),
	CANNOT_SEE_PLAN("无权限看对应计划"),
	CREATE_PLAN_ERROR("创建计划失败"),
	CANNOT_SAVE_PLAN("无权限修改对应计划"),
	ILLEGAL_WORK_SHEET_CONTENT("非法的工作表结构"),
	INCONSISTANT_WS_DATA("不一致的工作表数据"),
	UNEXPCETED_OP_ERROR_FOR_WS("工作表数据操作出现意外错误"),
	OPEN_WORK_SHEET_SYNC_ERROR("(计划时区)今天的工作表已经存在，不能重复开启"),
	OPEN_WORK_BASE_WRONG_STATE_PLAN("只能基于进行中的计划开启工作表"),
	CANNOTE_OPEN_OTHERS_PLAN("无权限以他人的计划开启工作表"),
	CANNOTE_OPREATE_OTHERS_WS("无权限对他人的工作表进行编辑、删除等修改性操作"),
	CANNOT_CANCEL_WS_WHICH_NOT_ASSUMED("不能对正常状态取消假定完成"),
	CANNOTE_SEE_OTHERS_WS("没有权限查看别人的工作表"),
	CANNOT_DELETE_WS_PLAN_ITEM_WITH_WORK_ITEM("不能删除有关联工作项的计划项"),
	WRONG_INITOR("传入了不一致的初始化函数"),
	NO_SYNC_ZERO_WS_PLAN_ITEM("不能同步结算值为0的计划项"),
	SYNC_ITEM_WITH_DEPT_ERROR("同步计划项时发生错误"),
	CONNOT_COPY_OTHERS_PLANITEMS("由于拥有该计划的用户将不允许别人复制，复制失败"),
	ILLEGAL_CODE("非法的ID，请检查是否复制完全"),
	SEND_SMS_ERROR("短信发送异常，估计是买的短信用完了，请暂时使用邮箱吧。"),
	NON_EXISTED_TEL("未注册的手机号"),
	NON_EXISTED_EMAIL("未注册的邮箱"),
	
	CREATE_NOTE_ERROR("创建笔记失败"),
	
	EDIT_NOTEBOOK_ERROR("编辑笔记本失败"),
	EDIT_NOTE_ERROR("编辑笔记失败"),
	
	SAVE_NOTES_SEQ_ERROR("调整笔记顺序失败"),
	CANNOTE_SEE_OTHER_NOTE_BOOK("无权限查看别人的笔记本"),
	CANNOT_SEE_OTHERS_NOTE("无权限查看别人笔记本的笔记"),
	
	MEMO_DOC_ERROR("备忘录doc异常"),
	MEMO_ITEMS_DUP_ERROR("对于来自同一份笔记页或手动添加的备忘录项，不允许有重复内容的标签（TODO和DONE视作同一标签）"),
	UNEXPCETED_OP_ERROR_FOR_MEMO("备忘录操作出现意外错误"),
	EDIT_MEMO_ERRO("修改备忘录错误"),
	
	RETRIEVE_USER_ERROR("找回信息错误"),
	RESET_PWD_ERROR("重置密码错误"),
	
	PPT_EEROR("PPT 操作异常"),
	IMG_ERROR("图片操作异常"),
	
	TOOL_RECORD_DOC_ERROR("Tool Record Content Doc Error"),
	
	EXTRACTE_PPT_FROM_IMGS_ERROR("PPT提取图片失败"),
	
	FIEL_UPLOADING_ERROR("文件上传失败"),
	
	MODIFY_DPI_ERROR("修改图片DPI失败"),
	ILLEGAL_TAG("标签中包含非法字符"),
	DUP_TAG("标签重复")
	;
	private final String description;
    
	private SMError(String description) {
		this.description = description;
	}


	public String getDescription() {
		return description;
	}
	
	
}
