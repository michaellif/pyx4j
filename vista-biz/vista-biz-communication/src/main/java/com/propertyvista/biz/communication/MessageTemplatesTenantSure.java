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
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
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
        email.setSubject(i18n.tr("15 Day Notice of Cancellation for Non-Payment of Premium"));

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

    public static MailMessage createTenantSureRenewalEmail(TenantSureInsurancePolicy policy) {
        MailMessage email = new MailMessage();
        email.setSender(getTenantSureSender());
        email.setSubject(i18n.tr("RENEWAL OF TENANTSURE RENTERS INSURANCE"));

        MessageTemplate template = new MessageTemplate("email/tenantsure/tenantsure-renewal-notice.html");

        DateFormat dateFormat = new SimpleDateFormat(i18n.tr("yyyy-MM-dd"));

        template.variable("${certificateNumber}", policy.renewalOf().certificate().insuranceCertificateNumber().getValue());
        template.variable("${inceptionDate}", dateFormat.format(policy.certificate().inceptionDate().getValue()));

        template.variable("${annualPremium}", policy.annualPremium().getValue());
        template.variable("${underwriterFee}", policy.underwriterFee().getValue());
        template.variable("${brokerFee}", policy.brokerFee().getValue());
        template.variable("${totalAnnualTax}", policy.totalAnnualTax().getValue());
        template.variable("${totalMonthlyPayable}", policy.totalMonthlyPayable().getValue());
        template.variable("${totalFirstPayable}", policy.totalFirstPayable().getValue());

        template.variable("${portalLink}", AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.resident, true), true,
                ResidentPortalSiteMap.ResidentServices.class));

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createTenantSureCCExpiringEmail(Person tenant, String ccLastDigits, LogicalDate ccExpiry) {
        MailMessage email = new MailMessage();
        email.setSender(getTenantSureSender());
        email.setSubject(i18n.tr("TenantSure Policy: Credit Card Expiring Notice"));

        MessageTemplate template = new MessageTemplate("email/tenantsure/tenantsure-credit-card-expiring.html");

        template.variable("${tenant}", tenant.name().getStringView());
        template.variable("${expiryDate}", new SimpleDateFormat("MMMM yyyy").format(ccExpiry));
        template.variable("${lastDigits}", ccLastDigits);
        template.variable("${paymentMethodLink}", AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.resident, true), true,
                ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCard.class));

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }
}
