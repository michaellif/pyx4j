/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 10, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.server.mail.MessageTemplate;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;

class MessageTemplatesCrmNotification {

    private final static I18n i18n = I18n.get(MessageTemplatesCrmNotification.class);

    private static String wrapperTextResourceName = "email/notification/template-body-crm.html";

    private static String getSender() {
        return ServerSideConfiguration.instance().getApplicationEmailSender();
    }

    public static MailMessage createNewPmcEmail(OnboardingUser user, Pmc pmc) {
        MailMessage email = new MailMessage();
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        email.setSubject(i18n.tr("New PMC Created"));

        MessageTemplate template = new MessageTemplate("email/notification/new-pmc.html");
        template.variable("${ownerName}", user.firstName().getValue());
        template.variable("${crmLink}", VistaDeployment.getBaseApplicationURL(pmc, VistaApplication.crm, true));
        template.variable("${portalLink}", VistaDeployment.getBaseApplicationURL(pmc, VistaApplication.resident, true));

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createOnlinePaymentSetupCompletedEmail(String userName) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        email.setSubject(i18n.tr("Your Online Payment Setup Is Complete"));

        MessageTemplate template = new MessageTemplate("email/notification/online-payment-setup-completed.html");
        template.variable("${userName}", userName);

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createPaymentRejectedNotificationEmail(PaymentRecord paymentRecord, boolean applyNSF) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());

