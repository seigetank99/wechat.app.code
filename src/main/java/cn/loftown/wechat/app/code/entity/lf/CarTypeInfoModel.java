package cn.loftown.wechat.app.code.entity.lf;

import cn.loftown.wechat.app.code.entity.BaseModel;
import lombok.Data;

@Data
public class CarTypeInfoModel extends BaseModel {
    private Integer carTypeAcid;
    /**
     * 主标题
     */
    private String name;
    /**
     * 副标题
     */
    private String title;
    /**
     * Banner图片
     */
    private String banner;
    /**
     * 联系号码
     */
    private String storefrontPhone;
    /**
     * 车型详情
     */
    private String detailHtml;
    /**
     * 车型参数
     */
    private String parameterHtml;

    private Integer operateAdminUser;
}
