package study.project.solrcluster.test;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 测试solr集群
 * @author canglang
 */
public class MySolrClusterTest {

	/**
	 * 向solr集群中添加数据
	 */
	@Ignore
	@Test
	public void addSolrclusterDoc(){
		//设置zk集群地址
		String zkhost = "192.168.254.66:2182,192.168.254.66:2183,192.168.254.66:2184";
		//创建solr集群对象
		CloudSolrServer cloudSolrServer = new CloudSolrServer(zkhost);
		//设置默认操作的索引库
		cloudSolrServer.setDefaultCollection("collection2");
		//创建docs文档对象
		SolrInputDocument docs = new SolrInputDocument();
		//设置数据
		docs.addField("id", "12345678");
		docs.addField("item_title", "牙膏");
		
		try {
			//设置doc文档对象
			cloudSolrServer.add(docs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			//提交
			cloudSolrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//测试成功见图5
	
	/**
	 * 查询数据
	 */
	@Ignore
	@Test
	public void querySolrclusterDoc(){
		//设置zk集群地址
		String zkhost = "192.168.254.66:2182,192.168.254.66:2183,192.168.254.66:2184";
		//创建solr集群对象
		CloudSolrServer cloudSolrServer = new CloudSolrServer(zkhost);
		//设置默认操作的索引库
		cloudSolrServer.setDefaultCollection("collection2");	
		
		//设置查询条件
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("id:12345678");
		
		try {
			//查询
			QueryResponse query = cloudSolrServer.query(solrQuery);
			SolrDocumentList results = query.getResults();
			
			//查询总记录数
			System.out.println("*************"+results.getNumFound());//1
			
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}	
}