        String crmUrl = VistaDeployment.getBaseApplicationURL(VistaDeployment.getCurrentPmc(), VistaApplication.crm, true);
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, paymentRecord.billingAccount().getPrimaryKey());

        Persistence.service().retrieve(billingAccount.lease());
        String leaseId = billingAccount.lease().leaseId().getValue();
        String leaseUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Tenants.Lease().formViewerPlace(billingAccount.lease().getPrimaryKey()));

        Persistence.ensureRetrieve(paymentRecord.leaseTermParticipant(), AttachLevel.Attached);

        String tenantUrl = AppPlaceInfo.absoluteUrl(crmUrl, true,
                new CrmSiteMap.Tenants.Tenant().formViewerPlace(paymentRecord.leaseTermParticipant().leaseParticipant().getPrimaryKey()));
        String tenantId = paymentRecord.leaseTermParticipant().leaseParticipant().participantId().getStringView();

        String tenantName = paymentRecord.leaseTermParticipant().leaseParticipant().customer().person().name().getStringView();

        String unitId = billingAccount.lease().unit().info().number().getValue();
        Persistence.service().retrieve(billingAccount.lease().unit().building(), AttachLevel.ToStringMembers, false);
        String buildingId = billingAccount.lease().unit().building().getStringView();

        String paymentRecordUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Finance.Payment().formViewerPlace(paymentRecord.getPrimaryKey()));
        String paymentId = paymentRecord.getPrimaryKey().toString();
        String paymentAmount = i18n.tr("${0,number,#,##0.00}", paymentRecord.amount().getValue());

        String rejectionReason = i18n.tr("UNKNOWN");
        if (!paymentRecord.transactionErrorMessage().isNull()) {
            rejectionReason = paymentRecord.transactionErrorMessage().getValue();
        }

        email.setSubject(i18n.tr("Payment Rejected Alert for Building {0}, Unit {1}, Lease {2}, Tenant {3} {4}", buildingId, unitId, leaseId, tenantId,
                tenantName));

        email.addKeywords(paymentRecord.id().getStringView());
        email.addKeywords(buildingId);
        email.addKeywords(leaseId);
        email.addKeywords(tenantId);

        MessageTemplate template = new MessageTemplate("email/notification/payment-rejected-notification.html");

        template.variable("${buildingId}", buildingId);
        template.variable("${unitId}", unitId);
        template.variable("${leaseId}", leaseId);
        template.variable("${leaseUrl}", leaseUrl);
        template.variable("${tenantId}", tenantId);
        template.variable("${tenantName}", tenantName);
        template.variable("${tenantUrl}", tenantUrl);
        template.variable("${paymentId}", paymentId);
        template.variable("${paymentAmount}", paymentAmount);
        template.variable("${paymentUrl}", paymentRecordUrl);
        template.variable("${rejectionReason}", rejectionReason);
        template.variable("${notes}", applyNSF ? i18n.tr("NSF fee will be applied.") : "");

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createPostToYardiFailedNotificationEmail(PaymentRecord paymentRecord, boolean applyNSF, String yardiErrorMessage) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());

        String crmUrl = VistaDeployment.getBaseApplicationURL(VistaDeployment.getCurrentPmc(), VistaApplication.crm, true);
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, paymentRecord.billingAccount().getPrimaryKey());

        Persistence.service().retrieve(billingAccount.lease());
        String leaseId = billingAccount.lease().leaseId().getValue();
        String leaseUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Tenants.Lease().formViewerPlace(billingAccount.lease().getPrimaryKey()));

        Persistence.ensureRetrieve(paymentRecord.leaseTermParticipant(), AttachLevel.Attached);

        String tenantUrl = AppPlaceInfo.absoluteUrl(crmUrl, true,
                new CrmSiteMap.Tenants.Tenant().formViewerPlace(paymentRecord.leaseTermParticipant().leaseParticipant().getPrimaryKey()));
        String tenantId = paymentRecord.leaseTermParticipant().leaseParticipant().participantId().getStringView();

        String tenantName = paymentRecord.leaseTermParticipant().leaseParticipant().customer().person().name().getStringView();

        String unitId = billingAccount.lease().unit().info().number().getValue();
        Persistence.service().retrieve(billingAccount.lease().unit().building(), AttachLevel.ToStringMembers, false);
        String buildingId = billingAccount.lease().unit().building().getStringView();

        String paymentRecordUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Finance.Payment().formViewerPlace(paymentRecord.getPrimaryKey()));
        String paymentId = paymentRecord.getPrimaryKey().toString();
        String paymentAmount = i18n.tr("${0,number,#,##0.00}", paymentRecord.amount().getValue());

        String rejectionReason = i18n.tr("UNKNOWN");
        if (!paymentRecord.transactionErrorMessage().isNull()) {
            rejectionReason = paymentRecord.transactionErrorMessage().getValue();
        }

        email.setSubject(i18n.tr("NSF Alert for Building {0}, Unit {1}, Lease {2}, Tenant {3} {4} -  failed to post into Yardi, needs to be posted manually",
                buildingId, unitId, leaseId, tenantId, tenantName));

        email.addKeywords(paymentRecord.id().getStringView());
        email.addKeywords(buildingId);
        email.addKeywords(leaseId);
        email.addKeywords(tenantId);

        MessageTemplate template = new MessageTemplate("email/notification/payment-post-to-yardi-failed-notification.html");

        template.variable("${buildingId}", buildingId);
        template.variable("${unitId}", unitId);
        template.variable("${leaseId}", leaseId);
        template.variable("${leaseUrl}", leaseUrl);
        template.variable("${tenantId}", tenantId);
        template.variable("${tenantName}", tenantName);
        template.variable("${tenantUrl}", tenantUrl);
        template.variable("${paymentId}", paymentId);
        template.variable("${paymentAmount}", paymentAmount);
        template.variable("${paymentUrl}", paymentRecordUrl);
        template.variable("${rejectionReason}", rejectionReason);
        template.variable("${yardiErrorMessage}", yardiErrorMessage);

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createAutoPayReviewRequiredNotificationEmail(List<Lease> leaseIds) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());

        MessageTemplate template = new MessageTemplate("email/notification/autopay-review-required-notification.html");
        {
            Lease lease = Persistence.service().retrieve(Lease.class, leaseIds.get(0).getPrimaryKey());
            Building building;
            {
                EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                criteria.eq(criteria.proto().units(), lease.unit());
                building = Persistence.service().retrieve(criteria);
            }

            String buildingName = building.info().name().getStringView();
            if (StringUtils.isEmpty(buildingName)) {
                buildingName = building.propertyCode().getStringView();
            }

            template.variable("${buildingName}", buildingName);
            template.variable("${buildingAddress}", building.info().address().getStringView());

            if (leaseIds.size() == 1) {
                email.setSubject(i18n.tr("Auto Pay Review Required for lease {0}, building {0}", lease, buildingName));
            } else {
                email.setSubject(i18n.tr("Auto Pay Review Required for building {0}", buildingName));
            }
            email.addKeywords(building.propertyCode().getStringView());
        }

        String crmUrl = VistaDeployment.getBaseApplicationURL(VistaDeployment.getCurrentPmc(), VistaApplication.crm, true);
        StringBuilder leaseLinks = new StringBuilder();
        for (Lease leaseId : leaseIds) {
            String leaseUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Tenants.Lease().formViewerPlace(leaseId.getPrimaryKey()));
            Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
            if (leaseLinks.length() > 0) {
                leaseLinks.append("<p/>");
            }
            leaseLinks.append("<a href=\"" + leaseUrl + "\">" + lease.getStringView() + "</a>");

            email.addKeywords(lease.id().getStringView());
            email.addKeywords(lease.leaseId().getStringView());
        }

        template.variable("${leaseLinks}", leaseLinks);
        template.variable("${autoPaysReviewLink}", AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Finance.AutoPayReview()));

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createAutoPayCancelledBySystemNotificationEmail(List<Lease> leaseIds, Map<Lease, List<AutopayAgreement>> canceledAgreements) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());

        MessageTemplate template = new MessageTemplate("email/notification/autopay-cancelled-by-system-notification.html");
        {
            Lease lease = Persistence.service().retrieve(Lease.class, leaseIds.get(0).getPrimaryKey());
            Building building;
            {
                EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                criteria.eq(criteria.proto().units(), lease.unit());
                building = Persistence.service().retrieve(criteria);
            }

            String buildingName = building.info().name().getStringView();
            if (StringUtils.isEmpty(buildingName)) {
                buildingName = building.propertyCode().getStringView();
            }

            template.variable("${buildingName}", buildingName);
            template.variable("${buildingAddress}", building.info().address().getStringView());

            if (leaseIds.size() == 1) {
                email.setSubject(i18n.tr("Auto Pay Cancelled for lease {0}, building {0}", lease, buildingName));
            } else {
                email.setSubject(i18n.tr("Auto Pay Cancelled in building {0}", buildingName));
            }
            email.addKeywords(building.propertyCode().getStringView());
        }

        String crmUrl = VistaDeployment.getBaseApplicationURL(VistaDeployment.getCurrentPmc(), VistaApplication.crm, true);
        StringBuilder leaseLinks = new StringBuilder();
        for (Lease leaseId : leaseIds) {
            String leaseUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Tenants.Lease().formViewerPlace(leaseId.getPrimaryKey()));
            Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
            if (leaseLinks.length() > 0) {
                leaseLinks.append("<p/>");
            }
            leaseLinks.append("<a href=\"" + leaseUrl + "\">" + lease.getStringView() + "</a>");
            for (AutopayAgreement autopayAgreement : canceledAgreements.get(leaseId)) {
                String agreementUrl = AppPlaceInfo
                        .absoluteUrl(crmUrl, true, new CrmSiteMap.Finance.AutoPay().formViewerPlace(autopayAgreement.getPrimaryKey()));
                leaseLinks.append(" <a href=\"" + agreementUrl + "\">Agreement ID" + autopayAgreement.getPrimaryKey() + "</a>");
            }

            email.addKeywords(lease.id().getStringView());
            email.addKeywords(lease.leaseId().getStringView());
        }
        template.variable("${leaseLinks}", leaseLinks);

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }

    public static MailMessage createAutoPayCancelledByResidentNotificationEmail(Lease leaseId, List<AutopayAgreement> canceledAgreements) {
        MailMessage email = new MailMessage();
        email.setSender(getSender());

        MessageTemplate template = new MessageTemplate("email/notification/autopay-cancelled-by-resident-notification.html");
        {
            Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
            Building building;
            {
                EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                criteria.eq(criteria.proto().units(), lease.unit());
                building = Persistence.service().retrieve(criteria);
            }

            String buildingName = building.info().name().getStringView();
            if (StringUtils.isEmpty(buildingName)) {
                buildingName = building.propertyCode().getStringView();
            }

            template.variable("${buildingName}", buildingName);
            template.variable("${buildingAddress}", building.info().address().getStringView());

            email.setSubject(i18n.tr("Auto Pay Cancelled by Resident for lease {0}, building {0}", lease, buildingName));

            email.addKeywords(lease.id().getStringView());
            email.addKeywords(lease.leaseId().getStringView());
            email.addKeywords(building.propertyCode().getStringView());
        }

        String crmUrl = VistaDeployment.getBaseApplicationURL(VistaDeployment.getCurrentPmc(), VistaApplication.crm, true);
        StringBuilder leaseLinks = new StringBuilder();
        {
            String leaseUrl = AppPlaceInfo.absoluteUrl(crmUrl, true, new CrmSiteMap.Tenants.Lease().formViewerPlace(leaseId.getPrimaryKey()));
            Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
            if (leaseLinks.length() > 0) {
                leaseLinks.append("<p/>");
            }
            leaseLinks.append("<a href=\"" + leaseUrl + "\">" + lease.getStringView() + "</a>");
            for (AutopayAgreement autopayAgreement : canceledAgreements) {
                String agreementUrl = AppPlaceInfo
                        .absoluteUrl(crmUrl, true, new CrmSiteMap.Finance.AutoPay().formViewerPlace(autopayAgreement.getPrimaryKey()));
                leaseLinks.append(" <a href=\"" + agreementUrl + "\">Agreement ID" + autopayAgreement.getPrimaryKey() + "</a>");
            }
        }
        template.variable("${leaseLinks}", leaseLinks);

        email.setHtmlBody(template.getWrappedBody(wrapperTextResourceName));
        return email;
    }
}
