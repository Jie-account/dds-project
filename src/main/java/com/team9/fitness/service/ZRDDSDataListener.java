package com.team9.fitness.service;

import com.zrdds.infrastructure.*;
import com.zrdds.subscription.DataReader;
import com.zrdds.subscription.SimpleDataReaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZRDDS数据监听器
 * 基于ZRDDS SimpleDataReaderListener实现真实的数据接收
 */
public class ZRDDSDataListener extends SimpleDataReaderListener<Bytes, BytesSeq, BytesDataReader> {
    
    private static final Logger log = LoggerFactory.getLogger(ZRDDSDataListener.class);
    
    private final ZRDDSService.DataListener dataListener;
    private final String topicName;
    
    public ZRDDSDataListener(String topicName, ZRDDSService.DataListener dataListener) {
        this.topicName = topicName;
        this.dataListener = dataListener;
    }
    
    @Override
    public void on_process_sample(DataReader dataReader, Bytes bytes, SampleInfo sampleInfo) {
        try {
            if (bytes != null && bytes.value != null) {
                // 将接收到的字节数据转换为字符串
                String receivedContent = new String(bytes.value.get_contiguous_buffer(), 0, bytes.value.length());
                log.debug("接收到数据: topic={}, content={}", topicName, receivedContent);
                
                // 调用数据监听器处理接收到的数据
                if (dataListener != null) {
                    dataListener.onDataReceived(topicName, receivedContent);
                }
            }
        } catch (Exception e) {
            log.error("处理接收到的数据失败: topic={}", topicName, e);
        }
    }
    
    @Override
    public void on_data_arrived(DataReader dataReader, Object data, SampleInfo sampleInfo) {
        // 这个方法在SimpleDataReaderListener中可能不会被调用
        // 主要的数据处理在on_process_sample中完成
        log.debug("数据到达事件: topic={}", topicName);
    }
    
    /**
     * 获取主题名称
     */
    public String getTopicName() {
        return topicName;
    }
}
