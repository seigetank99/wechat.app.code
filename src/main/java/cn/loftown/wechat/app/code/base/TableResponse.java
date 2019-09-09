package cn.loftown.wechat.app.code.base;

import lombok.Data;

import java.util.List;

@Data
public class TableResponse<T> extends BaseResponse {
    private int total;
    private List<T> data;
}
