package study.project;

import java.io.Serializable;

/**
 * EasyUI树形菜单节点包装类
 * @author canglang
 */
public class EasyUITreeNode implements Serializable{

	private Integer id;
	//节点文本来显示 
	private String text;
	//节点状态,“open”或“closed”,默认是“open”。当设置为“closed”,节点有子节点,并将负载从远程站点
	private String state;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "EasyUITreeNode [id=" + id + ", text=" + text + ", state="
				+ state + "]";
	}
}
