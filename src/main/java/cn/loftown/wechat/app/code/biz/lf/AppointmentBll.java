package cn.loftown.wechat.app.code.biz.lf;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.base.TableResponse;
import cn.loftown.wechat.app.code.dao.lf.AppointmentConfigDao;
import cn.loftown.wechat.app.code.dao.lf.AppointmentOrderDao;
import cn.loftown.wechat.app.code.dao.lf.CarTypeDao;
import cn.loftown.wechat.app.code.dao.lf.UserCarDao;
import cn.loftown.wechat.app.code.dto.lf.AppointmentConfigDTO;
import cn.loftown.wechat.app.code.dto.lf.AppointmentOrderDTO;
import cn.loftown.wechat.app.code.dto.lf.CarTypeDTO;
import cn.loftown.wechat.app.code.dto.lf.UserCarDTO;
import cn.loftown.wechat.app.code.dto.vo.OrderStatisticsVO;
import cn.loftown.wechat.app.code.entity.lf.AppointmentConfigModel;
import cn.loftown.wechat.app.code.entity.lf.AppointmentOrderHomeModel;
import cn.loftown.wechat.app.code.entity.lf.AppointmentOrderModel;
import cn.loftown.wechat.app.code.enums.ConfigTimeTypeEnum;
import cn.loftown.wechat.app.code.enums.OrderCancelTypeEnum;
import cn.loftown.wechat.app.code.enums.OrderStatusEnum;
import cn.loftown.wechat.app.code.enums.StatusEnum;
import cn.loftown.wechat.app.code.exception.PredictException;
import cn.loftown.wechat.app.code.model.lf.GetAppointmentOrderRequest;
import cn.loftown.wechat.app.code.util.DateTimeUtil;
import cn.loftown.wechat.app.code.util.DescribeTextUtil;
import cn.loftown.wechat.app.code.util.UsedNumberUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AppointmentBll {
    @Autowired
    UserCarDao userCarDao;
    @Autowired
    CarTypeDao carTypeDao;
    @Autowired
    AppointmentOrderDao appointmentOrderDao;
    @Autowired
    AppointmentConfigDao appointmentConfigDao;

    /**
     * 后端查询预约订单
     * @param request
     * @return
     */
    public TableResponse<AppointmentOrderModel> getAppointmentOrder(GetAppointmentOrderRequest request){
        if(request.getOrderStatus() != null && request.getOrderStatus() == 0){
            request.setOrderStatus(null);
        }
        Timestamp startTime = request.getStartTime() == null ? null : new Timestamp(request.getStartTime().getTime());
        Timestamp endTime = request.getEndTime() == null ? null : new Timestamp(request.getEndTime().getTime());
        PageHelper.startPage(request.getPageIndex(), request.getPageSize());
        List<AppointmentOrderDTO> orderDTOList = appointmentOrderDao.getOrderByAdmin(request.getUniacid(), request.getOrderStatus(), startTime , endTime);
        PageInfo<AppointmentOrderDTO> pageInfo =new PageInfo<>(orderDTOList);

        TableResponse<AppointmentOrderModel> response = new TableResponse<>();
        //实体转换
        List<AppointmentOrderModel> orderModelList = new ArrayList<>();
        for (AppointmentOrderDTO orderDTO : orderDTOList){
            AppointmentOrderModel orderModel = new AppointmentOrderModel();
            BeanUtils.copyProperties(orderDTO, orderModel);
            orderModel.setOrderStatus(OrderStatusEnum.parse(orderDTO.getOrderStatus()));
            if(orderDTO.getCancelType() != null) {
                orderModel.setCancelType(OrderCancelTypeEnum.parse(orderDTO.getCancelType()));
            }
            orderModelList.add(orderModel);
        }
        response.setData(orderModelList);
        response.setTotal(pageInfo.getTotal());
        return response;
    }

    /**
     * 根据订单编号查询订单详情
     * @param acid
     * @return
     */
    public BaseResponse<AppointmentOrderModel> getAppointmentOrderDetail(int acid){
        AppointmentOrderDTO orderDTO = appointmentOrderDao.selectByPrimaryKey(acid);
        AppointmentOrderModel orderModel = new AppointmentOrderModel();
        BeanUtils.copyProperties(orderDTO, orderModel);
        orderModel.setOrderStatus(OrderStatusEnum.parse(orderDTO.getOrderStatus()));
        if(orderDTO.getCancelType() != null) {
            orderModel.setCancelType(OrderCancelTypeEnum.parse(orderDTO.getCancelType()));
        }
        BaseResponse<AppointmentOrderModel> response = new BaseResponse<>();
        response.setData(orderModel);
        return response;
    }

    /**
     * 查询统计订单
     * @param request
     * @return
     */
    public BaseResponse<AppointmentOrderHomeModel> getOrderStatisticsByDay(GetAppointmentOrderRequest request) throws Exception{
        Timestamp startTime = request.getStartTime() == null ? null : new Timestamp(request.getStartTime().getTime());
        Timestamp endTime = request.getEndTime() == null ? null : new Timestamp(request.getEndTime().getTime());
        List<OrderStatisticsVO> statisticsList = appointmentOrderDao.getOrderStatisticsByDay(request.getUniacid(), request.getOrderStatus(), startTime, endTime);
        BaseResponse<AppointmentOrderHomeModel> response = new BaseResponse<>();
        AppointmentOrderHomeModel homeModel = new AppointmentOrderHomeModel();
        homeModel.setStatisticsList(statisticsList);

        if(request.getHasOrderData() != null && request.getHasOrderData()){
            request.setPageIndex(1);
            request.setPageSize(2);
            request.setStartTime(null);
            request.setEndTime(null);
            request.setOrderStatus(OrderStatusEnum.PROCESS.getCode());
            TableResponse<AppointmentOrderModel> processResponse  = getAppointmentOrder(request);
            homeModel.setProcessOrderList(processResponse.getData());
            homeModel.setProcessOrderCount(processResponse.getTotal().intValue());

            request.setPageSize(1);
            request.setOrderStatus(OrderStatusEnum.CONFIRM.getCode());
            String formatDate = DateTimeUtil.formatToDateString(new Date(System.currentTimeMillis()));
            request.setStartTime(DateTimeUtil.getDateToString(formatDate + " 00:00:00"));
            request.setEndTime(new Date(System.currentTimeMillis()));
            TableResponse<AppointmentOrderModel> confirmResponse  = getAppointmentOrder(request);
            homeModel.setConfirmOrderList(confirmResponse.getData());
            homeModel.setConfirmOrderCount(confirmResponse.getTotal().intValue());
        }
        response.setData(homeModel);
        return response;
    }

    /**
     * 下单
     * @param orderModel
     */
    public void addAppointmentOrder(AppointmentOrderModel orderModel){
        AppointmentConfigDTO configDTO = appointmentConfigDao.getConfigByUniacid(orderModel.getUniacid());
        if (configDTO == null){
            throw new PredictException("缺少配置");
        }
        if(configDTO.getAdvanceTime() > 0 && System.currentTimeMillis() + configDTO.getAdvanceTime() > orderModel.getAppointmentTime().getTime()){
            throw new PredictException("不能预约这个时间了哦～请往后选选");
        }
        if(configDTO.getReserveTime() >0 && orderModel.getAppointmentTime().getTime() - System.currentTimeMillis() > configDTO.getReserveTime()){
            throw new PredictException("不能预约这个时间了哦～请往前选选");
        }
        UserCarDTO userCarDTO = userCarDao.selectByPrimaryKey(orderModel.getUserCarAcid());
        if(userCarDTO == null || userCarDTO.getStatus() != StatusEnum.ENABLES.getCode() || userCarDTO.getUserid() != orderModel.getUserId()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }

        AppointmentOrderDTO orderDTO = new AppointmentOrderDTO();
        BeanUtils.copyProperties(orderModel, orderDTO);

        //如果订单需要超时取消，就加上取消时间
        if(configDTO.getInvalidTime() > 0){
            orderDTO.setCancelTime(new Date(System.currentTimeMillis() + configDTO.getInvalidTime()));
        }

        if(StringUtils.isEmpty(orderModel.getCarTypeName())){
            CarTypeDTO carTypeDTO = carTypeDao.selectByPrimaryKey(userCarDTO.getCarTypeAcid());
            if(carTypeDTO == null){
                throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
            }
            orderDTO.setCarTypeName(carTypeDTO.getName());
        }
        orderDTO.setAppointmentTime(orderModel.getAppointmentTime());
        orderDTO.setCarNumber(userCarDTO.getCarNumber());
        orderDTO.setOrderStatus(OrderStatusEnum.PROCESS.getCode());
        orderDTO.setCreateTime(new Date(System.currentTimeMillis()));
        orderDTO.setUpdateTime(orderDTO.getCreateTime());
        appointmentOrderDao.insert(orderDTO);
    }

    /**
     * 修改订单信息
     * @param model
     * @return
     */
    public void modifyAppointmentOrder(AppointmentOrderModel model){
        model.setOrderStatus(OrderStatusEnum.parse(model.getOrderStatusCode()));
        model.setCancelType(OrderCancelTypeEnum.parse(model.getCancelTypeCode()));
        if(model.getOrderStatus() == null || model.getOrderStatus().equals(OrderStatusEnum.UNKNOWN)){
            AppointmentOrderDTO orderDTO = appointmentOrderDao.selectByPrimaryKey(model.getAcid());
            if(orderDTO == null || orderDTO.getOrderStatus() != OrderStatusEnum.PROCESS.getCode()){
                throw new PredictException("订单此时不能修改");
            }

            AppointmentOrderDTO updateDTO = new AppointmentOrderDTO();
            BeanUtils.copyProperties(model, updateDTO);
            appointmentOrderDao.updateByPrimaryKey(updateDTO);
        }
        switch (model.getOrderStatus()){
            case CONFIRM:
                confirmAppointmentOrder(model);
                break;
            case FINISH:
                finishAppointmentOrder(model);
                break;
            case CANCEL:
                cancelAppointmentOrder(model);
                break;
        }
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
        updateOrderDTO.setConfirmTime(new Date(System.currentTimeMillis()));
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
        updateOrderDTO.setFinishTime(new Date(System.currentTimeMillis()));
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
        if(orderModel.getCancelType() == null || orderModel.getCancelType().equals(OrderCancelTypeEnum.UNKNOWN)){
            throw new PredictException("请选择取消原因哟～");
        }
        if(StringUtils.isEmpty(orderModel.getOperateAdminUser().toString()) && orderDTO.getUserId() != orderModel.getUserId()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        AppointmentOrderDTO updateOrderDTO = new AppointmentOrderDTO();
        updateOrderDTO.setAcid(orderModel.getAcid());
        updateOrderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        updateOrderDTO.setCancelAdminUser(orderModel.getOperateAdminUser());
        updateOrderDTO.setUpdateTime(new Date(System.currentTimeMillis()));
        updateOrderDTO.setCancelType(orderModel.getCancelType().getCode());

        appointmentOrderDao.updateByPrimaryKey(updateOrderDTO);
    }

    /**
     * 添加商家预约配置
     * @param model
     */
    public void addAppointmentConfig(AppointmentConfigModel model){
        //一个商家只能有一个配置
        AppointmentConfigDTO appointmentConfigDTO = appointmentConfigDao.getConfigByUniacid(model.getUniacid());
        if(appointmentConfigDTO != null){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }

        AppointmentConfigDTO configDTO = new AppointmentConfigDTO();

        saveConfigTime(model, configDTO);

        configDTO.setAppointmentConfig(model.getSelectViewData());
        configDTO.setAutoConfirm(model.getAutoConfirm());
        configDTO.setUniacid(model.getUniacid());
        configDTO.setCreateUser(model.getOperateAdminUser());
        configDTO.setCreateTime(new Date(System.currentTimeMillis()));
        configDTO.setUpdateTime(configDTO.getCreateTime());
        configDTO.setUpdateUser(0);
        appointmentConfigDao.insert(configDTO);
    }

    /**
     * 修改商家预约配置
     * @param model
     */
    public void modifyAppointmentConfig(AppointmentConfigModel model){
        AppointmentConfigDTO configDTO = appointmentConfigDao.selectByPrimaryKey(model.getAcid());
        if(configDTO == null || configDTO.getUniacid() != model.getUniacid()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        saveConfigTime(model, configDTO);

        configDTO.setAppointmentConfig(model.getSelectViewData());
        configDTO.setAutoConfirm(model.getAutoConfirm());
        configDTO.setUpdateUser(model.getOperateAdminUser());
        configDTO.setUpdateTime(new Date(System.currentTimeMillis()));
        appointmentConfigDao.updateByPrimaryKey(configDTO);
    }

    /**
     * 查询商家预约配置
     * @param uniacid
     * @return
     */
    public AppointmentConfigModel getAppointmentConfig(int uniacid){
        AppointmentConfigDTO configDTO = appointmentConfigDao.getConfigByUniacid(uniacid);
        if(configDTO == null){
            return null;
        }

        AppointmentConfigModel model = new AppointmentConfigModel();
        model.setAcid(configDTO.getAcid());
        model.setUniacid(configDTO.getUniacid());
        model.setAutoConfirm(configDTO.getAutoConfirm());
        model.setSelectViewData(configDTO.getAppointmentConfig());
        transforConfigTime(configDTO, model);
        return model;
    }

    /**
     * 时间转换为时间戳
     * @param model
     * @param configDTO
     */
    private void saveConfigTime(AppointmentConfigModel model, AppointmentConfigDTO configDTO){
        ConfigTimeTypeEnum advanceTimeType = ConfigTimeTypeEnum.parse(model.getAdvanceType());
        if(advanceTimeType == null || advanceTimeType.equals(ConfigTimeTypeEnum.UNKNOWN)){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        configDTO.setAdvanceTime(getConfigTime(advanceTimeType, model.getAdvanceNum()));

        ConfigTimeTypeEnum reserveTimeType = ConfigTimeTypeEnum.parse(model.getReserveType());
        if(reserveTimeType == null || reserveTimeType.equals(ConfigTimeTypeEnum.UNKNOWN)){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        configDTO.setReserveTime(getConfigTime(reserveTimeType, model.getReserveNum()));

        if(model.getHasInvalid() == 2) {
            ConfigTimeTypeEnum invalidTimeType = ConfigTimeTypeEnum.parse(model.getInvalidType());
            if (invalidTimeType == null || invalidTimeType.equals(ConfigTimeTypeEnum.UNKNOWN)) {
                throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
            }
            configDTO.setInvalidTime(getConfigTime(invalidTimeType, model.getInvalidNum()));
        } else {
            configDTO.setInvalidTime(0L);
        }
    }



    /**
     * 时间戳转换为页面展示的时间
     * @param configDTO
     * @param model
     */
    private void transforConfigTime(AppointmentConfigDTO configDTO, AppointmentConfigModel model) {

        ConfigTimeTypeEnum advanceTimeType = getTimeType(configDTO.getAdvanceTime());
        model.setAdvanceType(advanceTimeType.getCode());
        model.setAdvanceNum(getTimeNum(advanceTimeType, configDTO.getAdvanceTime()));

        ConfigTimeTypeEnum reserveTimeType = getTimeType(configDTO.getReserveTime());
        model.setReserveType(reserveTimeType.getCode());
        model.setReserveNum(getTimeNum(reserveTimeType, configDTO.getReserveTime()));

        if (configDTO.getInvalidTime() == 0) {
            model.setHasInvalid(1);
            model.setInvalidType(1);
            model.setInvalidNum(1);
        } else {
            model.setHasInvalid(2);
            ConfigTimeTypeEnum invalidTimeType = getTimeType(configDTO.getInvalidTime());
            model.setInvalidType(invalidTimeType.getCode());
            model.setInvalidNum(getTimeNum(invalidTimeType, configDTO.getInvalidTime()));
        }
    }


    private ConfigTimeTypeEnum getTimeType(long num){
        if(num / UsedNumberUtil.YEAR_TIME_STAMP > 0){
            return ConfigTimeTypeEnum.YEAR;
        } else if(num / UsedNumberUtil.MONTH_TIME_STAMP > 0){
            return ConfigTimeTypeEnum.MONTH;
        } if(num / UsedNumberUtil.WEEK_TIME_STAMP > 0){
            return ConfigTimeTypeEnum.WEEK;
        } if(num / UsedNumberUtil.DAY_TIME_STAMP > 0){
            return ConfigTimeTypeEnum.DAY;
        } if(num / UsedNumberUtil.HOUR_TIME_STAMP > 0){
            return ConfigTimeTypeEnum.HOUR;
        }
        return ConfigTimeTypeEnum.UNKNOWN;
    }

    private Integer getTimeNum(ConfigTimeTypeEnum timeType, long num){
        Long advanceTime = 0L;
        switch (timeType){
            case HOUR:
                advanceTime = num / UsedNumberUtil.HOUR_TIME_STAMP;
                break;
            case DAY:
                advanceTime = num / UsedNumberUtil.DAY_TIME_STAMP;
                break;
            case WEEK:
                advanceTime = num / UsedNumberUtil.MONTH_TIME_STAMP;
                break;
            case MONTH:
                advanceTime = num / UsedNumberUtil.MONTH_TIME_STAMP;
                break;
            case YEAR:
                advanceTime = num / UsedNumberUtil.YEAR_TIME_STAMP;
                break;
        }
        return advanceTime.intValue();
    }

    /**
     * 页面展示的时间转换时间戳
     * @param timeType
     * @param num
     * @return
     */
    private Long getConfigTime(ConfigTimeTypeEnum timeType, long num) {
        switch (timeType) {
            case HOUR:
                return UsedNumberUtil.HOUR_TIME_STAMP * num;
            case DAY:
                return UsedNumberUtil.DAY_TIME_STAMP * num;
            case WEEK:
                return UsedNumberUtil.MONTH_TIME_STAMP * num;
            case MONTH:
                return UsedNumberUtil.MONTH_TIME_STAMP * num;
            case YEAR:
                return UsedNumberUtil.YEAR_TIME_STAMP * num;
            default:
                return 0L;
        }
    }
}
