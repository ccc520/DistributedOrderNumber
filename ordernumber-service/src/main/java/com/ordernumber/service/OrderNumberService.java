package com.ordernumber.service;

/**** 
 * @ClassName: OrderService  
 * @Description: 分布式订单号生成器  
 * @author ccc520  
 * @date 2018年6月5日 下午2:27:37  
 * @modificationHistory===============逻辑或功能性重大变更记录
 * @modify by user：  (修改人)
 * @modify by reason：  (修改原因) 
 * 每秒10w+级别要求  及是2000家的话，一家50个业务，每个业务每秒能生成一个单号
 */
public interface OrderNumberService {

	/****
	 * @param companyId 公司Id
	 * @return bizCode 业务编号
	 * @Description: 获取订单号
	 * @author ccc520 2018年6月25日 下午2:35:18
	 * @modificationHistory===============逻辑或功能性重大变更记录
	 * @modify by user：  (修改人)
	 * @modify by reason：  (修改原因)
	 */
	public String getOrderNumber(String enterpriseId, String bizCode);
}
