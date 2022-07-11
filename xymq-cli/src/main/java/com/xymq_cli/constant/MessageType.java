package com.xymq_cli.constant;

/**
 * @author 黎勇炫
 * @date 2022年07月11日 15:57
 */
public enum MessageType {


    PRIVODER(0,"provider"),    //生产者
    COMSUMER(1,"comsumer"),    //消费者
    ACK(2,"ack");              //签收


    Integer type;
    String describe;

    MessageType(Integer type, String describe){
        this.type = type;
        this.describe = describe;
    }

    public Integer getType(){
        return type;
    }

    public String getDescribe(){
        return describe;
    }

}