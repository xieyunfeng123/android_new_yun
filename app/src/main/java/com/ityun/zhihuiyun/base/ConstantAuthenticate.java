package com.ityun.zhihuiyun.base;

public class ConstantAuthenticate
{
    public  static final  int WM_Authenticate_SvrMsgId_DisConnect=1;  //断线通知
    public  static final  int WM_Authenticate_SvrMsgId_ReConnect=2;   //重连通知
    public  static final  int WM_Authenticate_SvrMsgId_KickOut=3;     //被服务器踢通知, 
    public  static final  int WM_Authenticate_SvrMsgId_UpdateSignature=4;  //更新签名


    public static final int
            WM_Authenticate_Success=0,
            WM_Authenticate_ErrorCode_Fail = 10001,							//未知错误
            WM_Authenticate_ErrorCode_WaitResult = 10002,					//异步等待结果
            WM_Authenticate_ErrorCode_InvalidParameter = 10003,				//无效参数
            WM_Authenticate_ErrorCode_SdkInitFail = 10004,					//Sdk初始化失败
            WM_Authenticate_ErrorCode_HasInit = 10005,						//Sdk已经初始化
            WM_Authenticate_ErrorCode_NoInit = 10006,						//Sdk未初始化
            WM_Authenticate_ErrorCode_ConnectFail = 10007,					//连接失败
            WM_Authenticate_ErrorCode_ResponseTimeout = 10008,				//超时
            WM_Authenticate_ErrorCode_HasLogin = 10009,						//已经登录
            WM_Authenticate_ErrorCode_NoLogin = 10010,						//没有登录
            WM_Authenticate_ErrorCode_NoExistUserName = 10011,				//用户名不存在
            WM_Authenticate_ErrorCode_ErrorPassword = 10012,				//密码错误
            WM_Authenticate_ErrorCode_ServerReject = 10013,					//服务器拒绝
            WM_Authenticate_ErrorCode_InvalidSignature = 10014,				//无效签名
            WM_Authenticate_ErrorCode_NoVerifyCode = 10015,					//验证码无效
            WM_Authenticate_ErrorCode_VerifCodeError = 10016,				//验证码错误
            WM_Authenticate_ErrorCode_InvalidVerifyCode = 10017;	//验证码过期

}
