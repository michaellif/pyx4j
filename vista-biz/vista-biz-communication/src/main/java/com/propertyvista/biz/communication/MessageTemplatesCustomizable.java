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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.communication.mail.template.EmailTemplateManager;
import com.propertyvista.biz.communication.mail.template.EmailTemplateRootObjectLoader;
import com.propertyvista.biz.communication.mail.template.model.EmailTemplateContext;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.EmailTemplatesPolicy;
import com.propertyvista.domain.policy.policies.domain.EmailTemplate;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

class MessageTemplatesCustomizable {

    private final static I18n i18n = I18n.get(MessageTemplatesCustomizable.class);

    private final static Logger log = LoggerFactory.getLogger(MessageTemplatesCustomizable.class);

    private static String getSender() {
        return ServerSideConfiguration.instance().getApplicationEmailSender();
    }

    /**
     * Warning: can return <code>null</code> if the policy is not found.
     * 
     * @param type
     * @param building
     * @return
     */
    private static EmailTemplate getEmailTemplate(EmailTemplateType type, PolicyNode policyNode) {
        EmailTemplatesPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, EmailTemplatesPolicy.class).duplicate();
        EmailTemplate template = fetchEmailTemplate(type, policy);
        if (template == null) {
            //TODO hierarchical load with found item.
            policy = ServerSideFactory
                    .create(PolicyFacade.class)
                    .obtainEffectivePolicy(Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class)),
                            EmailTemplatesPolicy.class).duplicate();
            return fetchEmailTemplate(type, policy);
        } else {
            return template;
        }
    }

    static EmailTemplate getEmailTemplate(EmailTemplateType type, BillingAccount billingAccountId) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().units().$().leases().$().billingAccount(), billingAccountId);
        return getEmailTemplate(type, Persistence.service().retrieve(criteria, AttachLevel.IdOnly));
    }

    static EmailTemplate getEmailTemplate(EmailTemplateType type, Lease leaseId) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().units().$().leases(), leaseId);
        return getEmailTemplate(type, Persistence.service().retrieve(criteria, AttachLevel.IdOnly));
    }

    private static EmailTemplate fetchEmailTemplate(EmailTemplateType type, EmailTemplatesPolicy policy) {
        for (EmailTemplate emt : policy.templates()) {
            if (emt.type().getValue() == type)
                return emt;
        }
        return null;
    }

    public static MailMessage createProspectWelcome(LeaseTermTenant tenantInLease) {
        Persistence.ensureRetrieve(tenantInLease.application(), AttachLevel.Attached);
        Persistence.ensureRetrieve(tenantInLease.application().masterOnlineApplication(), AttachLevel.Attached);

        EmailTemplate emailTemplate;

        if (tenantInLease.application().masterOnlineApplication().building().isNull()) {
            emailTemplate = getEmailTemplate(EmailTemplateType.ProspectWelcome,
                    Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class)));
        } else {
            emailTemplate = getEmailTemplate(EmailTemplateType.ProspectWelcome, tenantInLease.application().masterOnlineApplication().building());
        }

        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.leaseTermParticipant().set(tenantInLease);

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(EmailTemplateType.ProspectWelcome)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        MailMessage email = new MailMessage();
        Persistence.ensureRetrieve(tenantInLease.leaseParticipant().customer().user(), AttachLevel.Attached);
        email.setTo(tenantInLease.leaseParticipant().customer().user().email().getValue());
        email.setSender(getSender());
        buildEmail(email, emailTemplate, data);
        return email;
    }

    public static MailMessage createApplicationStatusEmail(LeaseTermTenant tenantInLease, EmailTemplateType type) {
        // get building policy node
        Persistence.service().retrieve(tenantInLease.leaseTermV());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit());
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease().unit().building());
        EmailTemplate emailTemplate = getEmailTemplate(type, tenantInLease.leaseTermV().holder().lease().unit().building());

        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        // populate context properties required by template type
        context.leaseTermParticipant().set(tenantInLease);
        context.lease().set(tenantInLease.leaseTermV().holder().lease());

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(type)) {
            // ObjectLoader will load required T-Objects using context data
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        MailMessage email = new MailMessage();
        CustomerUser user = tenantInLease.leaseParticipant().customer().user();
        if (user.isValueDetached()) {
            Persistence.service().retrieve(tenantInLease.leaseParticipant().customer().user());
        }
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createTenantInvitationEmail(LeaseTermParticipant<?> leaseParticipant, EmailTemplateType emailType, String token) {
        Persistence.ensureRetrieve(leaseParticipant.leaseTermV().holder().lease().unit().building(), AttachLevel.Attached);
        Persistence.service().retrieve(leaseParticipant.leaseParticipant().customer().user());

        EmailTemplate emailTemplate = getEmailTemplate(emailType, leaseParticipant.leaseTermV().holder().lease().unit().building());

        // create required data context
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.user().set(leaseParticipant.leaseParticipant().customer().user());
        context.lease().set(leaseParticipant.leaseTermV().holder().lease());
        context.leaseTermParticipant().set(leaseParticipant);
        context.accessToken().setValue(token);
        // load data objects for template variable lookup
        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(emailType)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        MailMessage email = new MailMessage();
        email.setTo(leaseParticipant.leaseParticipant().customer().user().email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);
        return email;
    }

    public static MailMessage createCustomerPasswordResetEmail(EmailTemplateType templateType, AbstractUser user, String token) {
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.accessToken().setValue(token);
        context.user().set(user);

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        EmailTemplate emailTemplate = null;

        // get company policy node
        EntityQueryCriteria<OrganizationPoliciesNode> nodeCrit = EntityQueryCriteria.create(OrganizationPoliciesNode.class);
        PolicyNode policyNode = Persistence.service().retrieve(nodeCrit);
        // get building policy node form the first available TenantInLease entry
        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.eq(criteria.proto().leaseParticipant().customer().user(), user);
        criteria.eq(criteria.proto().leaseTermV().holder(), criteria.proto().leaseTermV().holder().lease().currentTerm());
        criteria.isCurrent(criteria.proto().leaseTermV());

        // TODO Fix this!
        LeaseTermTenant til = Persistence.service().retrieve(criteria);
        if (til != null) {
            Persistence.ensureRetrieve(til.leaseTermV().holder().lease().unit().building(), AttachLevel.Attached);

            if (!til.leaseTermV().holder().lease().unit().building().isNull()) {
                policyNode = til.leaseTermV().holder().lease().unit().building();
            }
        }

        emailTemplate = getEmailTemplate(templateType, policyNode);
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(templateType)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }

        MailMessage email = new MailMessage();
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createCrmPasswordResetEmail(AbstractUser user, String token) {
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.accessToken().setValue(token);
        context.user().set(user);

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        // get company policy node
        EntityQueryCriteria<OrganizationPoliciesNode> nodeCrit = EntityQueryCriteria.create(OrganizationPoliciesNode.class);
        PolicyNode policyNode = Persistence.service().retrieve(nodeCrit);

        EmailTemplate emailTemplate = getEmailTemplate(EmailTemplateType.PasswordRetrievalCrm, policyNode);
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(EmailTemplateType.PasswordRetrievalCrm)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }

        MailMessage email = new MailMessage();
        email.setTo(user.email().getValue());
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);

        return email;
    }

    private static MailMessage createTenantPayment(EmailTemplateType type, PaymentRecord paymentRecord) {
        Persistence.ensureRetrieve(paymentRecord.leaseTermParticipant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(paymentRecord.leaseTermParticipant().leaseParticipant().customer().user(), AttachLevel.Attached);
        String customerEmail = paymentRecord.leaseTermParticipant().leaseParticipant().customer().user().email().getValue();
        if (customerEmail == null) {
            //Do not send payment email when there are no email in tenant profile
            return null;
        }
        MailMessage email = new MailMessage();
        email.setTo(customerEmail);

        EmailTemplate emailTemplate = getEmailTemplate(type, paymentRecord.billingAccount());

        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.paymentRecord().set(paymentRecord);
        context.leaseTermParticipant().set(paymentRecord.leaseTermParticipant());

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(type)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }

        email.setSender(getSender());
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createTenantOneTimePaymentSubmitted(PaymentRecord paymentRecord) {
        return createTenantPayment(EmailTemplateType.OneTimePaymentSubmitted, paymentRecord);
    }

    public static MailMessage createTenantPaymenttRejected(PaymentRecord paymentRecord, boolean applyNSF) {
        return createTenantPayment(EmailTemplateType.PaymentReturned, paymentRecord);
    }

    public static MailMessage createTenantPaymentCleared(PaymentRecord paymentRecord) {
        if (paymentRecord.convenienceFee().isNull()) {
            return createTenantPayment(EmailTemplateType.PaymentReceipt, paymentRecord);
        } else {
            return createTenantPayment(EmailTemplateType.PaymentReceiptWithWebPaymentFee, paymentRecord);
        }
    }

    public static MailMessage createTenantAutopaySetupCompleted(AutopayAgreement autopayAgreement) {
        Persistence.ensureRetrieve(autopayAgreement.tenant(), AttachLevel.Attached);
        Persistence.ensureRetrieve(autopayAgreement.tenant().customer().user(), AttachLevel.Attached);
        String customerEmail = autopayAgreement.tenant().customer().user().email().getValue();
        if (customerEmail == null) {
            //Do not send payment email when there are no email in tenant profile
            return null;
        }
        MailMessage email = new MailMessage();
        email.setTo(customerEmail);

        EmailTemplate emailTemplate = getEmailTemplate(EmailTemplateType.AutoPaySetupConfirmation, autopayAgreement.tenant().lease());

        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.preauthorizedPayment().set(autopayAgreement);
        context.leaseParticipant().set(autopayAgreement.tenant());
        context.lease().set(autopayAgreement.tenant().lease());

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(EmailTemplateType.AutoPaySetupConfirmation)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }

        email.setSender(getSender());
        buildEmail(email, emailTemplate, data);

        return email;
    }

    public static MailMessage createMaintenanceRequestEmail(EmailTemplateType emailType, MaintenanceRequest request) {
        EmailTemplate emailTemplate = getEmailTemplate(emailType, request.building());

        // create required data context
        EmailTemplateContext context = EntityFactory.create(EmailTemplateContext.class);
        context.maintenanceRequest().set(request);

        // load data objects for template variable lookup
        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : EmailTemplateManager.getTemplateDataObjects(emailType)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, context));
        }
        MailMessage email = new MailMessage();
        email.setSender(getSender());
        // set email subject and body from the template
        buildEmail(email, emailTemplate, data);
        return email;
    }

    private static String bodyRaw;

    public static String getEmailHTMLBody() {
        if (bodyRaw == null) {
            try {
                bodyRaw = IOUtils.getTextResource("email/template-basic-body.html");
            } catch (IOException e) {
                throw new Error("Unable to load template html wrapper resource", e);
            }
        }
        return bodyRaw;
    }

    private static void buildEmail(MailMessage email, EmailTemplate emailTemplate, Collection<IEntity> data) {
        email.setSubject(EmailTemplateManager.parseTemplate(emailTemplate.subject().getValue(), data));

        Object contentHtml = EmailTemplateManager.parseTemplate(emailTemplate.content().getValue(), data);

        Object headerHtml;
        Object footerHtml;

        if (emailTemplate.useHeader().isBooleanTrue()) {
            headerHtml = EmailTemplateManager.parseTemplate(emailTemplate.policy().header().getValue(), data);
        } else {
            headerHtml = "";
        }

        if (emailTemplate.useHeader().isBooleanTrue()) {
            footerHtml = EmailTemplateManager.parseTemplate(emailTemplate.policy().footer().getValue(), data);
        } else {
            footerHtml = "";
        }

        email.setHtmlBody(SimpleMessageFormat.format(getEmailHTMLBody(),//@formatter:off
                headerHtml,
                contentHtml,
                footerHtml
            ));//@formatter:on
    }

}
