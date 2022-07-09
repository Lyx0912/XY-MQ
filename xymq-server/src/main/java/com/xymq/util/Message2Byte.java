package com.xymq.util;

import com.alibaba.fastjson.JSON;
import com.xymq.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * @author 黎勇炫
 * @date 2022年07月09日 17:55
 */
public class Message2Byte {

    /**
     * 将消息内容转换成字节数组
     * @param message 消息对象
     * @return byte[]
     * @author 黎勇炫
     * @create 2022/7/9
     * @email 1677685900@qq.com
     */
    public static byte[] change(Message message){
        return JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 将字节数组转换成message对象
     * @param bytes
     * @return com.xymq.message.Message
     * @author 黎勇炫
     * @create 2022/7/9
     * @email 1677685900@qq.com
     */
    public static Message reverse(byte[] bytes){
        return JSON.parseObject(new String(bytes,StandardCharsets.UTF_8),Message.class);
    }
}
