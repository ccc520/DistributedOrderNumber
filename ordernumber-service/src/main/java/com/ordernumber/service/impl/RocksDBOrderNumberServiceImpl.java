package com.ordernumber.service.impl;

import java.io.File;
import java.time.LocalDate;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ordernumber.service.OrderNumberService;

/**** 
 * @ClassName: RocksDBOrderNumberServiceImpl  
 * @Description: TODO(单号生成类)  
 * @author ccc520  
 * @date 2018年6月5日 下午2:36:16  
 * @modificationHistory===============逻辑或功能性重大变更记录
 * @modify by user：  (修改人)
 * @modify by reason：  (修改原因) 
 * 
 * 同一个公司不能重复，不同的公司可以重复
 * 重启后单号还能尽可能的保持连续
 * 尽量不造成单号的浪费
 * RockesDB的K/V存取保证存取+性能
 * 
 * 单据规则: 业务代码(bizCode)+机器号(001)+日期(20180605)+6位序号(000001)
 * 假设一个服务服务1000个商家，每个商家50个业务编码，要求维持50000个变量
 * 10w+订单号/s
 */
@Component("orderNumberService")
public class RocksDBOrderNumberServiceImpl implements OrderNumberService, InitializingBean{
	
	private static Logger logger = LoggerFactory.getLogger(RocksDBOrderNumberServiceImpl.class);
	
	@Value("${orderNumber.rocks.machineId}")
	private String machineId;
	
	@Value("${orderNumber.rocks.digit}")
	private Integer digit;
	
	@Value("${orderNumber.rocks.dbPath}")
	private String dbPath;
	
	private volatile RocksDB rocksDB;

	//跨天锁定并重新赋值
	LocalDate today;
	
	volatile String todayString;
	
	//格式化数字的数组
	int[] sizeTable;
	
	SnowflakeIdVariant snowflakeIdWorker = new SnowflakeIdVariant();
	
	@Override
	public synchronized String getOrderNumber(String companyId, String bizCode) {
		String key = (companyId+"--${bound}--"+bizCode); //.intern();
		//synchronized(key) {
			try {
				byte[] value = rocksDB.get(key.getBytes());
				if(value != null) {
					int seqNum = value[3] & 0xFF | (value[2] & 0xFF) << 8 | (value[1] & 0xFF) << 16 | (value[0] & 0xFF) << 24;
					seqNum += 1;
					rocksDB.put(key.getBytes(), new byte[]{(byte)((seqNum >>> 24) & 0xff),(byte)((seqNum >>> 16)& 0xff ),  
			                         (byte)((seqNum >>> 8) & 0xff ), (byte)((seqNum >>> 0) & 0xff )});
					return bizCode+machineId+todayString+getFormatString(seqNum);
				}else {
					rocksDB.put(key.getBytes(), new byte[]{(byte)((1 >>> 24) & 0xff),(byte)((1 >>> 16)& 0xff ),  
	                         (byte)((1 >>> 8) & 0xff ), (byte)((1 >>> 0) & 0xff )});
					return bizCode+machineId+todayString+getFormatString(1);
				}
			} catch (Throwable e) {
				//返回UUID
				return snowflakeIdWorker.nextId(machineId, bizCode);
			}
		//}
	}
	
	private final String getFormatString(int value) {
		StringBuilder sb = new StringBuilder();
		for(int i = sizeTable.length-1 ; i >= 0 ; i--) {
			if(sizeTable[i] > value)
				sb.append("0");
			else
				break;
		}
		return sb.append(value).toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//load rocksdb
	    try {
	    	RocksDB.loadLibrary();  
	    	
	    	//[1,10,100,1000,10000...]
	    	sizeTable = new int[digit];
			for(int i = 0 ; i < digit ; i++) {
				sizeTable[i] = (int)(1*Math.pow(10,i));
			}
	    	
			//创建明日，删除今日
	    	dbFileCheck();
	    } catch (Throwable e) {
	    	logger.warn(" init RocksDb error! ",e);
		}
	}

	@Scheduled(cron="0 0 0 * * ?")
	protected void dbFileCheck() throws Exception{
		
		today = LocalDate.now();
		
		Options options = new Options();  
	    options.setCreateIfMissing(true);
	    
	    File file = new File(dbPath);
	    if(!file.exists() && !file.isDirectory())
	    	file.mkdirs();
		
	    //创建今日的文件夹
	    todayString = ""+today.getYear()
	    		+(today.getMonthValue()<10?("0"+today.getMonthValue()):today.getMonthValue())
	    		+(today.getDayOfMonth()<10?("0"+today.getDayOfMonth()):today.getDayOfMonth());
	    
	    RocksDB todayDB = RocksDB.open(options, dbPath+File.separator+todayString);
		
		if(rocksDB != null) {
			RocksDB tempDB = rocksDB;
			rocksDB = todayDB;
			tempDB.close();
		}else {
			rocksDB = todayDB;
		}
		
		//删除昨日的文件夹, 改为删除所有以前的文件夹 
		File[] files = file.listFiles();
		for(File fileUnit : files) {
			if(!fileUnit.getName().equals(todayString)) {
				FileUtils.deleteDirectory(fileUnit);
			}
		}
		
		/*if(yesterday != null) {
			FileUtils.deleteDirectory(new File(dbPath+File.separator+yesterday.getYear()
			+(yesterday.getMonthValue()<10?("0"+yesterday.getMonthValue()):yesterday.getMonthValue())
			+(yesterday.getDayOfMonth()<10?("0"+yesterday.getDayOfMonth()):yesterday.getDayOfMonth())));
		}*/
		
	}
	
}
