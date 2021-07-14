package manager.system.tool;


import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

public enum Tool {
	UNDECIDED(0,"","","",""),
	IMGS_EXTRACTOR_OF_PPT(1,"PPT图片提取","允许上传单个PPT或Zip包（多份PPT），提取PPT内所有的图片。单次上传文件大小不超过20M。Zip只处理一层嵌套。","zip,pptx","0.1"),
	IMGS_DPI_MODIFER(2,"图片DPI修改","输入DPI","zip,png,jpg","0.1"),
	;
	
	private int dbCode;
	private String name;
	private String desc;
	private String fileTypeMes;
	private String version;
	
	private Tool(int dbCode,String name,String desc,String fileTypeMes,String version) {
		this.dbCode = dbCode;
		this.name = name;
		this.desc = desc;
		this.fileTypeMes = fileTypeMes;
		this.version = version;
	}
	
	public int getDbCode() {
		return dbCode;
	}

	public String getFileTypeMes() {
		return fileTypeMes;
	}

	public void setFileTypeMes(String fileTypeMes) {
		this.fileTypeMes = fileTypeMes;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDbCode(int dbCode) {
		this.dbCode = dbCode;
	}


	public static Tool valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), Tool.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return UNDECIDED;
		}
		
	}
}
