# XY-MQ
一款基于Netty+SpringBoot+LevelDB实现的拥有高性能、高并发、高可靠性、轻量级的消息队列，支持 队列消息、主题消息、延时消息、消息持久化、消息插队。简单易用，上手快。延时消息可以设置到毫秒。采用高效的kv数据库Leveldb进行数据持久化，JDK原生阻塞队列作为内存级容器，实现同步存储、异步分发，每秒可生产和消费50000+条消息。

### 系统架构

![image-20220722143333878](https://github.com/Lyx0912/XY-MQ/blob/main/doc/%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84.png)

持久化模型

![image-20220722143333879](https://github.com/Lyx0912/XY-MQ/blob/main/doc/%E6%B6%88%E6%81%AF%E6%8C%81%E4%B9%85%E5%8C%96%E6%A8%A1%E5%9E%8B.png)



### 优点

1.高性能，基于netty实现数据通信+高效的kv数据库Leveldb存储,快的飞起！

2.支持多种消息类型，例如队列消息、主题消息、延时消息，还可以设置优先级。

3.系统体积小，整个系统才不到3M。

4.支持高并发。

### 待优化

1.延迟消息BUG：延时消息基于jdk自带的delayQueue实现，系统宕机重启后服务端读取leveldb中的消息后将消息重新放回延时队列，会重新设置到期时间。例如:设置一条消息5分钟后推送，中途系统宕机，系统重启后会从当前时间开始重新计时5分钟。

2.异步推送下消息乱序：原先设想是每条队列来消息时，就会交给线程池专门用一条线程负责推送这条队列的消息，直到消息推送完毕。在推送时有新的消息进入容器可能会出现多个线程推送同一个队列的情况，造成消息乱序。而且这种推送模式在队列太多的情况下反而会影响性能，后面考虑使用单线程推送。

3.可视化界面：需要一个Web端可视化界面监控系统的运行情况。



### 使用Demo

##### 生产者

``

```java
 /**
   * 生产者实例
   */
public static void main(String[] args) {
    // 创建生产者
    Producer producer = new Producer();
    // 推送普通的队列消息
    producer.sendMsg("你好，我是队列消息","queue");
    // 推送主题消息
    producer.publish("你好，我是主题消息","topic");
    // 推送延迟消息，设置延迟数和单位，消息会在5分钟后推送给消费者
    producer.sendDelayMessage("你好，我是延时队列消息","queueDelayM",5,TimeUnit.SECONDS);
    // 推送延迟主题消息
    producer.sendDelayMessage("你好，我是延时主题消息","queueDelayT",5,TimeUnit.SECONDS);
    // 设置优先级，消息会插入到队列头
    producer.sendPriorityMessage("你好，我是队列消息","queue");
}
```

##### 消费者

``

```java
 /**
   * 消费者示例
   */
public static void main(String[] args) {
    // 指定‘queue’队列
    Consumer consumer = new Consumer("queue");
    // 构建监听器
    consumer.createListener(new MessageListener() {
        @Override
        public void getMessage(MessageData data) {
            // 监听到消息会进入MessageListener监听器中
            System.out.println(data.getMessage());
        }
    }).run();
    // 关闭消费者
    consumer.close();
}
```

##### 订阅者

``

```java
 /**
   * 订阅者消费示例
   */
public static void main(String[] args) {
    // 构建订阅者订阅'topic'主题，订阅者编号为1
    Subscriber subscriber = new Subscriber("topic",1);
    subscriber.createListener(new MessageListener() {
        @Override
        public void getMessage(MessageData data) {
            System.out.println(data.getMessage());
        }
    }).run();
    
    // 关闭订阅者
    subscriber.close();
}
```



### 