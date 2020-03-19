package cn.loftown.wechat.app.code.dto;

import lombok.Data;

@Data
public class MemberMessageTemplateDTO {
    private Integer id;

    private Integer uniacid;

    private String title;

    private String template_id;

    private String template_name;

    private String firstcolor;

    private String remarkcolor;

    private String url;

    private Integer createtime;

    private Integer sendtimes;

    private Integer sendcount;

    private String typecode;

    private Boolean messagetype;

    private String send_desc;

    private String first;

    private String data;

    private String remark;
}
