package study.project;

public class PicResult {

	//图片上传的状态error，0：成功、1：失败
	private int error;
	
	//上传成功后的url地址，用于保存到数据库和图片回显
	private String url;
	
	//失败时的错误信息
	private String message;

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "PicResult [error=" + error + ", url=" + url + ", message="
				+ message + "]";
	}
}
