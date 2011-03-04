/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.mail;

import static com.pyx4j.server.mail.SMTPMailUtils.email;
import static com.pyx4j.server.mail.SMTPMailUtils.emails;
import static com.pyx4j.server.mail.SMTPMailUtils.filterDestinations;
import static com.pyx4j.server.mail.SMTPMailUtils.isEmptyList;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;

class SMTPMailServiceImpl implements IMailService {

    private final static Logger log = LoggerFactory.getLogger(SMTPMailServiceImpl.class);

    @Override
    public MailDeliveryStatus send(MailMessage mailMessage) {

        Properties mailProperties = new Properties();

        IMailServiceConfigConfiguration mailConfig = ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
        if ((mailConfig == null) || (!(mailConfig instanceof SMTPMailServiceConfig))) {
            log.error("E-mail delivery SMTP not configured");
            return MailDeliveryStatus.ConfigurationError;
        }
        SMTPMailServiceConfig config = (SMTPMailServiceConfig) mailConfig;

        mailProperties.put("mail.smtp.host", config.getHost());
        mailProperties.put("mail.smtp.port", String.valueOf(config.getPort()));

        // Enable SSL connection
        if (config.isStarttls()) {
            mailProperties.setProperty("mail.smtp.starttls.enable", "true");
            mailProperties.setProperty("mail.smtp.socketFactory.port", String.valueOf(config.getPort()));
            mailProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            mailProperties.setProperty("mail.smtp.socketFactory.fallback", "false");
            mailProperties.setProperty("mail.smtp.ssl", "true");
        }

        Authenticator authenticator = null;
        if (CommonsStringUtils.isStringSet(config.getUser())) {
            authenticator = new SMTPAuthenticatorImpl(config.getUser(), config.getPassword());
            mailProperties.setProperty("mail.smtp.auth", "true");
        }
        Session mailSession = Session.getInstance(mailProperties, authenticator);
        mailSession.setDebug(config.isDebug());

        MimeMessage message = new MimeMessage(mailSession);

        try {
            message.setFrom(email(mailMessage.getSender()));
        } catch (MessagingException e) {
            log.error("Error", e);
            return MailDeliveryStatus.MessageDataError;
        }

        try {
            List<InternetAddress> address;
            List<InternetAddress> recipientsCc;
            List<InternetAddress> recipientsBcc;

            String emailFilter = config.getAllowSendToEmailSufix();
            if (!CommonsStringUtils.isStringSet(emailFilter)) {
                address = emails(mailMessage.getTo());
                recipientsCc = emails(mailMessage.getCc());
                recipientsBcc = emails(mailMessage.getBcc());
            } else {
                address = filterDestinations(emailFilter, emails(mailMessage.getTo()));
                recipientsCc = filterDestinations(emailFilter, emails(mailMessage.getCc()));
                recipientsBcc = filterDestinations(emailFilter, emails(mailMessage.getBcc()));
            }

            if (isEmptyList(address) && SMTPMailUtils.isEmptyList(recipientsCc) && isEmptyList(recipientsBcc)) {
                log.error("No destination E-Mail addreses found");
                return MailDeliveryStatus.MessageDataError;
            }

            if (!isEmptyList(address)) {
                message.setRecipients(Message.RecipientType.TO, address.toArray(new InternetAddress[address.size()]));
            }
            if (!isEmptyList(recipientsCc)) {
                message.setRecipients(Message.RecipientType.CC, recipientsCc.toArray(new InternetAddress[recipientsCc.size()]));
            }
            if (!isEmptyList(recipientsBcc)) {
                message.setRecipients(Message.RecipientType.BCC, recipientsBcc.toArray(new InternetAddress[recipientsBcc.size()]));
            }

            message.setSubject(mailMessage.getSubject());
            message.setSentDate(new Date());
            message.addHeader("MIME-Version", "1.0");
            if (mailMessage.getReplyTo() != null) {
                //TODO
                // message.setReplyTo(email(mailMessage.getReplyTo()));
            }
            for (Map.Entry<String, String> me : mailMessage.getHeaders()) {
                message.addHeader(me.getKey(), me.getValue());
            }

            Multipart content;
            if (mailMessage.getHtmlBody() == null) {
                // Pain text
                // create and fill the first message part
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setText(mailMessage.getTextBody());

                // Create the Multipart mime and attach the body parts
                content = new MimeMultipart();
                content.addBodyPart(bodyPart);
                message.setContent(content);
            } else {
                content = new MimeMultipart("alternative");
                message.setHeader("Content-Type", content.getContentType());

                if (!CommonsStringUtils.isEmpty(mailMessage.getTextBody())) {
                    MimeBodyPart textPart = new MimeBodyPart();
                    textPart.setText(mailMessage.getTextBody());
                    textPart.setHeader("MIME-Version", "1.0");
                    textPart.setHeader("Content-Type", "text/plain; charset=\"utf-8\"");
                    content.addBodyPart(textPart);
                }

                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(mailMessage.getHtmlBody(), "text/html");
                htmlPart.setHeader("MIME-Version", "1.0");
                htmlPart.setHeader("Content-Type", "text/html; charset=\"utf-8\"");

                content.addBodyPart(htmlPart);
                message.setContent(content);
            }

            //            if (mailMessage.getAttachments() != null) {
            //                for (DataSource attachment : mailMessage.getAttachments()) {
            //                    MimeBodyPart bodyPart = new MimeBodyPart();
            //                    // attach the file to the message
            //                    bodyPart.setDataHandler(new DataHandler(attachment));
            //                    bodyPart.setDisposition(javax.mail.Part.ATTACHMENT);
            //                    bodyPart.setFileName(attachment.getName());
            //                    content.addBodyPart(bodyPart);
            //                }
            //            }

        } catch (MessagingException e) {
            log.error("Error", e);
            return MailDeliveryStatus.MessageDataError;
        }

        Transport transport;
        try {
            transport = mailSession.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            log.error("SMTP Mail configuration error", e);
            return MailDeliveryStatus.ConfigurationError;
        }
        try {
            transport.connect();
        } catch (MessagingException e) {
            log.error("Error", e);
            return MailDeliveryStatus.ConnectionError;
        }

        try {
            Address[] allRecipients = message.getAllRecipients();
            transport.sendMessage(message, allRecipients);

            StringBuffer sendTo = new StringBuffer();
            for (Address a : allRecipients) {
                if (sendTo.length() > 0) {
                    sendTo.append(", ");
                }
                sendTo.append(a.toString());
            }
            log.info("mail sent to '{}'", sendTo);
            return MailDeliveryStatus.Success;
        } catch (MessagingException e) {
            log.error("Error", e);
            return MailDeliveryStatus.MessageDataError;
        } finally {
            try {
                transport.close();
            } catch (MessagingException ignore) {
            }
        }
    }

}
