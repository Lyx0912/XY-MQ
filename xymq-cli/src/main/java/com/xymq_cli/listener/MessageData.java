package com.xymq_cli.listener;

import java.util.EventObject;

/**
 * @author 黎勇炫
 * @date 2022年07月11日 15:19
 */
public class MessageData extends EventObject {
    //消息内容
    private String message;


    public MessageData(Object consumer) {
        this(consumer,null);
    }

    public MessageData(Object consumer,String message){
        super(consumer);
        this.message = message;
    }

    /*
     * 返回消息内容
     * */
    public String getMessage(){
        return message;
    }
}
