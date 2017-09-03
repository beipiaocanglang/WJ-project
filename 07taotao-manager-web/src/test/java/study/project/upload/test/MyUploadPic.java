package study.project.upload.test;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import study.project.FastDFSClient;

/**
 * 使用fastDFS测试图片上传
 * @author canglang
 */
public class MyUploadPic {

	/**
	 * 使用原始的上传
	 * @throws Exception
	 */
	@Test
	public void uploadPicTest01() throws Exception{
		
		//指定图片路径
		String picPath = "D:\\lang.jpg";
		//指定client配置文件的绝对路径
		String clitntPath = "E:\\JAVA\\ReStudy\\Study\\Project\\taotao\\07taotao-manager-web\\src\\main\\resources\\client.conf";
		//加载客户端的配置文件client.conf，连接fastDFS服务器
		ClientGlobal.init(clitntPath);
		
		//创建TrackerClient客户端
		TrackerClient trackerClient = new TrackerClient();
		//获取trackerServer对象
		TrackerServer trackerServer = trackerClient.getConnection();
		
		StorageServer storageServer = null;
		//创建StorageClient客户端
		StorageClient storageClient = new StorageClient(trackerServer, storageServer );
		//上传图片,参数1：图片路径、参数2：图片后缀、参数3：图片描述
		String[] str = storageClient.upload_file(picPath, "jpg", null);
		
		for (String string : str) {
			/**
			 * group1
			 * M00/00/00/wKj-Q1liKvGAJPlVAAsuME-Y23k010.jpg
			 */
			System.out.println(string);
		}
	}
	/**
	 * 抽取工具类
	 * @throws Exception 
	 */
	@Test
	public void uploadPicTest02() throws Exception{
		//指定图片路径
		String picPath = "D:\\lang.jpg";
		
		FastDFSClient fClient = new FastDFSClient("classpath:client.conf");
		
		String file = fClient.uploadFile(picPath, "jpg");
		//group1/M00/00/00/wKj-Q1liMDCAF9DrAAsuME-Y23k132.jpg
		System.out.println(file);
	}
}
