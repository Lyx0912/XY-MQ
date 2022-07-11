package com.xymq_cli.listener;

import java.util.EventListener;

/**
 * @author 黎勇炫
 * @date 2022年07月11日 15:18
 */
public interface MessageListener extends EventListener {
     /**
       * 获取消息
       */
    public void getMessage(MessageData data);
}