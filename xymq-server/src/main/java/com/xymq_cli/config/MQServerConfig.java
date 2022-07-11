package com.xymq_cli.config;

import com.xymq_cli.core.LevelDbStorageHelper;
import com.xymq_cli.core.StorageHelper;
import com.xymq_cli.util.SnowflakeIdUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列服务端配置类
 * @author 黎勇炫
 * @date 2022年07月10日 14:21
 */
@Configuration
@ConfigurationProperties(prefix = "xymq.storage")
public class MQServerConfig {

    private String type;

    /**
     * id生成
     * @return com.xymq.util.SnowflakeIdUtils
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    @Bean
    public SnowflakeIdUtils snowflakeIdUtils(){
        return new SnowflakeIdUtils(9,12);
    }

    @Bean
    public StorageHelper storageHelper(){
        if(type.equals("levelDb")){
            return new LevelDbStorageHelper();
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
