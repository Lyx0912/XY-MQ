# XY-MQ
一款基于Netty+SpringBoot+LevelDB实现的拥有高性能、高并发、高可靠性的消息队列，支持 队列消息、主题消息、延时消息、消息持久化、消息插队。简单易用，上手快。延时消息可以设置到毫秒。采用高效的kv数据库Leveldb进行数据持久化配合Java原生缓存队列，实现同步存储、异步分发，每秒可生产和消费50000+条消息。

## 系统架构图

![image-20220722143333878](https://github.com/Lyx0912/XY-MQ/blob/main/doc/%E7%B3%BB%E7%BB%9F%E6%9E%B6%E6%9E%84.png)