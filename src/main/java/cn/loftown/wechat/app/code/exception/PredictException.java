package cn.loftown.wechat.app.code.exception;

public class PredictException extends RuntimeException {
    public PredictException() {
    }

    public PredictException(String message) {
        super(message);
    }

    public PredictException(String message, Throwable cause) {
        super(message, cause);
    }

    public PredictException(Throwable cause) {
        super(cause);
    }

    public PredictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}