package com.unilab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendBookingRejectionEmail(String recipientEmail, String title,
                                          java.time.LocalDateTime startTime, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Your Lab Booking Has Been Rejected");

        String formattedDate = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        message.setText(String.format(
                "Dear user,\n\n" +
                        "Your booking \"%s\" scheduled for %s has been rejected.\n\n" +
                        "Reason: %s\n\n" +
                        "Best regards,\n" +
                        "UniLab Team",
                title, formattedDate, reason
        ));

        mailSender.send(message);
        System.out.println("Rejection email sent to: " + recipientEmail);
    }
}
