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
import java.io.UnsupportedEncodingException;
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

  private final String STANDARD_BODY =
      """
                <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
                <html data-editor-version="2" class="sg-campaigns" xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                      <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">
                      <!--[if !mso]><!-->
                      <meta http-equiv="X-UA-Compatible" content="IE=Edge">
                      <!--<![endif]-->
                      <!--[if (gte mso 9)|(IE)]>
                      <xml>
                        <o:OfficeDocumentSettings>
                          <o:AllowPNG/>
                          <o:PixelsPerInch>96</o:PixelsPerInch>
                        </o:OfficeDocumentSettings>
                      </xml>
                      <![endif]-->
                      <!--[if (gte mso 9)|(IE)]>
                  <style type="text/css">
                    body {width: 600px;margin: 0 auto;}
                    table {border-collapse: collapse;}
                    table, td {mso-table-lspace: 0pt;mso-table-rspace: 0pt;}
                    img {-ms-interpolation-mode: bicubic;}
                  </style>
                <![endif]-->
                      <style type="text/css">
                    body, p, div {
                      font-family: arial,helvetica,sans-serif;
                      font-size: 14px;
                    }
                    body {
                      color: #000000;
                    }
                    body a {
                      color: #1188E6;
                      text-decoration: none;
                    }
                    p { margin: 0; padding: 0; }
                    table.wrapper {
                      width:100%% !important;
                      table-layout: fixed;
                      -webkit-font-smoothing: antialiased;
                      -webkit-text-size-adjust: 100%%;
                      -moz-text-size-adjust: 100%%;
                      -ms-text-size-adjust: 100%%;
                    }
                    img.max-width {
                      max-width: 100%% !important;
                    }
                    .column.of-2 {
                      width: 50%%;
                    }
                    .column.of-3 {
                      width: 33.333%%;
                    }
                    .column.of-4 {
                      width: 25%%;
                    }
                    ul ul ul ul  {
                      list-style-type: disc !important;
                    }
                    ol ol {
                      list-style-type: lower-roman !important;
                    }
                    ol ol ol {
                      list-style-type: lower-latin !important;
                    }
                    ol ol ol ol {
                      list-style-type: decimal !important;
                    }
                    @media screen and (max-width:480px) {
                      .preheader .rightColumnContent,
                      .footer .rightColumnContent {
                        text-align: left !important;
                      }
                      .preheader .rightColumnContent div,
                      .preheader .rightColumnContent span,
                      .footer .rightColumnContent div,
                      .footer .rightColumnContent span {
                        text-align: left !important;
                      }
                      .preheader .rightColumnContent,
                      .preheader .leftColumnContent {
                        font-size: 80%% !important;
                        padding: 5px 0;
                      }
                      table.wrapper-mobile {
                        width: 100%% !important;
                        table-layout: fixed;
                      }
                      img.max-width {
                        height: auto !important;
                        max-width: 100%% !important;
                      }
                      a.bulletproof-button {
                        display: block !important;
                        width: auto !important;
                        font-size: 80%%;
                        padding-left: 0 !important;
                        padding-right: 0 !important;
                      }
                      .columns {
                        width: 100%% !important;
                      }
                      .column {
                        display: block !important;
                        width: 100%% !important;
                        padding-left: 0 !important;
                        padding-right: 0 !important;
                        margin-left: 0 !important;
                        margin-right: 0 !important;
                      }
                      .social-icon-column {
                        display: inline-block !important;
                      }
                    }
                  </style>
                      <!--user entered Head Start--><!--End Head user entered-->
                    </head>
                    <body>
                      <center class="wrapper" data-link-color="#1188E6" data-body-style="font-size:14px; font-family:arial,helvetica,sans-serif; color:#000000; background-color:#FFFFFF;">
                        <div class="webkit">
                          <table cellpadding="0" cellspacing="0" border="0" width="100%%" class="wrapper" bgcolor="#FFFFFF">
                            <tr>
                              <td valign="top" bgcolor="#FFFFFF" width="100%%">
                                <table width="100%%" role="content-container" class="outer" align="center" cellpadding="0" cellspacing="0" border="0">
                                  <tr>
                                    <td width="100%%">
                                      <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                        <tr>
                                          <td>
                                            <!--[if mso]>
                    <center>
                    <table><tr><td width="600">
                  <![endif]-->
                                                    <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="width:100%%; max-width:600px;" align="center">
                                                      <tr>
                                                        <td role="modules-container" style="padding:0px 0px 0px 0px; color:#000000; text-align:left;" bgcolor="#FFFFFF" width="100%%" align="left"><table class="module preheader preheader-hide" role="module" data-type="preheader" border="0" cellpadding="0" cellspacing="0" width="100%%" style="display: none !important; mso-hide: all; visibility: hidden; opacity: 0; color: transparent; height: 0; width: 0;">
                    <tr>
                      <td role="module-content">
                        <p></p>
                      </td>
                    </tr>
                  </table><table class="wrapper" role="module" data-type="image" border="0" cellpadding="0" cellspacing="0" width="100%%" style="table-layout: fixed;" data-muid="f0e5d558-205f-481f-96b1-00da6ebc90ec">
                    <tbody>
                      <tr>
                        <td style="font-size:6px; line-height:10px; padding:0px 0px 0px 0px;" valign="top" align="center">
                        <a href="https://www.samichlaushergiswil.ch"><img class="max-width" border="0" style="display:block; color:#000000; text-decoration:none; font-family:Helvetica, arial, sans-serif; font-size:16px; max-width:40%% !important; width:40%%; height:auto !important;" width="240" alt="" data-proportionally-constrained="true" data-responsive="true" src="cid:samichlausicon"></a></td>
                      </tr>
                    </tbody>
                  </table><table class="module" role="module" data-type="text" border="0" cellpadding="0" cellspacing="0" width="100%%" style="table-layout: fixed;" data-muid="95c94080-08e1-4e38-b020-ea3ff5291148" data-mc-module-version="2019-10-22">
                    <tbody>
                      <tr>
                        <td style="padding:18px 0px 18px 0px; line-height:40px; text-align:inherit;" height="100%%" valign="top" bgcolor="" role="module-content"><div><h1 style="text-align: center"><span style="font-size: 24px">Deine persönliche Samichlaus Besuchszeit!</span></h1>
                <div style="font-family: inherit; text-align: center"><span style="font-size: 12px">Der Samichlaus besucht dich am </span><span style="font-size: 12px"><strong>%s um ca. %s Uhr</strong></span><span style="font-size: 12px">.</span></div>
                <div style="font-family: inherit; text-align: center"><br></div>
                <div style="font-family: inherit; text-align: center"><span style="font-size: 12px">Für allfällige weitere Infos melde dich bei Florian Durrer, +41 79 616 17 80</span></div>
                <div style="font-family: inherit; text-align: inherit"><br></div>
                <div style="font-family: inherit; text-align: center"><span style="font-size: 12px">Wir freuen uns auf eine schöne Samichlauszyyt.</span></div>
                <div style="font-family: inherit; text-align: center"><span style="font-size: 12px"><strong>Samichlausvereinigung Hergiswil</strong></span></div><div></div></div></td>
                      </tr>
                    </tbody>
                  </table></td>
                                                      </tr>
                                                    </table>
                                                    <!--[if mso]>
                                                  </td>
                                                </tr>
                                              </table>
                                            </center>
                                            <![endif]-->
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </div>
                      </center>
                  </html>
                """;

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
