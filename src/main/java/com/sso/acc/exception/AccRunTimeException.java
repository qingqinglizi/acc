package com.sso.acc.exception;

/**
 * Project：acc
 * Date：2021/1/12
 * Time：17:49
 * Description：design exception
 *
 * @author lee
 * @version 1.0
 */
public class AccRunTimeException extends RuntimeException {

    private int code;

    private String exceptionMessage;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public AccRunTimeException(String exceptionMessage) {
        super(exceptionMessage);
        this.exceptionMessage = exceptionMessage;
    }

    public AccRunTimeException(int code, String exceptionMessage) {
        super(exceptionMessage);
        this.code = code;
        this.exceptionMessage = exceptionMessage;
    }
}
