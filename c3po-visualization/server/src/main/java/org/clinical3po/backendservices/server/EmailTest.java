package org.clinical3po.backendservices.server;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by w.ding on 23/05/15.
 */
public class EmailTest {
    public static void main(String[] args) {

        final String username = "postmaster@clinical3po.org";
        final String password = "sxwt2ysc";

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "mail.clinical3po.org");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("postmaster@clinical3po.org"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("vvei.ding@gmail.com"));
            message.setSubject("Testing Subject");
            message.setContent("Hi,<br>Thanks for registering with us.<br>Please use the following <a href='http://www.clinical3po.org/api/rs?cmd={\"readOnly\":true,\"category\":\"user\",\"name\":\"activateUser\"}'>link</a> to activate your account.", "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
