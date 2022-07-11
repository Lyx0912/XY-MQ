package com.xymq_cli.core;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xymq_cli.exception.ExceptionEnum;
import com.xymq_cli.exception.XyException;
import com.xymq_cli.message.Message;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * leveldb操作类
 * @return 
 * @author 黎勇炫
 * @create 2022/7/10
 * @email 1677685900@qq.com       
 */
@Component
@ConfigurationProperties(prefix = "leveldb")
public class LevelDb {

    private DB db = null;

    private Logger logger = LoggerFactory.getLogger(LevelDb.class);

    private String folder;

    private String charset;

    /**
     * 初始化levelDb
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void initLevelDb() {
        DBFactory dbFactory = new Iq80DBFactory();
        Options options = new Options();
        //如果不存在则创建
        options.createIfMissing(true);
        try {
            db = dbFactory.open(new File(folder), options);
            System.out.println("初始化db....");
        } catch (IOException e) {
            logger.error("levelDb初始化失败");
            throw new XyException(ExceptionEnum.LEVELDB_INIT_ERROR);
        }
    }


    /**
     * 消息对象存入levelDB数据库
     * @param key
     * @param message
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void putMessage(Long key, Message message) {
        byte[] messageByte = JSON.toJSONBytes(message, new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect});
        try {
            db.put(String.valueOf(key).getBytes(charset), messageByte);
        } catch (UnsupportedEncodingException e) {
            logger.error("消息编号{}持久化失败",message.getMessageId());
            throw new XyException(ExceptionEnum.FAILED_TO_STORAGE);
        }
    }

    /**
     * 将未消费的消息id和客户端id存入数据库
     * @param key
     * @param map
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void putUnConsumers(String key, HashMap<Long, HashSet<Long>> map) {
        try {
            byte[] keys = key.getBytes(charset);
            byte[] value = JSON.toJSONBytes(map, new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect});
            db.put(keys, value);
        } catch (UnsupportedEncodingException e) {
            logger.error("未消费消息id或客户端id持久化失败");
            throw new XyException(ExceptionEnum.FAILED_TO_STORAGE);
        }
    }

    /**
     * 获取消息对象
     * @param key
     * @return com.xymq.message.Message
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public Message getMessageBean(String key) {
        try {
            byte[] keyByte = key.getBytes(charset);
            byte[] bytes = db.get(keyByte);
            if (null != bytes) {
                Message messageBean = JSON.parseObject(new String(bytes), Message.class);
                return messageBean;
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            logger.error("消息时恢复发生异常");
            throw new XyException(ExceptionEnum.FAILED_TO_RECOVERY_DATA);
        }

    }

    /**
     * 从数据库中删除消息
     * @param key
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void deleteMessageBean(Long key) {
        try {
            byte[] keyByte = String.valueOf(key).getBytes(charset);
            db.delete(keyByte);
        } catch (UnsupportedEncodingException e) {
            logger.error("消息清理失败");
            throw new XyException(ExceptionEnum.FAILED_TO_CLEAN_DATA);
        }
    }

    /**
     * 获取所有key
     * @return java.util.List<java.lang.String>
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public List<String> getKeys() {
        DBIterator iterator = null;
        List<String> list = new ArrayList<>();
        try {
            iterator = db.iterator();
            while (iterator.hasNext()) {
                //如果有数据就初始化数组
                Map.Entry<byte[], byte[]> entry = iterator.next();
                if (new String(entry.getValue()).equals("")) {
                    db.delete(entry.getKey());
                }
                String key = new String(entry.getKey());
                list.add(key);
            }
        } catch (Exception e) {
            logger.error("获取keys失败");
            throw new XyException(ExceptionEnum.FAILED_TO_GET_KEYS);
        } finally {
            if (iterator != null) {
                try {
                    iterator.close();
                } catch (Exception e) {
                    logger.error("levelDb关闭失败");
                    throw new XyException(ExceptionEnum.FAILED_TO_CLOSE_DB);
                }
            }
        }

        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    long id1 = Long.parseLong(o1);
                    long id2 = Long.parseLong(o2);
                    if (id1 == id2) {
                        return 0;
                    }
                    if (id1 > id2) {
                        return 1;
                    } else {
                        return -1;
                    }
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        });
        return list;
    }

    /**
     * 往数据库中存储离线的订阅者数据
     * @param offLineSubscriber 离线订阅者
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void storeOffLineSubscriber(ConcurrentHashMap<String, HashMap<Long, SocketChannel>> offLineSubscriber) {
        if(null != db){
            try {
                byte[] key = "offLineSubscriber".getBytes(charset);
                byte[] value = JSON.toJSONBytes(offLineSubscriber, new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect});
                db.put(key, value);
            } catch (UnsupportedEncodingException e) {
                logger.error("离线订阅者持久化异常");
                throw new XyException(ExceptionEnum.FAILED_TO_STORAGE);
            }


        }
    }

    /**
     * 往数据库中存储离线的消息数据
     * @param storeOffLineTopicMessage 离线消息
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void storeOffLineMessage(ConcurrentHashMap<Long, ArrayList<String>> storeOffLineTopicMessage) {
        if (null != db) {
            try {
                byte[] key = "offLineTopicMessage".getBytes(charset);
                byte[] value = JSON.toJSONBytes(storeOffLineTopicMessage, new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect});
                db.put(key, value);
            } catch (UnsupportedEncodingException e) {
                logger.error("离线消息持久化异常");
                throw new XyException(ExceptionEnum.FAILED_TO_STORAGE);
            }
        }
    }

    /**
     * 从数据库中读取离线的订阅者信息
     * @return SocketChannel 离线的订阅者信息
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public ConcurrentHashMap<String, HashMap<Long, SocketChannel>> getOffLineSubscriber() {
        ConcurrentHashMap<String, JSONObject> hashMap = null;
        ConcurrentHashMap<String, HashMap<Long, SocketChannel>> realHashMap = null;
        try {
            byte[] key = "offLineSubscriber".getBytes(charset);
            byte[] value = db.get(key);
            hashMap = JSON.parseObject(new String(value), ConcurrentHashMap.class);
            if (null != hashMap) {
                realHashMap = new ConcurrentHashMap<String, HashMap<Long, SocketChannel>>();
                for (Map.Entry<String, JSONObject> entry : hashMap.entrySet()) {
                    String str = entry.getValue().toString();
                    HashMap<Long, JSONObject> subscribers = JSON.parseObject(str, HashMap.class);
                    HashMap<Long, SocketChannel> newSubscribers = new HashMap<Long, SocketChannel>();
                    for (Map.Entry<Long, JSONObject> longJSONObjectEntry : subscribers.entrySet()) {
                        String longString = String.valueOf(longJSONObjectEntry.getKey());
                        String socketString = longJSONObjectEntry.getValue().toString();
                        Long clientId = Long.valueOf(longString);
                        SocketChannel socketChannel = JSON.parseObject(socketString, SocketChannel.class);
                        newSubscribers.put(clientId, socketChannel);
                    }
                    realHashMap.put(entry.getKey(), newSubscribers);
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("离线订阅者信息读取失败");
            throw new XyException(ExceptionEnum.FAILED_TO_RECOVERY_DATA);
        }
        return realHashMap;
    }

    /**
     * 从数据库中读取离线的消息数据
     * @return 离线的消息数据
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public ConcurrentHashMap<Long, ArrayList<String>> getOffLineMessage() {
        ConcurrentHashMap<Integer, JSONArray> hashMap = null;
        ConcurrentHashMap<Long, ArrayList<String>> realData = null;
        try {
            byte[] key = "offLineTopicMessage".getBytes(charset);
            byte[] value = db.get(key);
            hashMap = JSON.parseObject(new String(value), ConcurrentHashMap.class);
            if (null != hashMap) {
                realData = new ConcurrentHashMap<Long, ArrayList<String>>();
                for (Map.Entry<Integer, JSONArray> hashMapEntry : hashMap.entrySet()) {
                    Long clientId = Long.valueOf(hashMapEntry.getKey());
                    JSONArray jsonObject = hashMapEntry.getValue();
                    ArrayList<String> arrayList = JSON.parseObject(jsonObject.toString(), ArrayList.class);
                    realData.put(clientId, arrayList);
                }
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("离线消息读取失败");
            throw new XyException(ExceptionEnum.FAILED_TO_RECOVERY_DATA);
        }
        return realData;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}