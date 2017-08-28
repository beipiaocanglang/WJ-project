<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript">
	function dataImport(){
		$.ajax({
			//导入数据库数据到索引库的请求
			type:"POST",
			url:"${pageContext.request.contextPath}/dataImport",
			success:function(data){
				if(data.status==200){
					alert("成功导入索引库");
				} else {
					alert("导入失败，请再次重试");
				}
			}
		});
	};
</script>

<div style="padding:5px">
    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="dataImport()">导入数据库数据</a>
</div>