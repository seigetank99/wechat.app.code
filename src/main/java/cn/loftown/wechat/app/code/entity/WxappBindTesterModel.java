package cn.loftown.wechat.app.code.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

@Data
public class WxappBindTesterModel {
    /**
     * 主键，自增
     */
    private Integer acid;

    private String wxNumber;

    private String wxUserStr;

    /**
     * 对应小程序编号
     */
    private Integer wxAppAcid;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    private Timestamp createDate;
}
