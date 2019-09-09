package cn.loftown.wechat.app.code.biz;

import cn.loftown.wechat.app.code.enums.FileTypeEnum;
import cn.loftown.wechat.app.code.exception.PredictException;
import cn.loftown.wechat.app.code.model.FileUploadRequest;
import cn.loftown.wechat.app.code.util.UnPackeUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUploadBll {
    public void handlerUpload(MultipartFile zipFile, FileUploadRequest request) {
        if (null == zipFile) {
            throw new PredictException("请上传压缩文件!");
        }
        boolean isZipPack = true;
        String fileContentType = zipFile.getContentType();
        //将压缩包保存在指定路径
        String packFilePath = FileUploadBll.class.getResource("") + request.getProduct() + "/" + request.getVersion();
        if (FileTypeEnum.FILE_TYPE_ZIP.type.equals(fileContentType)) {
            //zip解压缩处理
            packFilePath += FileTypeEnum.FILE_TYPE_ZIP.fileStufix;
        } else if (FileTypeEnum.FILE_TYPE_RAR.type.equals(fileContentType)) {
            //rar解压缩处理
            packFilePath += FileTypeEnum.FILE_TYPE_RAR.fileStufix;
            isZipPack = false;
        } else {
            throw new PredictException("上传的压缩包格式不正确,仅支持rar和zip压缩文件!");
        }
        File file = new File(packFilePath);
        try {
            zipFile.transferTo(file);
        } catch (IOException e) {
            throw new PredictException("保存压缩文件失败!");
        }
        if (isZipPack) {
            //zip压缩包
            UnPackeUtil.unPackZip(file, "", packFilePath);
        } else {
            //rar压缩包
            UnPackeUtil.unPackRar(file, packFilePath);
        }
        throw new PredictException("解压成功");
    }
}
