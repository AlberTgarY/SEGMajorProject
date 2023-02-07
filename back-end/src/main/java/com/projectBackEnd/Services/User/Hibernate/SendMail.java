package main.java.com.projectBackEnd.Services.User.Hibernate;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.sun.mail.util.MailSSLSocketFactory;

/**
 * This is the SendMail class.
 * It will send a email to the end user`s address while signing up.
 */
class SendMail

{
    private static String sender = "476070991@qq.com";
    private static String authentication = "bxnxbljhsskqcaji";
    private static String propertyHost = "mail.smtp.host";
    private static String propertyMailServer = "smtp.qq.com";
    private static String propertyAuth = "mail.smtp.auth";

    /**
     * This method will send a email to a specific email account.
     * The addresser account is my personal account.
     * Only in this way I can get authentication code.
     * @param to The email to send the message to
     * @param title The subject of email
     * @param content The content of email
     * @return The success of the call
     */
     static boolean send(String to, String title, String content) {
        try {
            sendMessage(to, title, content, getDefaultSession());
        }
        catch (MessagingException | UnsupportedEncodingException mex) { return false; }
        return true;
    }

    /**
     * Gets a default session set up using the sender and authentication
     * @return The session created in this way
     */
    private static Session getDefaultSession() {
        return Session.getDefaultInstance(getProperties(), new Authenticator()
        {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, authentication);
            }
        });
    }

    /**
     * Sends a message
     * @param to The email to send the message to
     * @param title The subject of the email
     * @param content The content of the email
     * @param session The session for authorization
     * @throws MessagingException Error thrown with messages
     * @throws UnsupportedEncodingException Errors thrown with bad Encodings
     */
    private static void sendMessage(String to, String title, String content, Session session) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender, "Team Team", "UTF-8"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(title, "UTF-8");
        message.setContent(content, "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        Transport.send(message);
    }

    /**
     * Get system properties to set up the host using a simple mail transfer protocol
     * @return The set up properties to this account
     */
    private static Properties getProperties() {
        Properties properties = System.getProperties();
        properties.setProperty(propertyHost, propertyMailServer);
        properties.put(propertyAuth, "true");
        MailSSLSocketFactory MailSocket;
        try {
            MailSocket = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) { return null;}
        MailSocket.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", MailSocket);
        return properties;
    }

}
