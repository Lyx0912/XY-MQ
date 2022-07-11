package com.xymq_cli.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


/**
 * @author 黎勇炫
 * @date 2022年07月11日 14:33
 */
public class ResourceUtils {
    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    /**
     * 属性文件全名
     */
    private static final String PFILE =System.getProperty("user.dir")+File.separator+"conf"+File.separator+"xymq-cli.properties";
    /**
     * 对应于属性文件的文件对象变量
     */
    private static File m_file = null;
    /**
     * 属性文件的最后修改日期
     */
    private static long m_lastModifiedTime = 0;
    private static ResourceBundle rb;

    private static BufferedInputStream inputStream;
    static {
        try {
            m_file = new File(PFILE);
            m_lastModifiedTime = m_file.lastModified();
            inputStream = new BufferedInputStream(new FileInputStream(PFILE));
            rb = new PropertyResourceBundle(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e ) {
            logger.error("配置文件xymq-cli.properties不存在");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("系统启动失败");
            e.printStackTrace();
        }
    }

    public static  String getKey(String key){
        long newTime = m_file.lastModified();
        if(newTime > m_lastModifiedTime){
            // Get rid of the old properties
            try {
                m_lastModifiedTime = m_file.lastModified();
                inputStream = new BufferedInputStream(new FileInputStream(PFILE));
                rb = new PropertyResourceBundle(inputStream);
                inputStream.close();
            } catch (FileNotFoundException e) {
                logger.error("配置文件xymq-cli.properties不存在");
                e.printStackTrace();
            } catch (IOException e) {
                logger.error("系统启动失败");
                e.printStackTrace();
            }
        }
        String result=rb.getString(key);

        return result;
    }

}