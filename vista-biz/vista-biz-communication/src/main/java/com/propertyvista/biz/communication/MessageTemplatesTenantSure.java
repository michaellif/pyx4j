/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.server.mail.MessageTemplate;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;

class MessageTemplatesTenantSure {

    private final static I18n i18n = I18n.get(MessageTemplatesTenantSure.class);

    private static String wrapperTextResourceName = "email/tenantsure/template-tenantsure.html";

    private static String getTenantSureSender() {
        return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureEmailSender();
    }

    public static MailMessage createTenantSurePaymentNotProcessedEmail(LogicalDate gracePeriodEndDate, LogicalDate cancellationDate) {
        MailMessage email = new MailMessage();
        email.setSender(getTenantSureSender());
        email.setSubject(i18n.tr("15 Day Notice of Cancellation for Non-payment of Premium"));

        MessageTemplate template = new MessageTemplate("email/tenantsure/tenantsure-payment-not-processed.html");

        DateFormat dateFormat = new SimpleDateFormat(i18n.tr("yyyy-MM-dd"));

        template.variable("${cancellationDate}", dateFormat.format(cancellationDate));
        template.variable("${gracePeriodEndDate}", dateFormat.format(gracePeriodEndDate));
        template.variable("${paymentMethodLink}", AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.resident, true), true,
                ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCard.class));

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createTenantSurePaymentsResumedEmail() {
        MailMessage email = new MailMessage();
        email.setSender(getTenantSureSender());
        email.setSubject(i18n.tr("Payment Processing Resumed"));

        MessageTemplate template = new MessageTemplate("email/tenantsure/tenantsure-payments-resumed.html");

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

}
