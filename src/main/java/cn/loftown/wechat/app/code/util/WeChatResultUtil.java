package cn.loftown.wechat.app.code.util;

public class WeChatResultUtil {
    public static String TransformCodeToMsg(int code){
        switch (code){
            case -1:
                return "系统繁忙";
            case 0:
                return "操作成功";
            case 86000:
                return "不是由第三方代小程序进行调用";
            case 86001:
                return "不存在第三方的已经提交的代码";
            case 85006:
                return "标签格式错误";
            case 85007:
                return "页面路径错误";
            case 85008:
                return "类目填写错误";
            case 85009:
                return "已经有正在审核的版本";
            case 85010:
                return "审核列表有项目为空";
            case 85011:
                return "标题填写错误";
            case 85023:
                return "审核列表填写的项目数不在1-5以内";
            case 85077:
                return "类目信息失效";
            case 86002:
                return "还未设置昵称、头像、简介。请先设置完后再重新提交";
            case 85085:
                return "近7天提交审核的小程序数量过多，请耐心等待审核完毕后再次提交";
            case 85086:
                return "提交代码审核之前需提前上传代码";
            case 85087:
                return "已使用api navigateToMiniProgram，请声明跳转appid列表后再次提交";
            case 85012:
                return "无效的审核id";
            case 85019:
                return "没有审核版本";
            case 85020:
                return "审核状态未满足发布";
            case 85021:
                return "状态不可变";
            case 85022:
                return "操作非法";
            case 87011:
                return "现网已经在灰度发布，不能进行版本回退";
            case 87012:
                return "该版本不能回退，可能的原因：1:无上一个线上版用于回退 2:此版本为已回退版本，不能回退 3:此版本为回退功能上线之前的版本，不能回退";
            case 85013:
                return "无效的自定义配置";
            case 85014:
                return "无效的模版编号";
            case 85043:
                return "模版错误";
            case 85044:
                return "代码包超过大小限制";
            case 85045:
                return "ext_json有不存在的路径";
            case 85046:
                return "tabBar中缺少path";
            case 85047:
                return "pages字段为空";
            case 85048:
                return "ext_json解析失败";
            case 80082:
                return "没有权限使用该插件";
            case 80067:
                return "找不到使用的插件";
            case 80066:
                return "非法的插件版本";
            case 85064:
                return "找不到模版";
            case 85065:
                return "模版库已满";
            default:
                return "操作失败";
        }
    }
}
