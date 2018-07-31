package com.ordernumber.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * Twitter_Snowflake<br> 
 * SnowFlake改进符合业务的结构如下(每部分用-分开):<br> 
 * bizCode(业务代码) + 201805051212(日期) + 4096(毫秒内随机数)<br> 
 */  

/**** 
 * @ClassName: SnowflakeIdWorker  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author ccc520  
 * @date 2018年6月5日 上午8:44:27  
 * @modificationHistory===============逻辑或功能性重大变更记录
 * @modify by user：  (修改人)
 * @modify by reason：  (修改原因) 
 */
public class SnowflakeIdVariant {
	
	// ==============================Fields===========================================  
    /** 序列在id中占的位数 */  
    private final long sequenceBits = 12L;  
    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */  
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);  
    /** 毫秒内序列(0~4095) */  
    private long sequence = 0L;  
    /** 上次生成ID的时间截 */  
    private long lastTimestamp = -1L;  
    
    private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {  
        @Override  
        protected SimpleDateFormat initialValue() {  
            return new SimpleDateFormat("yyyyMMddhhmmssSSS");  
        }  
    };  
    
    //==============================Constructors=====================================  
    /** 
     * 构造函数 
     */  
    public SnowflakeIdVariant() {}  

    // ==============================Methods==========================================  
    /** 
     * 获得下一个ID (该方法是线程安全的) 
     * @return SnowflakeId 
     */  
    public synchronized String nextId(String machineId, String bizCode) {  
        long timestamp = timeGen();  
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常  
        if (timestamp < lastTimestamp) {  
            throw new RuntimeException(  
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));  
        }  
        //如果是同一时间生成的，则进行毫秒内序列  
        if (lastTimestamp == timestamp) {  
            sequence = (sequence + 1) & sequenceMask;  
            //毫秒内序列溢出  
            if (sequence == 0) {  
                //阻塞到下一个毫秒,获得新的时间戳  
                timestamp = tilNextMillis(lastTimestamp);  
            }  
        }  
        //时间戳改变，毫秒内序列重置  
        else {  
            sequence = 0L;  
        }  
        //上次生成ID的时间截  
        lastTimestamp = timestamp;  
        //移位并通过或运算拼到一起组成64位的ID  
        return bizCode + machineId + (threadLocal.get().format(new Date(timestamp))) + sequence;
    }  
    /** 
     * 阻塞到下一个毫秒，直到获得新的时间戳 
     * @param lastTimestamp 上次生成ID的时间截 
     * @return 当前时间戳 
     */  
    protected long tilNextMillis(long lastTimestamp) {  
        long timestamp = timeGen();  
        while (timestamp <= lastTimestamp) {  
            timestamp = timeGen();  
        }  
        return timestamp;  
    }  
    /** 
     * 返回以毫秒为单位的当前时间 
     * @return 当前时间(毫秒) 
     */  
    protected long timeGen() {  
        return System.currentTimeMillis();  
    }  
    //==============================Test=============================================  
    /** 测试 */  
    public static void main(String[] args) throws InterruptedException {  
        SnowflakeIdVariant idWorker = new SnowflakeIdVariant();
        long time1 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {  
            String id = idWorker.nextId("001","ORC");  
            //Thread.sleep(1);  
            System.out.println(id);  
        }
        System.out.println(System.currentTimeMillis()-time1);
    }  
}
