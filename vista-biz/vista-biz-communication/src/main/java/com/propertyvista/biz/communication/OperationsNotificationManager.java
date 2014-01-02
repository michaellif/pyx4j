/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 2, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.server.mail.MessageTemplate;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class OperationsNotificationManager {

    private final static I18n i18n = I18n.get(OperationsNotificationManager.class);

    private static String wrapperTextResourceName = "email/notification/template-body-operations.html";

    private static String getSender() {
        return ServerSideConfiguration.instance().getApplicationEmailSender();
    }

    public static MailMessage createOperationsPasswordResetEmail(AbstractUser user, String token) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        email.setTo(user.email().getValue());
        email.setSubject(i18n.tr("Vista Operations Password Retrieval"));

        MessageTemplate template = new MessageTemplate();
        template.setBodyTemplate("Dear ${name},<br/>\n"
                + "This email was sent to you in response to your request to modify your Property Vista Support Administration account password.<br/>\n"
                + "Click the link below to go to the Property Vista Operations site and create new password for your account:<br/>\n"
                + "    <a href=\"${link}\">Change Your Password</a>");

        template.variable("${name}", user.name());
        template.variable("${link}", AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.operations, true), true,
                OperationsSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, token));

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createInvalidDirectDebitReceivedEmail(DirectDebitRecord paymentRecord) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        email.setTo("leonard@propertyvista.com, support@propertyvista.com");
        email.setSubject(i18n.tr("Invalid Direct Debit Received"));

        MessageTemplate template = new MessageTemplate();
        template.setBodyTemplate("Invalid Direct Debit received. See <a href=\"${link}\">Payment Record details</a>");

        template.variable("${link}", AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.operations, true), true,
                new OperationsSiteMap.FundsTransfer.DirectDebitRecord().formViewerPlace(paymentRecord.getPrimaryKey())));

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }
}
