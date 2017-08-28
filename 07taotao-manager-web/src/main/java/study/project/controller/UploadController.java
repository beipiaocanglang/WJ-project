package study.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import study.project.FastDFSClient;
import study.project.JsonUtils;
import study.project.PicResult;

@Controller
public class UploadController {

	@Value("${IMAGE_SERVER_PATH}")
	private String IMAGE_SERVER_PATH;
	
	/**
	 * 上传图片到fasfDFS分布式系统上
	 * 请求：
	 * 		common.js：/pic/upload
	 * 参数：
	 * 		common.js：uploadFile
	 * 业务需求：
	 * 		图片不跟随表单一起提交，图片上传完成后直接回显
	 * @param uploadFile
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/pic/upload")
	public String uploadPic(MultipartFile uploadFile){
		
		//获取上传的文件全名称
		String originalFilename = uploadFile.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		
		PicResult picResult = new PicResult();
		String url = "";
		//创建工具类对象
		try {
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:client.conf");
			url = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
			url = IMAGE_SERVER_PATH + url;
			
			//上传成功
			picResult.setError(0);
			picResult.setUrl(url);
			
		} catch (Exception e) {
			picResult.setError(1);
			picResult.setMessage("图片上传失败");
			e.printStackTrace();
		}
		
		return JsonUtils.objectToJson(picResult);
	}
}
