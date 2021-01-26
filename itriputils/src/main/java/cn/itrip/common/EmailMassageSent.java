package cn.itrip.common;


import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Properties;

public class EmailMassageSent {
    private static Logger log = Logger.getLogger(EmailMassageSent.class);
    public static void sendHtmlMail() {
        try{
            String from = "爱旅行";//发件人昵称展示   *
            String[] to ={"951397764@qq.com","接收邮箱2"};//接收邮箱
            String subject = "爱旅行注册验证";//邮件主题   *
            String text = "欢迎您注册爱旅行，您的验证码为，有效时常为5分钟";
            String host = "smtphz.qiye.163.com";//163企业邮箱smtp   *
            String username = "发件的企业邮箱";//企业邮箱   *
            String password= "发件的企业邮箱的授权码";//企业邮箱密码   *

                         //设置服务器验证信息
            Properties prop = new Properties();
            prop.setProperty("mail.smtp.auth", "true");
            prop.setProperty("mail.smtp.timeout", "994"); // 加密端口(ssl)  可通过 https://qiye.163.com/help/client-profile.html 进行查询

            MailSSLSocketFactory sf = new MailSSLSocketFactory();// SSL加密
            sf.setTrustAllHosts(true); // 设置信任所有的主机
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory", sf);

                         //设置邮件内容
                        JavaMailSenderImpl javaMailSend = new JavaMailSenderImpl();
                        MimeMessage message = javaMailSend.createMimeMessage();
                         MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");
                         String nick = MimeUtility.encodeText(from);//设置昵称
                         messageHelper.setFrom(new InternetAddress(nick + " <"+username+">"));// 邮件发送者
                         messageHelper.setTo(to);
                         messageHelper.setSubject(subject);
                         messageHelper.setText(text, true);

                         //设置邮件服务器登录信息
                         javaMailSend.setHost(host);
                         javaMailSend.setUsername(username);
                         javaMailSend.setPassword(password);
                         javaMailSend.setJavaMailProperties(prop);
                         log.info("maillText：" + text);
                         javaMailSend.send(message);
            } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(e);
            e.printStackTrace();
            }
        }
}
