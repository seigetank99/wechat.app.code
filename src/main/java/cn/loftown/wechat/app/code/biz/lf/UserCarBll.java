package cn.loftown.wechat.app.code.biz.lf;

import cn.loftown.wechat.app.code.dao.lf.CarTypeDao;
import cn.loftown.wechat.app.code.dao.lf.CarTypeInfoDao;
import cn.loftown.wechat.app.code.dao.lf.UserCarDao;
import cn.loftown.wechat.app.code.dto.lf.CarTypeDTO;
import cn.loftown.wechat.app.code.dto.lf.CarTypeInfoDTO;
import cn.loftown.wechat.app.code.dto.lf.UserCarDTO;
import cn.loftown.wechat.app.code.dto.vo.UserCarVO;
import cn.loftown.wechat.app.code.enums.CarTypeEnum;
import cn.loftown.wechat.app.code.enums.OrderStatusEnum;
import cn.loftown.wechat.app.code.enums.StatusEnum;
import cn.loftown.wechat.app.code.exception.PredictException;
import cn.loftown.wechat.app.code.entity.lf.CarTypeInfoModel;
import cn.loftown.wechat.app.code.entity.lf.CarTypeModel;
import cn.loftown.wechat.app.code.entity.lf.UserCarModel;
import cn.loftown.wechat.app.code.util.DescribeTextUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UserCarBll {
    @Autowired
    UserCarDao userCarDao;
    @Autowired
    CarTypeDao carTypeDao;
    @Autowired
    CarTypeInfoDao carTypeInfoDao;

    /**
     * 前端展示我的爱车列表
     * @param userId
     * @return
     */
    public List<UserCarModel> getUserCar(int userId){
        List<UserCarModel> userCarModels = new ArrayList<>();
        List<UserCarVO> userCarVOList = userCarDao.getUserCar(userId, OrderStatusEnum.FINISH.getCode(), StatusEnum.ENABLES.getCode(), null);
        for (UserCarVO userCarVO : userCarVOList){
            UserCarModel userCarModel = new UserCarModel();
            BeanUtils.copyProperties(userCarVO, userCarModel);
            userCarModels.add(userCarModel);
        }
        return userCarModels;
    }

    /**
     * 添加爱车
     * @param model
     */
    public void addUserCar(UserCarModel model){
        CarTypeDTO carTypeDTO = carTypeDao.selectByPrimaryKey(model.getCarTypeId());
        if(carTypeDTO == null || carTypeDTO.getStatus() != StatusEnum.ENABLES.getCode() || carTypeDTO.getTypeId() != CarTypeEnum.APPOINTMENT.getCode()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        if(userCarDao.getUserCarCountByUser(model.getUserId(), StatusEnum.ENABLES.getCode(), model.getCarNumber()) > 0){
            throw new PredictException(String.format("您已经添加过车牌为：%s的爱车了哟", model.getCarNumber()));
        }
        if(userCarDao.getUserCarCountByUser(model.getUserId(), StatusEnum.ENABLES.getCode(), null) >= 20){
            throw new PredictException("每个人最多只能添加20辆爱车哟");
        }
        UserCarDTO userCarDTO = new UserCarDTO();
        userCarDTO.setCarNumber(model.getCarNumber());
        userCarDTO.setUniacid(model.getUniacid());
        userCarDTO.setCarTypeAcid(model.getCarTypeId());
        userCarDTO.setCreateTime(new Date(System.currentTimeMillis()));
        userCarDTO.setUpdateTime(userCarDTO.getCreateTime());
        userCarDTO.setUserid(model.getUserId());
        userCarDTO.setStatus(StatusEnum.ENABLES.getCode());
        userCarDao.insert(userCarDTO);
    }

    /**
     * 修改爱车
     * @param userCarId
     * @param userId
     * @param carType
     * @param carNumber
     */
    public void modifyUserCar(int userCarId, int userId, int carType, String carNumber){
        UserCarDTO userCarDTO = userCarDao.selectByPrimaryKey(userCarId);
        if(userCarDTO == null || userCarDTO.getUserid() != userId || userCarDTO.getStatus() != StatusEnum.ENABLES.getCode()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        if(userCarDTO.getCarTypeAcid() == carType && userCarDTO.getCarNumber().equals(carNumber)){
            return;
        }
        UserCarDTO updateDTO = new UserCarDTO();
        updateDTO.setAcid(userCarDTO.getAcid());
        if(userCarDTO.getCarTypeAcid() != carType) {
            CarTypeDTO carTypeDTO = carTypeDao.selectByPrimaryKey(carType);
            if (carTypeDTO == null || carTypeDTO.getStatus() != StatusEnum.ENABLES.getCode() || carTypeDTO.getTypeId() != CarTypeEnum.APPOINTMENT.getCode()) {
                throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
            }
            updateDTO.setCarTypeAcid(carType);
        }
        updateDTO.setCarNumber(carNumber);
        updateDTO.setUpdateTime(new Date(System.currentTimeMillis()));
        userCarDao.updateByPrimaryKey(updateDTO);
    }

    /**
     * 删除爱车
     * @param userCarId
     * @param userId
     */
    public void delUserCar(int userCarId, int userId){
        UserCarDTO userCarDTO = userCarDao.selectByPrimaryKey(userCarId);
        if(userCarDTO == null || userCarDTO.getUserid() != userId){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        if(userCarDTO.getStatus() != StatusEnum.ENABLES.getCode()){
            return;
        }
        UserCarDTO updateDTO = new UserCarDTO();
        updateDTO.setAcid(userCarDTO.getAcid());
        updateDTO.setStatus(StatusEnum.DISABLES.getCode());
        updateDTO.setUpdateTime(new Date(System.currentTimeMillis()));
        userCarDao.updateByPrimaryKey(updateDTO);
    }

    /**
     * 添加车辆类型
     * @param carTypeModel
     */
    public void addCarType(CarTypeModel carTypeModel){
        CarTypeEnum carTypeEnum = CarTypeEnum.parse(carTypeModel.getTypeId());
        if(carTypeEnum == null || CarTypeEnum.UNKNOWN.equals(carTypeEnum)){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        if(carTypeDao.getCarTypeCount(carTypeModel.getUniacid(), StatusEnum.ENABLES.getCode(), carTypeModel.getTypeId(), carTypeModel.getName()) > 0){
            throw new PredictException(String.format("车型：%s已存在", carTypeModel.getName()));
        }
        if(carTypeDao.getCarTypeCount(carTypeModel.getUniacid(), StatusEnum.ENABLES.getCode(), carTypeModel.getTypeId(), null) >= 100){
            throw new PredictException("车型数量超出限制");
        }
        CarTypeDTO carTypeDTO = new CarTypeDTO();
        BeanUtils.copyProperties(carTypeModel, carTypeDTO);
        if(carTypeDTO.getParentAcid() == null || carTypeDTO.getParentAcid() < 0){
            carTypeDTO.setParentAcid(0);
        }
        carTypeDTO.setStatus(StatusEnum.ENABLES.getCode());
        carTypeDTO.setCreateTime(new Date(System.currentTimeMillis()));
        carTypeDTO.setUpdateTime(carTypeDTO.getCreateTime());
        carTypeDao.insert(carTypeDTO);
    }
    /**
     * 修改车辆类型
     * @param carTypeModel
     */
    public void modifyCarType(CarTypeModel carTypeModel) {
        CarTypeDTO carTypeDTO = carTypeDao.selectByPrimaryKey(carTypeModel.getAcid());
        if(carTypeDTO == null || carTypeDTO.getUniacid() != carTypeModel.getUniacid() || carTypeDTO.getStatus() != StatusEnum.ENABLES.getCode()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        CarTypeDTO updateDTO = new CarTypeDTO();
        BeanUtils.copyProperties(carTypeModel, updateDTO);
        updateDTO.setUpdateTime(new Date(System.currentTimeMillis()));

        carTypeDao.updateByPrimaryKey(updateDTO);
    }

    /**
     * 删除车辆类型
     * @param uniacid
     * @param acid
     */
    public void delCarType(Integer uniacid, Integer acid) {
        CarTypeDTO carTypeDTO = carTypeDao.selectByPrimaryKey(acid);
        if(carTypeDTO == null || carTypeDTO.getUniacid() != uniacid){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        if(carTypeDTO.getStatus() != StatusEnum.ENABLES.getCode()){
            return;
        }
        CarTypeDTO updateDTO = new CarTypeDTO();
        updateDTO.setAcid(acid);
        updateDTO.setUpdateTime(new Date(System.currentTimeMillis()));
        updateDTO.setStatus(StatusEnum.DISABLES.getCode());
        carTypeDao.updateByPrimaryKey(updateDTO);
    }

    /**
     * 添加车辆类型详情
     * @param carTypeInfoModel
     */
    public void addCarTypeInfo(CarTypeInfoModel carTypeInfoModel){
        CarTypeDTO carTypeDTO = carTypeDao.selectByPrimaryKey(carTypeInfoModel.getCarTypeAcid());
        if(carTypeDTO == null || carTypeDTO.getStatus() != StatusEnum.ENABLES.getCode() || carTypeDTO.getTypeId() != CarTypeEnum.MODEL.getCode()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        if(carTypeInfoDao.getCarTypeInfoCount(carTypeInfoModel.getUniacid(), StatusEnum.ENABLES.getCode(), carTypeInfoModel.getCarTypeAcid()) >= 20){
            throw new PredictException(String.format("车型:%s最多添加20个详情", carTypeDTO.getName()));
        }
        CarTypeInfoDTO carTypeInfoDTO = new CarTypeInfoDTO();
        BeanUtils.copyProperties(carTypeInfoModel, carTypeInfoDTO);
        carTypeInfoDTO.setStatus(StatusEnum.ENABLES.getCode());
        carTypeInfoDTO.setCreateAdminUser(carTypeInfoModel.getOperateAdminUser());
        carTypeInfoDTO.setUpdateAdminUser(carTypeInfoModel.getOperateAdminUser());
        carTypeInfoDTO.setCreateTime(new Date(System.currentTimeMillis()));
        carTypeInfoDTO.setUpdateTime(carTypeDTO.getCreateTime());
        carTypeDao.insert(carTypeDTO);
    }

    /**
     * 修改车辆类型详情
     * @param carTypeInfoModel
     */
    public void modifyCarTypeInfo(CarTypeInfoModel carTypeInfoModel){
        CarTypeInfoDTO carTypeInfoDTO = carTypeInfoDao.selectByPrimaryKey(carTypeInfoModel.getAcid());
        if(carTypeInfoDTO == null || carTypeInfoDTO.getUniacid() != carTypeInfoDTO.getUniacid() || carTypeInfoDTO.getStatus() != StatusEnum.ENABLES.getCode()){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        CarTypeInfoDTO updateDTO = new CarTypeInfoDTO();
        BeanUtils.copyProperties(carTypeInfoModel, updateDTO);
        updateDTO.setUpdateAdminUser(carTypeInfoModel.getOperateAdminUser());
        updateDTO.setUpdateTime(new Date(System.currentTimeMillis()));

        carTypeInfoDao.updateByPrimaryKey(updateDTO);
    }

    /**
     * 删除车辆类型详情
     * @param uniacid
     * @param acid
     */
    public void delCarTypeInfo(Integer uniacid, Integer acid, Integer operateAdminUser) {
        CarTypeInfoDTO carTypeInfoDTO = carTypeInfoDao.selectByPrimaryKey(acid);
        if(carTypeInfoDTO == null || carTypeInfoDTO.getUniacid() != uniacid){
            throw new PredictException(DescribeTextUtil.SYSTEM_ERROR);
        }
        if(carTypeInfoDTO.getStatus() != StatusEnum.ENABLES.getCode()){
            return;
        }
        CarTypeInfoDTO updateDTO = new CarTypeInfoDTO();
        updateDTO.setAcid(acid);
        updateDTO.setUpdateAdminUser(operateAdminUser);
        updateDTO.setUpdateTime(new Date(System.currentTimeMillis()));
        updateDTO.setStatus(StatusEnum.DISABLES.getCode());
        carTypeInfoDao.updateByPrimaryKey(updateDTO);
    }
}
