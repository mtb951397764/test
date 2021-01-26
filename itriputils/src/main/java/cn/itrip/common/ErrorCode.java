package cn.itrip.common;

public class ErrorCode {
    public final static String AUTH_UNKNOWN = "30000";
    public final static String AUTH_USER_ALREADY_EXISTS="30001";//用户已存在
    public final static String AUTH_AUTHENTICATION_FAILED="30002";//认证失败
    public final static String AUTH_PARAMETER_ERROR="30003";//用户名密码参数错误，为空
    public final static String AUTH_ACTIVATE_FAILED="30004";//邮件注册，激活失败
    public final static String AUTH_REPLACEMENT_FAILED="30005";//置换token失败
    public final static String AUTH_TOKEN_INVALID="30006";//token无效
    public final static String AUTH_ILLEGAL_USERCODE="30007";//非法的用户名
}
