package cn.itrip.common;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

public class EmailTestUtil {
    public static void sentMessage(String emailAddr,String code,String time)
    {
        MultiPartEmail email = new HtmlEmail();
        email.setHostName("smtp.163.com");
        email.setCharset("utf-8");
        try {
            email.addTo(emailAddr);
            email.setFrom("mtb95139@163.com","爱旅行");
            email.setAuthentication("mtb95139@163.com","LECOGDYYBJUJJBST");
            email.setSubject("注册认证");
            email.setMsg("欢迎您注册爱旅行，您的验证码为"+code+"，请在"+time+"分钟内完成验证");
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        EmailTestUtil.sentMessage("951397764@qq.com",""+MD5.getRandomCode(),"5");
    }
}
