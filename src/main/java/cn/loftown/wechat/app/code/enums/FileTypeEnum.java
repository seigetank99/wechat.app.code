package cn.loftown.wechat.app.code.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum FileTypeEnum {
    FILE_TYPE_ZIP("application/zip", ".zip"),
    FILE_TYPE_RAR("application/octet-stream", ".rar");
    public String type;
    public String fileStufix;

    public static String getFileStufix(String type) {
        for (FileTypeEnum fileTypeEnum : FileTypeEnum.values()) {
            if (fileTypeEnum.type.equals(type)) {
                return fileTypeEnum.fileStufix;
            }
        }
        return null;
    }
}
