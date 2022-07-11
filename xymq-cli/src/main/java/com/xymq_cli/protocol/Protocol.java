package com.xymq_cli.protocol;

/**
 * 解决消息内容粘包和拆包问题，将要传输的内容大小写入bytebuf的header中
 * @author 黎勇炫
 * @date 2022年07月07日 16:51
 */
public class Protocol {
    private Integer len;
    private byte[] content;


    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Protocol(Integer len, byte[] content) {
        this.len = len;
        this.content = content;
    }
}
