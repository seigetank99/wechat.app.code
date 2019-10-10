package cn.loftown.wechat.app.code.entity.lf;

import cn.loftown.wechat.app.code.dto.vo.OrderStatisticsVO;
import lombok.Data;

import java.util.List;

@Data
public class AppointmentOrderHomeModel {
    /**
     * 地图统计
     */
    private List<OrderStatisticsVO> statisticsList;
    /**
     * 待确认订单2条
     */
    private List<AppointmentOrderModel> processOrderList;
    /**
     * 已确认订单1条
     */
    private List<AppointmentOrderModel> confirmOrderList;
    /**
     * 待确认订单总个数
     */
    private Integer processOrderCount;
    /**
     * 已确认订单总个数
     */
    private Integer confirmOrderCount;
}
