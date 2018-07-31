package com.ordernumber.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ordernumber.service.OrderNumberService;

/**** 
 * @ClassName: OrderNumberController  
 * @Description: 分布式订单号生成器  
 * @author ccc520  
 * @date 2018年6月5日 下午2:50:42  
 * @modificationHistory===============逻辑或功能性重大变更记录
 * @modify by user：  (修改人)
 * @modify by reason：  (修改原因) 
 */
@RestController
public class OrderNumberController {

	@Autowired
	private OrderNumberService orderNumberService;
	
	/**  
	 * @Title: getOrderNumber  
	 * @Description: 获取订单号  
	 * @param companyId 公司Id
	 * @return bizCode 业务编号  
	 * @return void    返回类型  
	 * @throws  
	 */
	@GetMapping("orderNumber/{enterpriseId}/{bizCode}")
	public String getOrderNumber(@PathVariable String enterpriseId, @PathVariable String bizCode) {
		return orderNumberService.getOrderNumber(enterpriseId, bizCode);
	}
}
