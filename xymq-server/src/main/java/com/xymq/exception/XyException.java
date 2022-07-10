package com.xymq.exception;

/**
 * @author 黎勇炫
 * @date 2022年07月10日 13:01
 */
public class XyException extends RuntimeException{
    /**
     * 错误码
     */
    private Integer code;

    /**
     * 信息
     */
    private String message;

    public XyException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public XyException(ExceptionEnum exceptionEnum){
        super(exceptionEnum.getMessage());
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
