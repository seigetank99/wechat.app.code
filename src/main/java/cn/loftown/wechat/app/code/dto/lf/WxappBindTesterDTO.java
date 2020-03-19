package cn.loftown.wechat.app.code.dto.lf;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class WxappBindTesterDTO {
    /**
     * 主键，自增
     */
    private Integer acid;
    /**
     * 对应小程序编号
     */
    private Integer wxAppAcid;

    private Integer uniacid;

    private String wxNumber;

    private String wxUserStr;

    private Integer status;

    private Timestamp createDate;
}
