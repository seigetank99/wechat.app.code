package cn.loftown.wechat.app.code.biz.lf;

import cn.loftown.wechat.app.code.dao.lf.AppointmentOrderDao;
import cn.loftown.wechat.app.code.dao.lf.CarTypeDao;
import cn.loftown.wechat.app.code.dao.lf.UserCarDao;
import cn.loftown.wechat.app.code.dto.lf.AppointmentOrderDTO;
import cn.loftown.wechat.app.code.dto.lf.CarTypeDTO;
import cn.loftown.wechat.app.code.dto.lf.UserCarDTO;
import cn.loftown.wechat.app.code.enums.OrderCancelTypeEnum;
import cn.loftown.wechat.app.code.enums.OrderStatusEnum;
import cn.loftown.wechat.app.code.enums.StatusEnum;
import cn.loftown.wechat.app.code.exception.PredictException;
import cn.loftown.wechat.app.code.model.lf.AppointmentOrderModel;
import cn.loftown.wechat.app.code.util.DescribeTextUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Date;

public class AppointmentOrderBll {
    @Autowired
    UserCarDao userCarDao;
    @Autowired
    CarTypeDao carTypeDao;
    @Autowired
    AppointmentOrderDao appointmentOrderDao;

    /**
     * 下单
     * @param orderModel
     */
    public void addAppointmentOrder(AppointmentOrderModel orderModel){
        UserCarDTO userCarDTO = userCarDao.selectByPrimaryKey(orderModel.getUserCarAcid());
        if(userCarDTO == null || userCarDTO.getStatus() != StatusEnum.ENABLES.getCode() || userCarDTO.getUserid() != orderModel.getUserid()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }

        AppointmentOrderDTO orderDTO = new AppointmentOrderDTO();
        BeanUtils.copyProperties(orderModel, orderDTO);

        if(StringUtils.isEmpty(orderModel.getCarTypeName())){
            CarTypeDTO carTypeDTO = carTypeDao.selectByPrimaryKey(userCarDTO.getCarTypeAcid());
            if(carTypeDTO == null){
                throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
            }
            orderModel.setCarTypeName(String.format("%s %s", carTypeDTO.getName(), userCarDTO.getCarNumber()));
        }
        orderDTO.setCarNumber(userCarDTO.getCarNumber());
        orderDTO.setOrderStatus(OrderStatusEnum.PROCESS.getCode());
        orderDTO.setCreateTime(new Date());
        orderDTO.setUpdateTime(orderDTO.getCreateTime());
        appointmentOrderDao.insert(orderDTO);
    }

    /**
     * 确认订单
     * @param orderModel
     */
    public void confirmAppointmentOrder(AppointmentOrderModel orderModel){
        AppointmentOrderDTO orderDTO = appointmentOrderDao.selectByPrimaryKey(orderModel.getAcid());
        if(orderDTO == null || orderDTO.getOrderStatus() != OrderStatusEnum.PROCESS.getCode()){
            throw new PredictException("订单此时不能确认");
        }

        AppointmentOrderDTO updateOrderDTO = new AppointmentOrderDTO();
        updateOrderDTO.setAcid(orderModel.getAcid());
        updateOrderDTO.setOrderStatus(OrderStatusEnum.CONFIRM.getCode());
        updateOrderDTO.setConfirmAdminUser(orderModel.getOperateAdminUser());
        updateOrderDTO.setConfirmTime(new Date());
        updateOrderDTO.setUpdateTime(updateOrderDTO.getConfirmTime());

        appointmentOrderDao.updateByPrimaryKey(updateOrderDTO);
    }

    /**
     * 完成订单
     * @param orderModel
     */
    public void finishAppointmentOrder(AppointmentOrderModel orderModel){
        AppointmentOrderDTO orderDTO = appointmentOrderDao.selectByPrimaryKey(orderModel.getAcid());
        if(orderDTO == null || orderDTO.getOrderStatus() != OrderStatusEnum.CONFIRM.getCode()){
            throw new PredictException("订单此时不能完成");
        }

        AppointmentOrderDTO updateOrderDTO = new AppointmentOrderDTO();
        updateOrderDTO.setAcid(orderModel.getAcid());
        updateOrderDTO.setOrderStatus(OrderStatusEnum.FINISH.getCode());
        updateOrderDTO.setFinishAdminUser(orderModel.getOperateAdminUser());
        updateOrderDTO.setFinishTime(new Date());
        updateOrderDTO.setUpdateTime(updateOrderDTO.getFinishTime());

        appointmentOrderDao.updateByPrimaryKey(updateOrderDTO);
    }

    /**
     * 取消订单
     * @param orderModel
     */
    public void cancelAppointmentOrder(AppointmentOrderModel orderModel){
        AppointmentOrderDTO orderDTO = appointmentOrderDao.selectByPrimaryKey(orderModel.getAcid());
        if(orderDTO == null || (orderDTO.getOrderStatus() != OrderStatusEnum.PROCESS.getCode() && orderDTO.getOrderStatus() != OrderStatusEnum.CONFIRM.getCode())){
            throw new PredictException("订单此时不能取消");
        }
        OrderCancelTypeEnum orderCancelTypeEnum = OrderCancelTypeEnum.parse(orderModel.getCancelType());
        if(orderCancelTypeEnum == null || orderCancelTypeEnum.equals(OrderCancelTypeEnum.UNKNOWN)){
            throw new PredictException("请选择取消原因哟～");
        }
        if(StringUtils.isEmpty(orderModel.getOperateAdminUser().toString()) && orderDTO.getUserid() != orderModel.getUserid()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        AppointmentOrderDTO updateOrderDTO = new AppointmentOrderDTO();
        updateOrderDTO.setAcid(orderModel.getAcid());
        updateOrderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        updateOrderDTO.setCancelAdminUser(orderModel.getOperateAdminUser());
        updateOrderDTO.setUpdateTime(new Date());
        updateOrderDTO.setCancelType(orderCancelTypeEnum.getCode());

        appointmentOrderDao.updateByPrimaryKey(updateOrderDTO);
    }
}
