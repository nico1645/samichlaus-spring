package com.samichlaus.api.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  private final JavaMailSender mailSender;

  public MailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendMail(String to, String body, String subject, File file)
      throws MessagingException, MailException, UnsupportedEncodingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setFrom(
        new InternetAddress("mail@samichlaushergiswil.ch", "Samichlausvereinigung Hergiswil"));
    helper.setTo(new InternetAddress(to));
    helper.setSubject(subject);
    helper.setText(body, true);
    helper.addInline("samichlausicon", file);
    mailSender.send(message);
  }
}
