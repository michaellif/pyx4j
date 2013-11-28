/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 28, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.communication;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailAttachment;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

public class MailSendFacade {

    public MailDeliveryStatus sendMail(FeedEMail email) {
        MailMessage m = new MailMessage();
        if (email.getTo() == null || email.getTo().isEmpty()) {
            throw new IllegalArgumentException(); // TODO: I18N message
        }
        m.setTo(email.getTo());
        m.setSender(email.getSender() == null ? ServerSideConfiguration.instance().getApplicationEmailSender() : email.getSender());
        m.setSubject(email.getSubject());
        if (email.isHtmlbody()) {
            m.setHtmlBody(email.getBody());
        } else {
            m.setTextBody(email.getBody());
        }
        if (email.getAttachment() != null) {
            m.addAttachment(new MailAttachment(email.getAttachment().getName(), email.getAttachment().getContentType(), email.getAttachment().getAttachment()
                    .getBytes()));
        }
        return email.getMailServiceConfiguration() == null ? Mail.send(m) : Mail.send(m, email.getMailServiceConfiguration());
    }
}
