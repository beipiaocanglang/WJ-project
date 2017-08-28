package study.project;

import java.io.Serializable;
import java.util.List;

/**
 * EasyUI框架分页插件分页查询商品列表的包装类，跨服务器需要实现序列化
 * @author canglang
 */
public class EasyUIResult implements Serializable{

	private Long total;//总记录数
	
	private List<?> rows;//商品集合

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "EasyUIResult [total=" + total + ", rows=" + rows + "]";
	}
}
