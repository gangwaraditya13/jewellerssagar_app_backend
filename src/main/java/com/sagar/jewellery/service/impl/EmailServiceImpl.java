package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.time.Year;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request - Sagar Jewellery");

            String htmlMsg = "<div style='font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #eaeaea; padding: 30px; border-radius: 12px; background-color: #ffffff;'>"
                    + "<div style='text-align: center; margin-bottom: 25px;'>"
                    + "  <h1 style='color: #d4af37; margin: 0; font-size: 28px; font-weight: 300; letter-spacing: 1px;'>Sagar Jewellery</h1>"
                    + "  <div style='height: 2px; background: linear-gradient(to right, transparent, #d4af37, transparent); margin-top: 15px;'></div>"
                    + "</div>"
                    + "<p style='font-size: 16px; color: #444; line-height: 1.6;'>Dear Customer,</p>"
                    + "<p style='font-size: 16px; color: #444; line-height: 1.6;'>We received a request to reset the password for your Sagar Jewellery account.</p>"
                    + "<p style='font-size: 16px; color: #444; line-height: 1.6;'>Your secure One-Time Password (OTP) is:</p>"
                    + "<div style='text-align: center; margin: 35px 0;'>"
                    + "  <span style='font-size: 36px; font-weight: 700; letter-spacing: 8px; color: #333; background-color: #fcfbf7; padding: 20px 40px; border-radius: 8px; border: 1px solid #d4af37; display: inline-block; box-shadow: 0 4px 6px rgba(212, 175, 55, 0.1);'>" + otp + "</span>"
                    + "</div>"
                    + "<p style='font-size: 15px; color: #666; line-height: 1.6;'>This OTP is valid for <strong>5 minutes</strong>. For your protection, please do not share this code with anyone.</p>"
                    + "<hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;' />"
                    + "<p style='font-size: 13px; color: #999; text-align: center; line-height: 1.5;'>If you did not request a password reset, please safely ignore this email.</p>"
                    + "<p style='font-size: 13px; color: #aaa; text-align: center; margin-top: 20px;'>&copy; " + Year.now().getValue() + " Sagar Jewellery. All rights reserved.</p>"
                    + "</div>";

            String plainTextMsg = "Dear Customer,\n\n"
                    + "We received a request to reset the password for your Sagar Jewellery account.\n\n"
                    + "Your secure One-Time Password (OTP) is: " + otp + "\n\n"
                    + "This OTP is valid for 5 minutes. For your protection, please do not share this code with anyone.\n\n"
                    + "If you did not request a password reset, please safely ignore this email.\n\n"
                    + "Sagar Jewellery";

            helper.setText(plainTextMsg, htmlMsg);

            helper.setReplyTo("jewellerssagar34@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send professional OTP email", e);
        }
    }
}
