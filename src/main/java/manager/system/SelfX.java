package manager.system;

import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

import static manager.util.CommonUtil.getValFromPropertiesFileInResource;

import java.io.File;
@Resource
public class SelfX {

	@Value("${file.external.root}")
	private String externalDir;


	public final static String VERSION = "3.0";
	
	public final static Long APP_STARTING_TIME = System.currentTimeMillis();
	
	public final static String PROPERTIES_FILE_NAME = "sm.properties";

			
	public final static String DB_NAME = getValFromPropertiesFileInResource("db_name"); 
	public final static String ADMIN_ACCOUNT = getValFromPropertiesFileInResource("admin_account"); 
	
	public final static String FRONT_END_STATIC_FILES_PATH = "/sm_files/";
	public final static String BASIC_FILES_FOLDER = FRONT_END_STATIC_FILES_PATH+"basic/";

	public static File SM_EXTERNAL_FILES_DIRECTORY = new File(getValFromPropertiesFileInResource("sm_external_files_directory"));

	public final static File PDF_FILES_FOLDER = new File(SM_EXTERNAL_FILES_DIRECTORY,"pdf");
	
	public final static String BRAND_NAME = getValFromPropertiesFileInResource("brand_name");
	
	public final static String DEFAULT_BASIC_USER_GROUP = "普通用户";
	
	public final static String WEB_TITLE =BRAND_NAME;
	
	/*系统的Id 用在一些Log上 表明是系统的动作*/
	public final static long SYSTEM_ID = 0;
	public final static String SYSTEM_NAME ="System";
	
	public final static String ARRAY_SPLIT_MARK=";;;";

	public final static int MAX_DB_LINES_IN_ONE_SELECTS = 500;

}
