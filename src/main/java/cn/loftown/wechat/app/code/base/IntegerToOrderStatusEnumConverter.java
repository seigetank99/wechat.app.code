package cn.loftown.wechat.app.code.base;

import cn.loftown.wechat.app.code.enums.OrderStatusEnum;
import org.springframework.core.convert.converter.Converter;

public class IntegerToOrderStatusEnumConverter implements Converter<Integer, OrderStatusEnum> {

    @Override
    public OrderStatusEnum convert(Integer code) {
        if (code == null) {
            return null;
        }
        return OrderStatusEnum.parse(code);
    }
}
