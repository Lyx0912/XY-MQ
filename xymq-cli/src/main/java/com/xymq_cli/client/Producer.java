package com.xymq_cli.client;

import com.xymq_cli.util.ResourceUtils;

/**
 * @author 黎勇炫
 * @date 2022年07月11日 15:09
 */
public class Producer {
    /**
     * 服务器端地址
     */
    private static String host;
    /**
     * 服务器端口
     */
    private static int port;

    static{
        host = ResourceUtils.getKey("server.host");
        port = Integer.parseInt(ResourceUtils.getKey("server.port"));
    }
}
