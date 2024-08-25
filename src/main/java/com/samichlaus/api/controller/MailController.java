package com.samichlaus.api.controller;

import com.samichlaus.api.config.YAMLConfig;
import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.customer.CustomerRepository;
import com.samichlaus.api.domain.mail.MailDto;
import com.samichlaus.api.domain.mail.MailStatus;
import com.samichlaus.api.services.MailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
public class MailController {

  private final String STANDARD_BODY;

  private final DateTimeFormatter dateFormatter =
      DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN);
  private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
  private final File imgFile;
  private final MailService mailService;
  private final CustomerRepository customerRepository;

  public MailController(
      MailService mailService,
      CustomerRepository customerRepository,
      @Qualifier("config") YAMLConfig yamlConfig) {
    this.mailService = mailService;
    this.customerRepository = customerRepository;
    this.imgFile = new File(yamlConfig.getPathToSamichlausIcon());
    try {
      this.STANDARD_BODY = Files.readString(Paths.get(yamlConfig.getPathToSamichlausEmail()));
    } catch (IOException e) {
      throw new RuntimeException("Failed to read Email file from disk", e);
    }
  }

  @PostMapping("")
  public ResponseEntity<Customer> sendMail(@RequestBody @Valid MailDto mailDto) {
    String body =
        String.format(
            STANDARD_BODY,
            mailDto.getVisitDate().format(dateFormatter),
            mailDto.getVisitTime().format(timeFormatter));
    Customer customer =
        customerRepository
            .findByUUID(mailDto.getCustomerId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Customer with id " + mailDto.getCustomerId() + " does not exist."));
    try {
      mailService.sendMail(mailDto.getEmail(), body, "Samichlaus Besuchszeit", imgFile);
      customer.setMailStatus(MailStatus.SENT);
    } catch (MessagingException | UnsupportedEncodingException | MailException e) {
      System.out.println(e);
      customer.setMailStatus(MailStatus.FAILED);
    }
    customer = customerRepository.save(customer);
    return new ResponseEntity<>(customer, HttpStatus.OK);
  }

  @PostMapping("many")
  public ResponseEntity<List<Customer>> sendManyMails(@RequestBody @Valid List<MailDto> mailDtos) {
    List<Customer> customers = new ArrayList<>();
    for (MailDto mailDto : mailDtos) {
      String body =
          String.format(
              STANDARD_BODY,
              mailDto.getVisitDate().format(dateFormatter),
              mailDto.getVisitTime().format(timeFormatter));
      try {
        mailService.sendMail(mailDto.getEmail(), body, "Samichlaus Besuchszeit", imgFile);
        Optional<Customer> c = customerRepository.findByUUID(mailDto.getCustomerId());
        if (c.isPresent()) {
          Customer customer = c.get();
          customer.setMailStatus(MailStatus.SENT);
          customers.add(customer);
        } else
          throw new IllegalArgumentException(
              "Mail sent but Customer with id " + mailDto.getCustomerId() + " does not exist.");
      } catch (MessagingException | UnsupportedEncodingException | MailException e) {
        Optional<Customer> c = customerRepository.findByUUID(mailDto.getCustomerId());
        if (c.isPresent()) {
          Customer customer = c.get();
          customer.setMailStatus(MailStatus.FAILED);
          customers.add(customer);
        } else
          throw new IllegalArgumentException(
              "Customer with id " + mailDto.getCustomerId() + " does not exist.");
      }
    }
    customers = customerRepository.saveAll(customers);
    return new ResponseEntity<>(customers, HttpStatus.OK);
  }
}
