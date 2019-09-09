package cn.loftown.wechat.app.code.filter;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.exception.PredictException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class BingExceptionHandler {
    //自定义异常返回对应编码
    @ExceptionHandler(PredictException.class)
    @ResponseBody
    public BaseResponse handlerBingException(PredictException e) {
        return new BaseResponse(BaseResponse.business_error(), e.getMessage());
    }

    //其他异常报对应的信息
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseResponse handlerSellException(Exception e) {
        return new BaseResponse(BaseResponse.system_error(), "系统出错，错误信息为：" + e.getMessage());
    }
}
