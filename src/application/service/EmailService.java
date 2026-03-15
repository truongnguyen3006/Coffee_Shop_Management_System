package application.service;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.util.logging.Logger;

import javax.mail.MessagingException;

import application.service.EmailSender;
import application.util.AppLogger;

public class EmailService {
    private static final Logger LOGGER = AppLogger.getLogger(EmailService.class);

    public void sendOtpEmail(String email, String otp) throws MessagingException {
        try {
            EmailSender.sendOTPEmail(email, otp);
        } catch (MessagingException ex) {
            AppLogger.error(LOGGER, "Gửi OTP thất bại tới email: " + email, ex);
            throw ex;
        }
    }

    public void sendOrderConfirmation(String email, String content) throws MessagingException {
        try {
            EmailSender.sendOrderConfirmation(email, content);
        } catch (MessagingException ex) {
            AppLogger.error(LOGGER, "Gửi email xác nhận đơn hàng thất bại tới email: " + email, ex);
            throw ex;
        }
    }
}
