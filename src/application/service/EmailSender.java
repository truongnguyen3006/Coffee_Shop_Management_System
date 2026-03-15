package application.service;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import application.util.AppLogger;

public class EmailSender {
	private static final Logger LOGGER = AppLogger.getLogger(EmailSender.class);
	private static final String FROM_EMAIL = AppConfig.get("mail.username");
	private static final String PASSWORD = AppConfig.get("mail.password");
	private static final String SMTP_HOST = AppConfig.get("mail.host");
	private static final String SMTP_PORT = AppConfig.get("mail.port");

	private static Session createSession() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", SMTP_HOST);
		props.put("mail.smtp.port", SMTP_PORT);

		return Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
			}
		});
	}

	public static void sendOTPEmail(String toEmail, String otp) throws MessagingException {
		Session session = createSession();
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(FROM_EMAIL));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
		message.setSubject("Mã OTP xác thực");
		message.setText("Mã OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 60s.");
		try {
			Transport.send(message);
		} catch (MessagingException e) {
			AppLogger.error(LOGGER, "Gửi OTP thất bại tới email: " + toEmail, e);
			throw e;
		}
	}

	public static void sendOrderConfirmation(String toEmail, String orderDetails) throws MessagingException {
		Session session = createSession();
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(FROM_EMAIL));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
		message.setSubject("Thông tin đơn hàng của bạn");
		message.setText(orderDetails);
		try {
			Transport.send(message);
		} catch (MessagingException e) {
			AppLogger.error(LOGGER, "Gửi email xác nhận thất bại tới email: " + toEmail, e);
			throw e;
		}
	}
}
