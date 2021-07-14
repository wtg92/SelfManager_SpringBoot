package manager.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import com.sun.mail.smtp.SMTPMessage;

import manager.exception.LogicException;
import manager.system.SMError;

public abstract class EmailUtil {
	
	private final static String SENDER_ADDRESS = CommonUtil.getValFromPropertiesFileInResource("email_sender_address");
	private final static String SENDER_ACCOUNT = CommonUtil.getValFromPropertiesFileInResource("email_sender_account");
	private final static String SENDER_PWD = CommonUtil.getValFromPropertiesFileInResource("email_sender_pwd");
	private final static boolean SHOW_DEBUG_MES = CommonUtil.getBoolValFromPropertiesFileInResource("show_email_debug_mes");
	private final static String SENDER_NAME = CommonUtil.getValFromPropertiesFileInResource("email_sender_name");
	private final static String EMAIL_SMTP_HOST = CommonUtil.getValFromPropertiesFileInResource("email_smtp_host");
	private final static String EMAIL_EMTP_PORT = CommonUtil.getValFromPropertiesFileInResource("email_smtp_http_port");
	
	private final static Properties SENDING_SMTP_CONFIG = getSendingSMTPConfig();

	
	public static void sendSimpleEmail(String recipient,String subject,String content) throws LogicException{
		try {
			Session session = Session.getInstance(SENDING_SMTP_CONFIG);
			session.setDebug(SHOW_DEBUG_MES);
			Message mes = createSMTPMessage(session, Arrays.asList(recipient), subject, content);
			try(Transport transport = session.getTransport()){
				transport.connect(SENDER_ACCOUNT, SENDER_PWD);
				transport.sendMessage(mes, mes.getAllRecipients());
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new LogicException(SMError.SEND_EMAIL_ERROR);
		}
	}

	private static Properties getSendingSMTPConfig() {
		Properties props = new Properties();
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", EMAIL_SMTP_HOST);
		props.setProperty("mail.smtp.port", EMAIL_EMTP_PORT);
		
	    props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    props.setProperty("mail.smtp.socketFactory.fallback", "false");
	    props.setProperty("mail.smtp.socketFactory.port", EMAIL_EMTP_PORT);
	    props.setProperty("mail.smtp.ssl.enable", "true");
		return props;
	}
	
	/*TODO recipent 非法问题 需要处理*/
	private static SMTPMessage createSMTPMessage(Session session,List<String> recipients,String subject,String mesInfo) throws MessagingException {
		SMTPMessage mes =  new SMTPMessage(session);
		List<Address> addresses = new ArrayList<>();
		for(int i=0;i<recipients.size();i++) {
			addresses.add(new InternetAddress(recipients.get(i)));
		}
		mes.setRecipients(Message.RecipientType.TO,addresses.toArray(new Address[0]));
		mes.setFrom(SENDER_ADDRESS);
		
		mes.setSubject(subject);
		mes.setContent(mesInfo, "text/html;charset=UTF-8");
		mes.setSentDate(new Date());
		return mes;
	}
	
}
