package manager.system;

import static manager.util.CommonUtil.getValFromPropertiesFileInResource;

import java.io.File;
import java.util.Calendar;

public abstract class SM {

	public final static String VERSION = "3.0";
	
	public final static Calendar APP_STARTING_TIME = Calendar.getInstance();
	
	public final static String PROPERTIES_FILE_NAME = "sm.properties";

			
	public final static String DB_NAME = getValFromPropertiesFileInResource("db_name"); 
	public final static String ADMIN_ACCOUNT = getValFromPropertiesFileInResource("admin_account"); 
	
	public final static String FRONT_END_STATIC_FILES_PATH = "/sm_files/";
	public final static String BASIC_FILES_FOLDER = FRONT_END_STATIC_FILES_PATH+"basic/";

	public final static File SM_EXTERNAL_FILES_DIRECTORY = new File(getValFromPropertiesFileInResource("sm_external_files_directory"));
	public final static File PDF_FILES_FOLDER = new File(SM_EXTERNAL_FILES_DIRECTORY,"pdf");
	
	public final static String BRAND_NAME = getValFromPropertiesFileInResource("brand_name");
	
	public final static String DEFAULT_BASIC_USER_GROUP = "普通用户";
	
	public final static String WEB_TITLE =BRAND_NAME+"，成就非凡";
	
	/*系统的Id 用在一些Log上 表明是系统的动作*/
	public final static long SYSTEM_ID = 0;
	public final static String SYSTEM_NAME ="系统";
	
	public final static String ARRAY_SPLIT_MARK=";;;";
	
}
