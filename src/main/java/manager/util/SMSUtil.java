package manager.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;

import manager.exception.LogicException;
import manager.system.SMError;

public abstract class SMSUtil {
	
	/**
	 * {1}为您的登录验证码，请于{2}分钟内填写，如非本人操作，请忽略本短信。
	 */
	public final static String SIGN_IN_TEMPLATE_ID = "765547";
	/**
	 * 您正在申请手机注册，验证码为：{1}，{2}分钟内有效！
	 */
	public final static String SIGN_UP_TEMPLATE_ID = "765545";
	
	/**
	 *  您在科管申请的账号为{1}，如非本人操作，请忽略本短信。
	 */
	public final static String RETRIEVE_ACCOUNT_TEMPLATE_ID = "791186";
	
	/**
	 * 您的动态验证码为：{1}，您正在进行密码重置操作，如非本人操作，请忽略本短信！
	 */
	public final static String RESET_PWD_TEMPLATE_ID = "790960";
	
	private final static String TX_SDK_ID = CommonUtil.getValFromPropertiesFileInResource("tx_sdk_id");
	private final static String TX_SECRET_ID = CommonUtil.getValFromPropertiesFileInResource("tx_secret_id");
	private final static String TX_SECRET_KEY = CommonUtil.getValFromPropertiesFileInResource("tx_secrect_key");

	public static void sendSMS(String templeteId,String tel,Object ...args) throws LogicException {
        try{
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            
            Credential cred = new Credential(TX_SECRET_ID,TX_SECRET_KEY);
            SmsClient client = new SmsClient(cred, "", clientProfile);

            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {"+86"+tel};
            req.setPhoneNumberSet(phoneNumberSet1);
            String sign = "王天戈的个人博客";
            req.setSign(sign);
            req.setTemplateID(templeteId);
            req.setTemplateParamSet(Arrays.stream(args).map(Object::toString).collect(Collectors.toList()).toArray(new String[0]));
            req.setSmsSdkAppid(TX_SDK_ID);
            client.SendSms(req);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            throw new LogicException(SMError.SEND_SMS_ERROR);
        }
	}

}
