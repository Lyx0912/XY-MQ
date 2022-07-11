package com.xymq_cli.message;

/**
 * @author 黎勇炫
 * @date 2022年07月09日 17:11
 */
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 封装消息的实体类
 * @author 黎勇炫
 * @date 2022年07月09日 17:11
 */
public class Message implements Delayed {
     /**
       * 消息唯一ID，避免防止消息重复
       */
    private Long messageId;
     /**
       * 消息类型(0生产者，1消费者，2签收)
       */
    private int type;
     /**
       * 消息内容
       */
    private String content;
     /**
       * 消息的目的地
       */
    private String destination;
     /**
       * 消息的目的地(队列或频道)
       */
    private int destinationType;
     /**
       * 是否插队消息
       */
    private boolean isTopPriority = false;
     /**
       * 到期时间
       */
    private  long expire;
     /**
       * 时间单位
       */
    private TimeUnit timeUnit;
     /**
       * 延迟时间
       */
    private long delay;

    public Message(Long messageId, int type, String content, String destination, int destinationType, boolean isTopPriority, long delay, TimeUnit timeUnit) {
        this.messageId = messageId;
        this.type = type;
        this.content = content;
        this.destination = destination;
        this.destinationType = destinationType;
        this.isTopPriority = isTopPriority;
        this.timeUnit=timeUnit;
        this.delay=delay;
        if(0 != delay){
            delay = timeUnit.toMillis(delay);
            expire = System.currentTimeMillis()+delay;
        }
    }

    public boolean getIsTopPriority(){
        return this.isTopPriority;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(int destinationType) {
        this.destinationType = destinationType;
    }

    public boolean isTopPriority() {
        return isTopPriority;
    }

    public void setTopPriority(boolean topPriority) {
        isTopPriority = topPriority;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", destination='" + destination + '\'' +
                ", destinationType=" + destinationType +
                ", isTopPriority=" + isTopPriority +
                ", expire=" + expire +
                ", timeUnit=" + timeUnit +
                ", delay=" + delay +
                '}';
    }
}
