package manager.cache;
/**
 * 简写 R means relation E means entity T means temporary
 * @author 王天戈
 *  这个枚举 用于 CacheSchedule.invoker CacheSchedule 和 CacheConverter 三者的沟通
 *  不可避免地带来了一些耦合（如E_UNIQUE_FIELD_ID）但似乎可以忍受。
 */
public enum CacheMode {
	/*key 为库名:表名:1:id  */
	R_ONE_TO_MANY_FORMER,
	/*key 为库名:表名:0:id*/
	R_ONE_TO_MANY_LATTER,
	/*key为id*/
	E_ID,
	/*处理一对一的关系表时，从属表隐藏本身的Id，以关联Id表明自身*/
	E_UNIQUE_FIELD_ID,
	
	/*key为 tp:user:uuid*/
	T_USER,
	
	/*key 为 tp:ws:dateStr*/
	T_WS_COUNT_FOR_DATE,
	
	/*key 为 tp:email/tel:val*/
	T_EMAIL_FOR_SIGN_IN,
	T_TEL_FOR_SIGN_IN,
	
	/*key 为 tp:email/tel:resetpwd:val*/
	/*val为 account:email/telval*/
	T_EMAIL_FOR_RESET_PWD,
	T_TEL_FOR_RESET_PWD,

	/**
	 * 缓存是否有权限
	 */
	PERM,

	/**
	 * 代表整个系统就一份的对象缓存
	 * key为 tp:uni:obj
	 */
	T_UNIQUE_OBJ,
	
	

}
