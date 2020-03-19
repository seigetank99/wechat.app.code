package cn.loftown.wechat.app.code.model;

import cn.loftown.wechat.app.code.entity.SubmitCodeModel;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.List;

@Data
public class SubmitCodeRequest {
    private Integer acid;
    private Integer wxAppAcId;
    private String wxAppName;
    private String feedbackInfo;
    private String feedbackStuff;
    private List<SubmitCodeModel> submitDataList;
    private JSONArray jsonArray;
}
