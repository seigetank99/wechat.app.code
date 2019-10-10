package cn.loftown.wechat.app.code.controller.lf;

import cn.loftown.wechat.app.code.base.BaseResponse;
import cn.loftown.wechat.app.code.biz.lf.UserCarBll;
import cn.loftown.wechat.app.code.entity.lf.UserCarModel;
import cn.loftown.wechat.app.code.util.DescribeTextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/lf")
public class UserCarManageController {
    @Autowired
    UserCarBll userCarBll;

    @RequestMapping(value = "/userCar/add", method = RequestMethod.POST)
    public BaseResponse addUserCar(UserCarModel model) {
        try {
            userCarBll.addUserCar(model);
            return new BaseResponse();
        } catch (Exception ex){
            return new BaseResponse(DescribeTextUtil.SYSTEM_ERROR);
        }
    }

    public BaseResponse getUserCar(@RequestParam("uniacid") Integer uniacid, @RequestParam("userId") Integer userId){
        return new BaseResponse();
    }
}
