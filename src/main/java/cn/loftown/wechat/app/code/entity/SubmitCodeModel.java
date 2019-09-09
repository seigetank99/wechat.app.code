package cn.loftown.wechat.app.code.entity;

import lombok.Data;

@Data
public class SubmitCodeModel {
    private String firstCategory;
    private String page;
    private String secondCategory;
    private String tag;
    private String thirdCategory;
    private String title;
    private String firstCategoryName;
    private String secondCategoryName;
    private String thirdCategoryName;
}
