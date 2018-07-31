package com.ordernumber.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.ordernumber.service.OrderNumberService;

/**** 
 * @ClassName: WithRandomOrderNumberServiceImpl  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author ccc520  
 * @date 2018年6月6日 上午8:12:58  
 * @modificationHistory===============逻辑或功能性重大变更记录
 * @modify by user：  (修改人)
 * @modify by reason：  (修改原因)
 * 
 *  单据规则 “业务编码 + 时间戳 + 机器编号[前4位] + 随机4位数 + 毫秒数”
 */
public class WithRandomOrderNumberServiceImpl implements OrderNumberService {
	
	@Value("${server.machineId}")
	private String machineId;
	
	//业务编码（OrderType: Web=1 CallCenter=2 Wap=3） 机器编号(用来表示由那台服务器生成的订单)
	@Override
	public String getOrderNumber(String enterpriseId, String bizCode) {
	    Date curDate = new Date();
        String dateStr4yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss").format(curDate);
        String dateStr4SSS = new SimpleDateFormat("SSS").format(curDate);
        String random = RandomStringUtils.random(4,false,true);
        String machineCode = machineId;
        return bizCode+dateStr4yyyyMMddHHmmss+machineCode+random+dateStr4SSS;
	}

	public static void main(String args[]) {
		long time1 = System.currentTimeMillis();
		for(int i = 0; i < 100000; i++) {
			Date curDate = new Date();
			String dateStr4yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss").format(curDate);
			String dateStr4SSS = new SimpleDateFormat("SSS").format(curDate);
			String random = RandomStringUtils.random(4,false,true);
			String machineCode = "001";
			//System.out.println("COR"+dateStr4yyyyMMddHHmmss+machineCode+random+dateStr4SSS);
			//System.out.println("COR"+machineCode+dateStr4yyyyMMddHHmmss+dateStr4SSS+random);
		}
		System.out.println(System.currentTimeMillis()-time1);
	}
}
