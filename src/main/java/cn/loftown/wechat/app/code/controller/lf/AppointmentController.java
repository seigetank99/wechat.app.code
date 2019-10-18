package cn.loftown.wechat.app.code.controller.lf;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.base.TableResponse;
import cn.loftown.wechat.app.code.biz.lf.AppointmentBll;
import cn.loftown.wechat.app.code.entity.lf.AppointmentConfigModel;
import cn.loftown.wechat.app.code.entity.lf.AppointmentOrderHomeModel;
import cn.loftown.wechat.app.code.entity.lf.AppointmentOrderModel;
import cn.loftown.wechat.app.code.entity.lf.CarTypeModel;
import cn.loftown.wechat.app.code.model.lf.GetAppointmentOrderRequest;
import cn.loftown.wechat.app.code.model.lf.GetCarTypeRequest;
import cn.loftown.wechat.app.code.util.DescribeTextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/lf")
public class AppointmentController {
    @Autowired
    AppointmentBll appointmentBll;

    @RequestMapping(value = "/appointmentConfig/add", method = RequestMethod.POST)
    public BaseResponse addAppointmentConfig(AppointmentConfigModel model) {
        try {
            appointmentBll.addAppointmentConfig(model);
            return new BaseResponse();
        } catch (Exception ex){
            return new BaseResponse(DescribeTextUtil.SYSTEM_ERROR);
        }
    }

    @RequestMapping(value = "/appointmentConfig/modify", method = RequestMethod.POST)
    public BaseResponse modifyAppointmentConfig(AppointmentConfigModel model) {
        try {
            appointmentBll.modifyAppointmentConfig(model);
            return new BaseResponse();
        } catch (Exception ex){
            return new BaseResponse(DescribeTextUtil.SYSTEM_ERROR);
        }
    }

    @RequestMapping(value = "/appointmentConfig/get", method = RequestMethod.POST)
    public BaseResponse getAppointmentConfig(@RequestParam("uniacid") Integer uniacid) {
        try {
            AppointmentConfigModel model = appointmentBll.getAppointmentConfig(uniacid);
            BaseResponse<AppointmentConfigModel> response = new BaseResponse();
            response.setData(model);
            return response;
        } catch (Exception ex) {
            return new BaseResponse(DescribeTextUtil.SYSTEM_ERROR);
        }
    }

    @RequestMapping(value = "/appointmentOrder/getCarType", method = RequestMethod.POST)
    public TableResponse<CarTypeModel> getAppointmentCarType(GetCarTypeRequest request) {
        return appointmentBll.getAppointmentCarType(request);
    }

    @RequestMapping(value = "/appointmentOrder/get", method = RequestMethod.POST)
    public TableResponse<AppointmentOrderModel> getAppointmentOrder(GetAppointmentOrderRequest request) {
        return appointmentBll.getAppointmentOrder(request);
    }

    @RequestMapping(value = "/appointmentOrder/getDetail", method = RequestMethod.POST)
    public BaseResponse<AppointmentOrderModel> getAppointmentOrderDetail(@RequestParam("acid") Integer acid) {
        return appointmentBll.getAppointmentOrderDetail(acid);
    }

    @RequestMapping(value = "/appointmentOrder/statistics", method = RequestMethod.POST)
    public BaseResponse<AppointmentOrderHomeModel> getOrderStatisticsByDay(GetAppointmentOrderRequest request) {
        try {
            return appointmentBll.getOrderStatisticsByDay(request);
        } catch (Exception ex){
            return new BaseResponse(DescribeTextUtil.SYSTEM_ERROR);
        }
    }

    @RequestMapping(value = "/appointmentOrder/modify", method = RequestMethod.POST)
    public BaseResponse modifyAppointmentOrder(AppointmentOrderModel model) {
        try {
            appointmentBll.modifyAppointmentOrder(model);
            return new BaseResponse();
        } catch (Exception ex){
            return new BaseResponse(DescribeTextUtil.SYSTEM_ERROR);
        }
    }

    @RequestMapping(value = "/appointmentOrder/modifyCarType", method = RequestMethod.POST)
    public BaseResponse modifyAppointmentModifyCarType(CarTypeModel model) {
        try {
            appointmentBll.modifyAppointmentCarType(model);
            return new BaseResponse();
        } catch (Exception ex){
            return new BaseResponse(DescribeTextUtil.SYSTEM_ERROR);
        }
    }
}
