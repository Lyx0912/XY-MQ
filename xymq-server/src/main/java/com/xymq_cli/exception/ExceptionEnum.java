package com.xymq_cli.exception;

/**
 * @author 黎勇炫
 * @date 2022年07月10日 13:02
 */
public enum ExceptionEnum {

     /**
       * 1开头为处理器执行异常
       */
    ECEC_STRETEGY_NOT_FOUNT(101,"找不到执行处理器"),
     /**
       * 1开头为leveldb异常
       */
    LEVELDB_INIT_ERROR(201,"levelDb初始化失败"),
    FAILED_TO_STORAGE(202,"消息持久化失败"),
    FAILED_TO_RECOVERY_DATA(203,"消息时恢复发生异常"),
    FAILED_TO_CLEAN_DATA(204,"数据清理失败"),
    FAILED_TO_GET_KEYS(205,"获取keys失败"),
    FAILED_TO_CLOSE_DB(206,"levelDb关闭失败");





    private Integer code;
    private String message;

    ExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
